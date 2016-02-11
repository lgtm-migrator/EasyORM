package org.suntao.easyorm.xmlparse;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class xmlTest {
	public static void main(String[] args) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setIgnoringElementContentWhitespace(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document xmlDoc = builder.parse("src/test/resources/xmltest.xml");
			System.out.println(XmlParse.parseDataBaseConfig(xmlDoc)
					.getInfoStr());
			Map<String, MapperConfig> map = XmlParse.parseMappersConfig(xmlDoc);
			Set set = map.keySet();
			System.out.println(map.get(set.iterator().next()).getInfoStr());
			EasyormConfig easyormConfig = XmlParse
					.configParse("src/test/resources/xmltest.xml");
			System.out.println(String.format("DAO PATH:%s\nMappers PATH:%s",
					easyormConfig.getDaoPath(),
					easyormConfig.getMapperXmlPath()));
			System.out.println();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
