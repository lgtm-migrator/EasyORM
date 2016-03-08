package org.suntao.easyorm.fulltest;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.suntao.easyorm.configuration.DatabaseConfig;
import org.suntao.easyorm.configuration.EasyormConfig;
import org.suntao.easyorm.session.SqlSession;
import org.suntao.easyorm.session.SqlSessionFactory;
import org.suntao.easyorm.session.defaults.DefaultSqlSessionFactory;
import org.suntao.easyorm.testDAO.courseinfoMapper;

public class fullTest {
	SqlSessionFactory sqlSessionFactory;
	SqlSession sqlSession;
	courseinfoMapper courseinfomapper;

	@Before
	public void setUp() throws Exception {
		DatabaseConfig databaseConfig = new DatabaseConfig(
				"com.mysql.jdbc.Driver", "jdbc:mysql://42.96.206.67:3306/stu",
				"stu", "admin");
		EasyormConfig easyormConfig = new EasyormConfig(databaseConfig, true,
				15);
		sqlSessionFactory = new DefaultSqlSessionFactory(easyormConfig);
		sqlSession = sqlSessionFactory.openSession();
		courseinfomapper = sqlSession.getMapper(courseinfoMapper.class);
	}

	@Test
	public void testList() {
		List<courseinfo> cs = sqlSession.selectALL(courseinfo.class);
		for(courseinfo c:cs){
			System.out.println(c);
		}
	}

	@After
	public void destroy() {
		sqlSession.destroy();

	}
}
