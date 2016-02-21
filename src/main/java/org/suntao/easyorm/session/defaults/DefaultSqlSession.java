package org.suntao.easyorm.session.defaults;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import javax.net.ssl.SSLEngineResult.Status;

import org.apache.log4j.Logger;
import org.suntao.easyorm.annotation.DataBaseModel;
import org.suntao.easyorm.executor.Executor;
import org.suntao.easyorm.executor.defaults.SimpleExecutor;
import org.suntao.easyorm.map.MapStatment;
import org.suntao.easyorm.map.ResultMapConfig;
import org.suntao.easyorm.map.ResultMapping;
import org.suntao.easyorm.map.ResultMappingType;
import org.suntao.easyorm.map.defaults.SimpleResultMapping;
import org.suntao.easyorm.proxy.MapperProxyBuilder;
import org.suntao.easyorm.scan.Scanner;
import org.suntao.easyorm.scan.SimpleScanner;
import org.suntao.easyorm.session.SqlSession;
import org.suntao.easyorm.xmlparse.DatabaseConfig;
import org.suntao.easyorm.xmlparse.EasyormConfig;

public class DefaultSqlSession implements SqlSession {
	private DatabaseConfig databaseConfig;
	private EasyormConfig easyormConfig;
	private Map<String, MapStatment> mapStatments;
	private Scanner scanner;
	private static Logger logger = Logger.getLogger(SqlSession.class);
	private ResultMapping resultMapping;
	private Executor executor;

	public DefaultSqlSession(EasyormConfig easyormConfig) {
		this.easyormConfig = easyormConfig;
		this.databaseConfig = easyormConfig.getDatabaseConfig();
		this.resultMapping = new SimpleResultMapping();
		this.executor = new SimpleExecutor(this, this.resultMapping);
		if (scanner == null)
			scanner = new SimpleScanner(easyormConfig);
		scanner.scan();
		this.mapStatments = scanner.getScannedMapStatment();
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
			logger.warn("创建和归还连接失败(更有可能是归还数据库连接失败)" + e.toString());
		}
		logger.debug("校验完成,EasyORM可以完成对连接的创建和关闭");
	}

	@Override
	public Integer delete(Object obj) {
		// TODO Auto-generated method stub
		return null;
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
				this.executor, mapStatments);
		if (result != null) {
			logger.debug("代理获取成功");
		}
		return result;
	}

	@Override
	public Integer insert(Object obj) {
		Integer result = -1;
		DataBaseModel annoConfig = null;
		if (obj != null) {
			String objClassName = obj.getClass().getName();
			String key = objClassName + ".insert";
			MapStatment mapStatment = mapStatments.get(key);
			Field[] fields = obj.getClass().getDeclaredFields();
			Object[] params = new Object[fields.length];
			annoConfig = obj.getClass().getAnnotation(DataBaseModel.class);
			String primarykeyname = null;
			Boolean autoincrease = null;
			String tablename = null;
			if (annoConfig != null) {
				primarykeyname = annoConfig.primarykey();
				autoincrease = annoConfig.autoincrease();
				tablename = annoConfig.tablename();

				for (int i = 0; i < fields.length; i++) {
					Field currentField = fields[i];
					try {
						currentField.setAccessible(true);
						if (autoincrease
								&& currentField.getName()
										.equals(primarykeyname))
							params[i] = null;
						else {
							params[i] = currentField.get(obj);
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				if (mapStatment != null) {
					executor.execute(mapStatment, params);
				} else {
					if (annoConfig != null) {
						MapStatment willBeCachedMapStatment = new MapStatment();
						willBeCachedMapStatment.setId(key);
						String sql = "insert into " + tablename;
						sql += " ( ";
						for (int i = 0; i < fields.length; i++) {
							if (i > 0)
								sql += ",";
							sql += fields[i].getName();
						}
						sql += " ) values ( ";
						for (int i = 0; i < fields.length; i++) {
							if (i > 0)
								sql += ",";
							sql += "?";
						}
						sql += " ) ";
						willBeCachedMapStatment.setStatmentSQL(sql);
						willBeCachedMapStatment
								.setResultMap(new ResultMapConfig(null, null,
										key, ResultMappingType.INTEGER));
						logger.debug("默认方法产生Sql语句为:" + sql);
						mapStatments.put(willBeCachedMapStatment.getId(),
								willBeCachedMapStatment);
						executor.execute(willBeCachedMapStatment, params);
					}
				}
			} else {
				logger.error("使用默认方法需要对实体进行DatabaseModel注解");
			}
		}
		return result;
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

	@Override
	public Integer update(Object obj) {
		// TODO Auto-generated method stub
		return null;
	}
}
