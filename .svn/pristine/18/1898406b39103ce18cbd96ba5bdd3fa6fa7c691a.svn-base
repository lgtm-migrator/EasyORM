package org.suntao.easyorm.session;

import java.sql.Connection;

/**
 * SqlSession连接
 * 
 * @author suntao
 *
 */
public interface SqlSession {
	/**
	 * 获取数据库连接
	 * 
	 * @return
	 */
	Connection getConnection();

	/**
	 * 获取Mapper
	 * <p>
	 * 使用代理方法 通过接口 调用executor
	 * 
	 * @param mapperClass
	 * @return
	 */
	<T> T getMapper(Class<T> mapperClass);

	/**
	 * 返回一个连接
	 * 
	 * @param conn
	 */
	void returnConnection(Connection conn);
}
