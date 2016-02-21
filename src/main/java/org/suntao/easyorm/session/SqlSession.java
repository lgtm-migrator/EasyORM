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
	 * 返回一个新的数据库连接
	 * <p>
	 * java.sql.Connection
	 * 
	 * @return 数据库连接
	 */
	Connection getConnection();

	/**
	 * 获取Mapper
	 * <p>
	 * 通过使用动态代理,可以调用DAO直接执行SQL<br>
	 * 返回的实际上是一个MapperProxy
	 * 
	 * @param mapperClass
	 * @return 一个mapper代理
	 */
	<T> T getMapper(Class<T> mapperClass);

	/**
	 * 返回一个连接到SqlSession留待处理
	 * <p>
	 * 通常有两种情况<br>
	 * 如果没有数据连接池,就关闭连接<br>
	 * 如果使用数据连接池,就将该Connection重新放入池中
	 * 
	 * @param conn
	 *            一个数据库连接
	 */
	void returnConnection(Connection conn);

	/**
	 * 根据id更新
	 * 
	 * @param obj
	 * @return
	 */
	Integer update(Object obj);

	/**
	 * 插入一条数据
	 * 
	 * @param obj
	 * @return
	 */
	Integer insert(Object obj);

	/**
	 * 删除一条数据
	 * 
	 * @param obj
	 * @return
	 */
	Integer delete(Object obj);
}
