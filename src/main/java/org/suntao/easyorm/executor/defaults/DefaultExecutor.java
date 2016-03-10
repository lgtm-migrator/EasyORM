package org.suntao.easyorm.executor.defaults;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Logger;

import org.suntao.easyorm.executor.Executor;
import org.suntao.easyorm.map.MapStatement;
import org.suntao.easyorm.map.ResultMapping;
import org.suntao.easyorm.map.ResultMappingType;
import org.suntao.easyorm.session.SqlSession;
import org.suntao.easyorm.utils.Utils;

/**
 * 简化sql解释器
 * 
 * @author suntao
 *
 */
public class DefaultExecutor implements Executor {
	/**
	 * 结果映射器
	 */
	private ResultMapping mapping;
	private SqlSession sqlSession;
	private static Logger logger = Logger.getLogger(DefaultExecutor.class.getName());

	/**
	 * 解释器实现
	 * 
	 * @param sqlSession
	 *            连接 <br>
	 *            用于创建连接和归还连接<br>
	 *            解释器自身并不持有连接
	 * @param mapping
	 *            映射器<br>
	 *            如果是查询流程,需要对结果进行映射
	 */
	public DefaultExecutor(SqlSession sqlSession, ResultMapping mapping) {
		super();
		this.sqlSession = sqlSession;
		this.mapping = mapping;
	}

	@Override
	public Object execute(MapStatement mapStatement, Object[] params)
			throws SQLException {
		Object result = null;// 返回对象
		ResultSet resultSet = null;// 结果集
		Connection conn = sqlSession.getConnection();// 连接
		int influRows = -1;// 影响行数
		String sqlStr = mapStatement.getStatmentSQL();// 执行语句
		// 是否可执行
		if (sqlStr == null || sqlStr.isEmpty()) {
			logger.warning(String.format("请使用注解或者xml配置文件为方法%s配置Sql语句,并确认Sql语句不为空",
					mapStatement.getId()));
		}// 准备执行
		else {
			try {
				PreparedStatement preparedStatement = conn
						.prepareStatement(sqlStr);
				// 将params数组填入preparedStatement中
				if (params != null) {
					for (int index = 1; index <= params.length; index++) {
						Object currentParam = params[index - 1];
						if (currentParam == null) {
							int type = Types.NULL;
							preparedStatement.setNull(index, type);
						} else if (currentParam instanceof Integer) {
							preparedStatement.setInt(index, (int) currentParam);
						} else if (currentParam instanceof String) {
							preparedStatement.setString(index,
									(String) currentParam);
						} else if (currentParam instanceof Date) {
							preparedStatement.setDate(index,
									(Date) currentParam);
						} else if (currentParam instanceof java.util.Date) {
							// 如果是java.util.date,转化成java.sql.date
							preparedStatement
									.setDate(
											index,
											Utils.convertSqlDate((java.util.Date) currentParam));
						} else if (currentParam instanceof Float) {
							preparedStatement.setFloat(index,
									(Float) currentParam);
						} else {
							logger.severe(String.format("传入的参数错误,当前不可接受%s类型的参数",
									currentParam.getClass().getName()));
						}
					}
				}
				/**
				 * isSelectProcedure<br>
				 * 是否是select流程<br>
				 * 根据execute是否返回ResultSet<br>
				 * 可以判断是否为查询流程
				 */
				boolean isSelectProcedure = preparedStatement.execute();// 执行语句
				resultSet = preparedStatement.getResultSet();// 结果集
				influRows = preparedStatement.getUpdateCount();// 影响行数
				if (!isSelectProcedure) {
					logger.info("UPDATE/INSERT/DELETE流程");
					ResultMappingType resultMappingType = mapStatement
							.getResultMap().getResultType();
					switch (resultMappingType) {
					case BOOLEAN:
						if (influRows > 0)
							result = true;
						else
							result = false;
						logger.info("查询返回布尔值," + result);
						break;
					case INTEGER:
						result = influRows;
						logger.info("查询返回整型值," + result);
						break;
					default:
						logger.warning("请注意,非select SQL仅可使用int和boolean两种返回类型");
						result = null;
						break;
					}
				}// 如果是查询流程则对结果进行映射
				else {
					logger.info("SELECT流程");
					result = mapping.mapObject(mapStatement.getResultMap(),
							resultSet);
				}
				preparedStatement.close();
			} catch (SQLException e) {
				logger.severe("SQL发生错误,请确认SQL语句是否正确,列名,关键字,参数是否正确\n" + e);
				throw e;
			} catch (Exception e) {
				logger.severe("出现了非SQL Exception:");
				e.printStackTrace();
			}
		}
		sqlSession.returnConnection(conn);
		return result;
	}
}
