package org.suntao.easyorm.executor.defaults;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.suntao.easyorm.executor.Executor;
import org.suntao.easyorm.map.MapStatment;
import org.suntao.easyorm.map.ResultMapping;
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
		ResultSet resultSet = null;
		conn = sqlSession.getConnection();
		/**
		 * 在此处使用preparedStatment完成查询 <br>
		 * 获得ResultSet之后使用Mapping完成映射
		 */
		try {
			PreparedStatement preparedStatement = conn
					.prepareStatement(mapStatment.getStatmentSQL());
			if (params != null) {
				for (int index = 1; index <= params.length; index++) {
					Object currentParam = params[index - 1];
					if (currentParam instanceof Integer) {
						preparedStatement.setInt(index, (int) currentParam);
					} else if (currentParam instanceof String) {
						preparedStatement.setString(index,
								(String) currentParam);
					} else if (currentParam instanceof Date) {
						preparedStatement.setDate(index, (Date) currentParam);
					} else {
						logger.error(String.format("传入的参数错误,当前不可接受%s类型的参数",
								currentParam.getClass().getName()));
					}
				}
			}
			resultSet = preparedStatement.executeQuery();
			result = mapping.mapObject(mapStatment.getResultMap(), resultSet);
			preparedStatement.close();
		} catch (SQLException e) {
			logger.error("SQL发生错误,有可能是参数和Sql语句不对应");
			e.printStackTrace();
		}
		sqlSession.returnConnection(conn);
		return result;
	}

}
