package org.suntao.easyorm.session;

import java.sql.Connection;
import java.util.List;

/**
 * SqlSession连接
 * 
 * @author suntao
 *
 */
public interface SqlSession {
	/**
	 * 获取一个数据库连接
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
	 * 如果使用数据连接池,就将该Connection重新放入池中<br>
	 * 请不要在返回连接之后继续使用它
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
	Integer updateByPrimaryKey(Object obj);

	/**
	 * 插入一条数据
	 * <p>
	 * 成功执行主要流程返回值代表影响条数<br>
	 * 主要流程执行失败将返回-1
	 * 
	 * @param obj
	 * @return
	 */
	Integer insert(Object obj);

	/**
	 * 删除一条数据
	 * <p>
	 * 只需填充对象id属性
	 * <p>
	 * 成功执行主要流程返回值代表影响条数<br>
	 * 主要流程执行失败将返回-1
	 * 
	 * @param obj
	 * @return
	 */
	Integer deleteByPrimaryKey(Object obj);

	/**
	 * 根据主键查询一条数据
	 * <p>
	 * 主键唯一
	 * 
	 * @param obj
	 *            对象,应该包含一个id
	 * @return 对象所对应的实体
	 */
	<T> T selectByPrimaryKey(T obj);

	/**
	 * 获取该表所有数据并映射为实体列表
	 * 
	 * @param modelClass
	 *            数据库实体的类
	 * @return 数据库实体列表
	 */
	<T> List<T> selectALL(Class<T> modelClass);
}
