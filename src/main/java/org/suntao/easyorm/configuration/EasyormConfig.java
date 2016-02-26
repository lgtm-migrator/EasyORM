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
	 * DAO接口文件包位置
	 */
	private String daoPath;

	public EasyormConfig(String daoPath, DatabaseConfig databaseConfig) {
		super();
		this.daoPath = daoPath;
		this.databaseConfig = databaseConfig;
	}

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
	}

	@Override
	public String toString() {
		String result = null;
		result = String.format(
				"DaoPath:%s DBConfig:%s mapperConfigs:%s XmlsPath:%s", daoPath,
				databaseConfig, mapperConfigs, mapperXmlPath);
		return result;
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

}
