package org.suntao.easyorm.session.defaults;

import org.suntao.easyorm.session.SqlSession;
import org.suntao.easyorm.session.SqlSessionFactory;
import org.suntao.easyorm.xmlparse.EasyormConfig;
import org.suntao.easyorm.xmlparse.XmlParse;

public class DefaultSqlSessionFactory implements SqlSessionFactory {
	private EasyormConfig easyOrmConfig;

	public DefaultSqlSessionFactory(EasyormConfig easyormConfig) {
		this.easyOrmConfig = easyormConfig;
	}

	public DefaultSqlSessionFactory(String xmlFileLocation) {
		this.easyOrmConfig = XmlParse.configParse(xmlFileLocation);
	}

	@Override
	public SqlSession openSession() {
		return new DefaultSqlSession(easyOrmConfig);
	}

}
