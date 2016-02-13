package org.suntao.easyorm.session;

/**
 * SqlSessionFactory
 * <p>
 * 用于创建SqlSession
 * 
 * @author suntao
 *
 */
public interface SqlSessionFactory {
	/**
	 * 打开一个SqlSession
	 * 
	 * @return
	 */
	SqlSession openSession();

}
