package org.suntao.easyorm.session.defaults;

import java.util.logging.Logger;

import org.suntao.easyorm.configuration.DatabaseConfig;
import org.suntao.easyorm.configuration.EasyormConfig;
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
	private static Logger logger = Logger.getLogger(SqlSessionFactory.class
			.getName());

	/**
	 * 通过配置实体创建Factory
	 * @param easyormConfig 配置实体
	 */
	public DefaultSqlSessionFactory(EasyormConfig easyormConfig) {
		logger.info("使用EasyORMConfig实体配置SqlSessionFactory");
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
		logger.info("仅使用数据库信息配置SqlSessionFactory/非池化");
		EasyormConfig easyormConfig = new EasyormConfig();
		easyormConfig.setDatabaseConfig(new DatabaseConfig(JDBCDriver, url,
				username, passwd));
		easyormConfig.setPooled(false);
		this.easyormConfig = easyormConfig;
	}

	@Override
	public SqlSession openSession() {
		SqlSession result = null;
		try {
			result = new DefaultSqlSession(easyormConfig);
		} catch (EasyormConfigException e) {
			logger.severe("当前的配置不能打开一个Session/无法创建一个可打开关闭的数据库连接");
			e.printStackTrace();
		}
		return result;

	}

}
