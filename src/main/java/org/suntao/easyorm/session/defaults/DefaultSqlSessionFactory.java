package org.suntao.easyorm.session.defaults;

import org.apache.log4j.Logger;
import org.suntao.easyorm.configuration.DatabaseConfig;
import org.suntao.easyorm.configuration.EasyormConfig;
import org.suntao.easyorm.configuration.XmlParse;
import org.suntao.easyorm.exceptions.EasyormConfigException;
import org.suntao.easyorm.session.SqlSession;
import org.suntao.easyorm.session.SqlSessionFactory;

/**
 * SqlSessionFactory 实现
 * 
 * @author suntao
 *
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {
	private EasyormConfig easyormConfig;
	private static Logger logger = Logger.getLogger(SqlSessionFactory.class);

	public DefaultSqlSessionFactory(EasyormConfig easyormConfig) {
		logger.debug("使用EasyORMConfig实体配置SqlSessionFactory");
		this.easyormConfig = easyormConfig;
	}

	/**
	 * 建立SqlSessionFactory
	 * 
	 * @param JDBCDriver
	 *            jdbc driver
	 * @param url
	 *            jdbc url
	 * @param username
	 *            <p>
	 *            连接username <br>
	 *            sqlite为null
	 * @param passwd
	 *            <p>
	 *            连接密码 <br>
	 *            sqlite 为null
	 */
	public DefaultSqlSessionFactory(String JDBCDriver, String url,
			String username, String passwd) {
		logger.debug("仅使用数据库信息配置SqlSessionFactory");
		EasyormConfig easyormConfig = new EasyormConfig();
		easyormConfig.setDatabaseConfig(new DatabaseConfig(JDBCDriver, url,
				username, passwd));
		this.easyormConfig = easyormConfig;
	}

	// 放弃XML文件配置
	// /**
	// * 使用xml配置文件配置
	// *
	// * @param xmlFileLocation
	// * 配置文件位置
	// */
	// public DefaultSqlSessionFactory(String xmlFileLocation) {
	// logger.debug("使用XML配置文件配置SqlSessionFactory");
	// this.easyormConfig = XmlParse.configParse(xmlFileLocation);
	// }

	@Override
	public SqlSession openSession() {
		SqlSession result = null;
		try {
			result = new DefaultSqlSession(easyormConfig);
		} catch (EasyormConfigException e) {
			// 需要进行提示
			e.printStackTrace();
		}
		return result;

	}

}
