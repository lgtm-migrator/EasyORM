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
	DefaultSqlSession sqlSession;
	courseinfoMapper courseinfomapper;

	@Before
	public void setUp() throws Exception {
		sqlSessionFactory = new DefaultSqlSessionFactory(
				"src/test/resources/xmltest.xml");
		sqlSession = (DefaultSqlSession) sqlSessionFactory.openSession();
		List<Class<?>> mapperList = new ArrayList<Class<?>>();
		mapperList.add(courseinfoMapper.class);
		SimpleScanner scanner = new SimpleScanner();
		scanner.setDaoClasses(mapperList);
		scanner.scan();
		sqlSession.setMapStatments(scanner.getScannedMapStatment());
		sqlSession.setResultMaps(scanner.getScanedResultMap());
		courseinfomapper = sqlSession.getMapper(courseinfoMapper.class);
	}

	@Test
	public void testList() {
		List<courseinfo> courses = courseinfomapper.selectAll();
		for (courseinfo c : courses) {
			System.out.println(c);
		}

	}

	@Test
	public void testOne() {
		System.out.println(courseinfomapper.selectOne(3));
	}

	@Test
	public void testCount() {
		System.out.println(courseinfomapper.count());
	}
}
