package org.suntao.easyorm.fulltest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DebugGraphics;

import org.junit.Before;
import org.junit.Test;
import org.suntao.easyorm.scan.Scanner;
import org.suntao.easyorm.scan.SimpleScanner;
import org.suntao.easyorm.session.SqlSession;
import org.suntao.easyorm.session.SqlSessionFactory;
import org.suntao.easyorm.session.defaults.DefaultSqlSession;
import org.suntao.easyorm.session.defaults.DefaultSqlSessionFactory;
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
		for (courseinfo c : courses) {
			System.out.println(c);
		}

	}
}
