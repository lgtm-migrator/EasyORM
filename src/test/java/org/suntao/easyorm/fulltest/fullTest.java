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
		List<courseinfo> courses = sqlSession.selectALL(courseinfo.class);
		sqlSession.selectALL(courseinfo.class);
		courseinfo c = new courseinfo();
		c.courseid = 17;
		c.classhour = 64;
		c.course = "测试名称修改4";
		c.score = (float) 3.75;
		c.teacherid = 100096;
		System.out.println(sqlSession.updateByPrimaryKey(c));
		for (courseinfo ci : courses) {
			System.out.println(ci);
		}
		System.out.println("ID 17 信息为:" + sqlSession.selectByPrimaryKey(c));
		
		
	}
}
