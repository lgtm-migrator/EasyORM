package org.suntao.easyorm.configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * easyorm配置
 * <p>
 * 存储easyorm的各种配置
 * 
 * @author suntao
 * 
 */
public class EasyormConfig {
	/**
	 * 连接池大小
	 */
	private int poolSize;
	/**
	 * 是否使用连接池
	 */
	private boolean isPooled = false;

	/**
	 * DAO接口文件包位置
	 */
	private String daoPath;

	/**
	 * 数据库配置
	 */
	private DatabaseConfig databaseConfig;

	/**
	 * mapper位置配置
	 */
	private Map<String, MapperConfig> mapperConfigs;
	/**
	 * mapper xml文件包位置
	 */
	private String mapperXmlPath;

	public EasyormConfig() {
		this.databaseConfig = new DatabaseConfig();
		this.mapperConfigs = new HashMap<String, MapperConfig>();
		this.daoPath = null;
		this.isPooled = false;
	}

	/**
	 * EasyORM主配置文件
	 * 
	 * @param databaseConfig
	 *            数据库配置
	 * @param mapperConfigs
	 *            mapper位置配置
	 */
	public EasyormConfig(DatabaseConfig databaseConfig,
			Map<String, MapperConfig> mapperConfigs) {
		super();
		this.databaseConfig = databaseConfig;
		this.mapperConfigs = mapperConfigs;
	}

	public EasyormConfig(DatabaseConfig databaseConfig, boolean pooled,
			int pooledsize) {
		this(pooledsize, pooled, null, databaseConfig, null, null);
	}

	public EasyormConfig(DatabaseConfig databaseConfig, boolean pooled) {
		this(10, pooled, null, databaseConfig, null, null);
	}

	public EasyormConfig(int poolSize, boolean isPooled, String daoPath,
			DatabaseConfig databaseConfig,
			Map<String, MapperConfig> mapperConfigs, String mapperXmlPath) {
		super();
		this.poolSize = poolSize;
		this.isPooled = isPooled;
		this.daoPath = daoPath;
		this.databaseConfig = databaseConfig;
		this.mapperConfigs = mapperConfigs;
		this.mapperXmlPath = mapperXmlPath;
	}

	public EasyormConfig(String daoPath, DatabaseConfig databaseConfig) {
		super();
		this.daoPath = daoPath;
		this.databaseConfig = databaseConfig;
	}

	public String getDaoPath() {
		return daoPath;
	}

	public DatabaseConfig getDatabaseConfig() {
		return databaseConfig;
	}

	public DatabaseConfig getDbConfig() {
		return databaseConfig;
	}

	public Map<String, MapperConfig> getMapperConfigs() {
		return mapperConfigs;
	}

	public String getMapperXmlPath() {
		return mapperXmlPath;
	}

	public void setDaoPath(String daoPath) {
		this.daoPath = daoPath;
	}

	public void setDatabaseConfig(DatabaseConfig dbConfig) {
		this.databaseConfig = dbConfig;
	}

	public void setDbConfig(DatabaseConfig dbConfig) {
		this.databaseConfig = dbConfig;
	}

	public void setMapperConfigs(Map<String, MapperConfig> mapConfigs) {
		this.mapperConfigs = mapConfigs;
	}

	public void setMapperXmlPath(String mapperXmlPath) {
		this.mapperXmlPath = mapperXmlPath;
	}

	@Override
	public String toString() {
		String result = null;
		result = String.format(
				"DaoPath:%s DBConfig:%s mapperConfigs:%s XmlsPath:%s", daoPath,
				databaseConfig, mapperConfigs, mapperXmlPath);
		return result;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public boolean isPooled() {
		return isPooled;
	}

	/**
	 * 是否池化
	 * 
	 * @param isPooled
	 */
	public void pooled(boolean isPooled) {
		this.isPooled = isPooled;
	}

}
