package org.suntao.easyorm.session;

public interface SqlSessionFactory {
	/**
	 * 打开一个SqlSession
	 * 
	 * @return
	 */
	SqlSession openSession();
	
}
