package org.suntao.easyorm.session.defaults;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import javax.net.ssl.SSLEngineResult.Status;

import org.apache.log4j.Logger;
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
		check();
	}

	/**
	 * 校验本SqlSession是否可以完成工作
	 */
	public void check() {
		logger.debug(String.format("开始校验配置实体"));
		if (easyormConfig != null) {
			logger.debug(easyormConfig + "\n如果当中存在null则说明该属性没有初始化");
		}
		if (databaseConfig == null) {
			logger.fatal("致命错误,退出程序,必须配置数据库否则EasyOrm无法使用");
			System.exit(1);
		}
		logger.debug("尝试获取连接");
		try {
			returnConnection(getConnection());
		} catch (Exception e) {
			logger.warn("创建和归还连接失败(更有可能是归还数据库失败)" + e.toString());
		}
		logger.debug("校验完成,EasyORM可以完成对连接的创建和关闭");
	}

	@Override
	public Connection getConnection() {
		Connection result = null;
		try {
			Class.forName(databaseConfig.getDriver());
			result = DriverManager.getConnection(databaseConfig.getJdbcurl(),
					databaseConfig.getUsername(), databaseConfig.getPassword());
		} catch (ClassNotFoundException e) {
			logger.fatal("致命错误,程序退出,JDBC Driver 没有找到\n请确认driver名称正确,请确认相应jar包已加入Referenced Libraries");
			System.exit(1);
		} catch (SQLException e) {
			logger.error("JDBC 连接失败或者存在问题,请检查");
		} catch (NullPointerException e) {
			logger.fatal("致命错误,程序退出,JDBC 数据库配置不存在,请检查\n数据库配置详情:"
					+ databaseConfig);
			System.exit(1);
		}
		if (result != null)
			logger.debug("创建了一个数据库连接" + result);
		else
			logger.warn("创建了一个空连接,请检查网络是否连接");
		return result;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> T getMapper(Class<T> mapperClass) {
		logger.debug(String.format("尝试获取%s的代理", mapperClass));
		T result = null;
		result = (T) MapperProxyBuilder.getMapperProxy(mapperClass,
				new SimpleExecutor(this, new SimpleResultMapping()),
				mapStatments);
		if (result != null) {
			logger.debug("代理获取成功");
		}
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
		logger.debug("向SqlSession返回了一个数据库连接" + conn);
		try {
			conn.close();
			logger.debug(String.format("连接 %s已关闭", conn));
		} catch (SQLException e) {
			logger.warn(String.format("连接 %s关闭失败", conn));
		}
	}

}
