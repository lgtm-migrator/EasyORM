package org.suntao.easyorm.session.defaults;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.Log4jEntityResolver;
import org.suntao.easyorm.executor.defaults.SimpleExecutor;
import org.suntao.easyorm.map.MapStatment;
import org.suntao.easyorm.map.ResultMapConfig;
import org.suntao.easyorm.map.SimpleResultMapping;
import org.suntao.easyorm.proxy.MapperProxyBuilder;
import org.suntao.easyorm.scan.Scanner;
import org.suntao.easyorm.scan.SimpleScanner;
import org.suntao.easyorm.session.SqlSession;
import org.suntao.easyorm.xmlparse.DatabaseConfig;
import org.suntao.easyorm.xmlparse.EasyormConfig;

public class DefaultSqlSession implements SqlSession {
	private DatabaseConfig databaseConfig;
	private EasyormConfig easyormConfig;
	private MapperProxyBuilder mapperProxyBuilder;
	private Map<String, MapStatment> mapStatments;
	private Map<String, ResultMapConfig<?>> resultMaps;
	private Scanner scanner;
	private static Logger logger = Logger.getLogger(SqlSession.class);

	public DefaultSqlSession(EasyormConfig easyormConfig) {
		this.easyormConfig = easyormConfig;
		this.databaseConfig = easyormConfig.getDatabaseConfig();
		if (scanner == null)
			scanner = new SimpleScanner(easyormConfig);
		scanner.scan();
		this.mapStatments = scanner.getScannedMapStatment();
		this.resultMaps = scanner.getScanedResultMap();
	}

	@Override
	public Connection getConnection() {
		Connection result = null;
		try {
			Class.forName(databaseConfig.getDriver());
			result = DriverManager.getConnection(databaseConfig.getJdbcurl(),
					databaseConfig.getUsername(), databaseConfig.getPassword());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		logger.info("创建了一个数据库连接" + result);
		return result;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> T getMapper(Class<T> mapperClass) {
		T result = null;
		result = (T) MapperProxyBuilder.getMapperProxy(mapperClass,
				new SimpleExecutor(this, new SimpleResultMapping()),
				mapStatments);

		return result;
	}

	public Scanner getScanner() {
		return scanner;
	}

	public void setScanner(Scanner scanner) {
		this.scanner = scanner;
	}

	public Map<String, MapStatment> getMapStatments() {
		return mapStatments;
	}

	public void setMapStatments(Map<String, MapStatment> mapStatments) {
		this.mapStatments = mapStatments;
	}

	public Map<String, ResultMapConfig<?>> getResultMaps() {
		return resultMaps;
	}

	public void setResultMaps(Map<String, ResultMapConfig<?>> resultMaps) {
		this.resultMaps = resultMaps;
	}

	@Override
	public void returnConnection(Connection conn) {
		logger.info("返回了一个数据库连接" + conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
