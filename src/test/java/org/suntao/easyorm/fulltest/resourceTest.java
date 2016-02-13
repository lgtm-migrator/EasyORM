package org.suntao.easyorm.fulltest;

import java.net.URL;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

public class resourceTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		// 获取java相关属性
		Properties properties = System.getProperties();
		for (Object o : properties.keySet()) {
			System.out.println(o + " " + properties.get(o));
		}
		URL url = this.getClass().getClassLoader().getResource("xmltest.xml");
		System.out.println(url.getFile());
	}

}
