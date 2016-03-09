package org.suntao.easyorm.configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.suntao.easyorm.map.MapStatement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * xml配置读取
 * <p>
 * 包含从xml文件中读取配置的各种方法<br>
 * 由于本版本放弃通过XML配置的方案,此类暂时废弃
 * 
 * @author suntao
 * 
 */
public class XmlParse {

	private static Logger logger = Logger.getLogger(XmlParse.class);

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
		logger.debug(String.format("开始从file:%s扫描easyormconfig实体", xml.getName()));
		EasyormConfig result = new EasyormConfig();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);// 设定验证dtd
		/**
		 * 使用本方法跳过无用空格
		 * <p>
		 * 如果看不懂为什么需要跳过空格<br>
		 * 请参阅xml解析相关章节
		 */
		factory.setIgnoringElementContentWhitespace(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document xmlDoc = builder.parse(xml);
			result.setDatabaseConfig(parseDataBaseConfig(xmlDoc));// 获取database配置实体
			Map<String, String> daoMap = parseDAOandMapperXmlConfig(xmlDoc);// 获取dao相关配置
			result.setDaoPath(daoMap.get("daojavapath"));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("输入输出错误,请检查文件是否存在");
			e.printStackTrace();
		}
		logger.debug("EasyORMConfig实体扫描完成");
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
	 * 将mapper xml配置文件转化成MapStatmnet文件
	 * 
	 * @param xml
	 * @return
	 */
	public static Map<String, MapStatement> mapStatmentsParse(File xml) {
		Map<String, MapStatement> result = new HashMap<String, MapStatement>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);// 设定验证dtd
		factory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(xml);
			Element root = (Element) doc.getElementsByTagName("mapper").item(0);
			String mapperid = root.getAttribute("id");
			String daoclass = root.getAttribute("daoclass");
			NodeList methods = root.getChildNodes();
			for (int index = 0; index < methods.getLength(); index++) {
				Element currentMethod = (Element) methods.item(index);
				String currentMethodName = currentMethod.getAttribute("name");
				String sqlStr = currentMethod.getChildNodes().item(0)
						.getTextContent();
				MapStatement currentMapStatment = new MapStatement();
				currentMapStatment.setId(daoclass + "." + currentMethodName);
				currentMapStatment.setStatementSQL(sqlStr);
				result.put(currentMapStatment.getId(), currentMapStatment);
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
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
		String daojavapath = null;
		String mapperxmlpath = null;
		if (daoElement != null) {
			daojavapath = daoElement.getAttribute("daopackage");
			mapperxmlpath = daoElement.getAttribute("mapperxmlpackage");
			result.put("daojavapath", daojavapath);
			result.put("mapperxmlpath", mapperxmlpath);
		}
		logger.debug(String.format(
				"dao和mapper,xml的地址扫描完成,Dao Path:%s,Xml Path:%s", daojavapath,
				mapperxmlpath));
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
		logger.debug("数据库配置扫描完成");
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
	public static Map<String, MapStatement> parseMapStatment(Document doc) {
		Map<String, MapStatement> result = new HashMap<String, MapStatement>();
		// TO DO
		return result;
	}

}
