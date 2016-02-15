package org.suntao.easyorm.xmlparse;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.suntao.easyorm.map.MapStatment;
import org.w3c.dom.Document;

/**
 * XMLParse单元测试
 * 
 * @author suntao
 *
 */
public class xmlparsetest {
	static String xmlpath = "src/test/resources/xmltest.xml";
	Document xmldoc;

	@Before
	public void setUp() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);
		factory.setValidating(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		xmldoc = builder.parse(xmlpath);
	}

	@Test
	public void test() {
		EasyormConfig easyormConfig = XmlParse.configParse(xmlpath);
		System.out.println("--------------DAO  config--------------");
		System.out.println(String.format(
				"DAO\t\tPATH:\t%s\nMAPPER\t\tPATH:\t%s\nDATABASE\tURL:\t%s",
				easyormConfig.getDaoPath(), easyormConfig.getMapperXmlPath(),
				easyormConfig.getDatabaseConfig().getJdbcurl()));
		Assert.assertNotNull(easyormConfig);
		System.out.println("----------------end--------------------");
	}

	@Test
	public void testDBConfigParse() {
		DatabaseConfig databaseConfig = XmlParse.parseDataBaseConfig(xmldoc);
		System.out.println(databaseConfig.getInfoStr());
		Assert.assertNotNull(databaseConfig);
	}

	@Test
	public void testMapperConfigParse() {
		Map<String, MapperConfig> mapperconfigmap = XmlParse
				.parseMappersConfig(xmldoc);
		System.out.println("------------mapper config--------------");
		Set keys = mapperconfigmap.keySet();
		MapperConfig mapperConfig;
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			mapperConfig = mapperconfigmap.get(it.next());
			System.out.println(mapperConfig.getInfoStr());
		}
		System.out.println("-----------------end-------------------");
	}

	@Test
	public void testMapperXmlParse() {
		System.out.println("------------dao sql config--------------");
		Map<String, MapStatment> statments = XmlParse
				.mapStatmentsParse(new File(
						"src/test/java/org/suntao/easyorm/testMapper/courseInfoMapper.xml"));

		for (String key : statments.keySet()) {
			System.out.println(String.format("%s %s", statments.get(key)
					.getId(), statments.get(key).getStatmentSQL()));
		}
		System.out.println("-----------------end-------------------");
	}
}
