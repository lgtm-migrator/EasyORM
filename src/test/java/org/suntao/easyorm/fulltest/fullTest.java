package org.suntao.easyorm.fulltest;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.suntao.easyorm.session.SqlSession;
import org.suntao.easyorm.session.SqlSessionFactory;
import org.suntao.easyorm.session.defaults.DefaultSqlSessionFactory;
import org.suntao.easyorm.testDAO.courseinfoMapper;
import org.suntao.easyorm.xmlparse.EasyormConfig;

public class fullTest {
	SqlSessionFactory sqlSessionFactory;
	SqlSession sqlSession;
	courseinfoMapper courseinfomapper;

	@Before
	public void setUp() throws Exception {
		sqlSessionFactory = new DefaultSqlSessionFactory(this.getClass()
				.getClassLoader().getResource("xmltest.xml").getPath());
		sqlSession = sqlSessionFactory.openSession();
		courseinfomapper = sqlSession.getMapper(courseinfoMapper.class);
	}

	@Test
	public void testList() {
		List<courseinfo> courses = courseinfomapper.selectAll();
		System.out.println(courseinfomapper.update());
		for (courseinfo c : courses) {
			System.out.println(c);
		}
	}
}
