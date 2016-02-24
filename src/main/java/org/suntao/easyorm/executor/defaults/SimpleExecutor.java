package org.suntao.easyorm.executor.defaults;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.log4j.Logger;
import org.suntao.easyorm.executor.Executor;
import org.suntao.easyorm.map.MapStatment;
import org.suntao.easyorm.map.ResultMapping;
import org.suntao.easyorm.map.ResultMappingType;
import org.suntao.easyorm.session.SqlSession;

/**
 * 简化sql解释器
 * 
 * @author suntao
 *
 */
public class SimpleExecutor implements Executor {
	/**
	 * 数据库连接
	 */
	private Connection conn;
	/**
	 * 结果映射器
	 */
	private ResultMapping mapping;
	private SqlSession sqlSession;
	private static Logger logger = Logger.getLogger(Executor.class);

	public SimpleExecutor(SqlSession sqlSession, ResultMapping mapping) {
		super();
		this.sqlSession = sqlSession;
		this.mapping = mapping;
	}

	@Override
	public Object execute(MapStatment mapStatment, Object[] params) {
		Object result = null;
		/**
		 * 执行后返回的结果集(如果有)
		 */
		ResultSet resultSet = null;
		/**
		 * 数据库连接
		 */
		conn = sqlSession.getConnection();
		/**
		 * 影响行数
		 */
		int influRows = -1;
		/**
		 * Sql语句
		 */
		String sqlStr = mapStatment.getStatmentSQL();

		if (sqlStr == null || sqlStr.isEmpty()) {
			logger.warn(String.format("请使用注解或者xml配置文件为方法%s配置Sql语句,并确认Sql语句不为空",
					mapStatment.getId()));
		} else {
			/**
			 * 在此处使用preparedStatment完成查询 <br>
			 * 获得ResultSet之后使用Mapping完成映射
			 */
			try {
				PreparedStatement preparedStatement = conn
						.prepareStatement(sqlStr);
				// 将params数组填入preparedStatment中
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
						} else if (currentParam instanceof Float) {
							preparedStatement.setFloat(index,
									(Float) currentParam);
						} else {
							logger.error(String.format("传入的参数错误,当前不可接受%s类型的参数",
									currentParam.getClass().getName()));
						}
					}
				}
				preparedStatement.execute();// 执行语句
				/**
				 * 获取结果集<br>
				 * 如果不是select语句则返回null
				 */
				resultSet = preparedStatement.getResultSet();
				influRows = preparedStatement.getUpdateCount();// 获取影响行数
				boolean isSelect = false;// 是否是Select 流程
				if (resultSet == null)
					isSelect = true;
				else
					isSelect = false;

				// 如若不是select语句,则无法得到ResultSet
				if (isSelect) {
					logger.debug("UPDATE/INSERT/DELETE流程");
					ResultMappingType resultMappingType = mapStatment
							.getResultMap().getResultType();
					switch (resultMappingType) {
					case BOOLEAN:
						if (influRows > 0)
							result = true;
						else
							result = false;
						logger.debug("查询返回布尔值," + result);
						break;
					case INTEGER:
						result = influRows;
						logger.debug("查询返回整型值," + result);
						break;
					default:
						logger.warn("请注意非select SQL仅可使用int和boolean两种返回类型");
						result = null;
						break;
					}
				} else {
					// 如果是select语句则对结果进行映射
					logger.debug("SELECT流程");
					result = mapping.mapObject(mapStatment.getResultMap(),
							resultSet);
				}
				preparedStatement.close();
			} catch (SQLException e) {
				logger.error("SQL发生错误,请确认SQL语句是否正确,列名,关键字,参数是否正确\n" + e);
			} catch (Exception e) {
				logger.error("出现了非SQL Exception:");
				e.printStackTrace();
			}
		}
		sqlSession.returnConnection(conn);
		return result;
	}
}
