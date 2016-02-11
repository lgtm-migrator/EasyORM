package org.suntao.easyorm.xmlparse;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.suntao.easyorm.map.MapStatment;
import org.suntao.easyorm.map.ResultMapConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * xml配置读取
 * <p>
 * 包含从xml文件中读取配置的各种方法
 * 
 * @author suntao
 * 
 */
public class XmlParse {
	/**
	 * 配置文件格式化
	 * <p>
	 * 将xml文件转化成easyorm配置实体<br>
	 * 方便读取和使用
	 * 
	 * @param xml
	 *            xml文件
	 * @return easyorm配置实体
	 */
	public static EasyormConfig configParse(File xml) {
		EasyormConfig result = new EasyormConfig();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setIgnoringElementContentWhitespace(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document xmlDoc = builder.parse(xml);
			result.setDatabaseConfig(parseDataBaseConfig(xmlDoc));// 获取database实体
			result.setMapperConfigs(parseMappersConfig(xmlDoc));// 获取mapper实体
			Map<String, String> daoMap = parseDAOandMapperXmlConfig(xmlDoc);// 获取dao相关配置
			result.setDaoPath(daoMap.get("daojavapath"));
			result.setMapperXmlPath(daoMap.get("mapperxmlpath"));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 配置文件格式化
	 * <p>
	 * 将xml文件转化成easyorm配置实体<br>
	 * 方便读取和使用
	 * 
	 * @param xmlPath
	 *            文件位置
	 * @return easyorm配置实体
	 */
	public static EasyormConfig configParse(String xmlPath) {
		return configParse(new File(xmlPath));
	}

	/**
	 * 读取dao接口相关配置
	 * <p>
	 * 包含:<br>
	 * dao接口所在包的位置<br>
	 * mapper的配置xml文件所在位置<br>
	 * 返回的Map,通过KEY:"daojavapath",获取dao接口包位置,通过KEY:"mapperxmlpath",
	 * 获取mapper配置xml文件位置
	 * 
	 * @param doc
	 * @return
	 */
	public static Map<String, String> parseDAOandMapperXmlConfig(Document doc) {
		Map<String, String> result = new HashMap<String, String>();
		Element daoElement = (Element) doc.getElementsByTagName("dao").item(0);
		if (daoElement != null) {
			String daojavapath = daoElement.getAttribute("daojavapath");
			String mapperxmlpath = daoElement.getAttribute("mapperxmlpath");
			result.put("daojavapath", daojavapath);
			result.put("mapperxmlpath", mapperxmlpath);
		}
		return result;
	}

	/**
	 * 从Document 读取数据库配置
	 * <p>
	 * 读取数据库的url,驱动,driver,用户名和密码
	 * 
	 * @param doc
	 *            xml document
	 * @return DatabaseConfig 配置实体
	 */
	public static DatabaseConfig parseDataBaseConfig(Document doc) {
		DatabaseConfig result = null;
		Element dbElement = (Element) doc.getElementsByTagName("database")
				.item(0);
		if (dbElement != null) {
			NodeList children = dbElement.getChildNodes();
			result = new DatabaseConfig();
			for (int i = 0; i < children.getLength(); i++) {
				Element child = (Element) children.item(i);
				result.set(child.getNodeName(), child.getTextContent());
			}
		}
		return result;
	}

	/**
	 * 读取mapper的位置信息
	 * <p>
	 * 从easyorm配置文件Document<br>
	 * 读取mapper文件的id和位置
	 * 
	 * @param doc
	 *            Document
	 * @return 存有mappers配置的Map
	 */
	public static Map<String, MapperConfig> parseMappersConfig(Document doc) {
		Map<String, MapperConfig> result = new HashMap<String, MapperConfig>();
		NodeList mappersnode = doc.getElementsByTagName("mapper");
		for (int i = 0; i < mappersnode.getLength(); i++) {
			Element mapperElement = (Element) mappersnode.item(i);
			String id = mapperElement.getAttribute("id");
			String location = mapperElement.getAttribute("location");
			result.put(id, new MapperConfig(id, location));
		}
		return result;
	}

	/**
	 * 解析docment中mapstatment配置
	 * <p>
	 * 针对一个xml文件将配置转化成mapstatment实体
	 * 
	 * @param doc
	 * @return
	 */
	public static Map<String, MapStatment> parseMapStatment(Document doc) {
		Map<String, MapStatment> result = new HashMap<String, MapStatment>();
		// TO DO
		return result;
	}

	/**
	 * 解析ResultMap
	 * 
	 * @param doc
	 * @return
	 */
	public static Map<String, ResultMapConfig<?>> parseResultMap(Document doc) {
		Map<String, ResultMapConfig<?>> result = new HashMap<String, ResultMapConfig<?>>();
		// TO DO
		return result;
	}
}
