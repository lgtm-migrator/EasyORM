package org.suntao.easyorm.configuration;

/**
 * easyorm配置
 * <p>
 * 存储easyorm的各种配置
 * 
 * @author suntao
 * 
 */
public class EasyormConfig {
	public static int DEFAULT_POOL_SIZE = 10;
	/**
	 * DAO接口文件包位置
	 */
	private String daoPath;
	/**
	 * 数据库配置
	 */
	private DatabaseConfig databaseConfig;

	/**
	 * 是否使用连接池
	 */
	private boolean isPooled = false;

	/**
	 * 连接池大小
	 */
	private int poolSize;

	public EasyormConfig() {
		this.databaseConfig = new DatabaseConfig();
		this.daoPath = null;
		this.isPooled = false;
		this.poolSize = DEFAULT_POOL_SIZE;
	}

	public EasyormConfig(DatabaseConfig databaseConfig, boolean isPooled) {
		this();
		this.databaseConfig = databaseConfig;
		this.isPooled = isPooled;
	}

	public EasyormConfig(DatabaseConfig databaseConfig, boolean pooled,
			int pooledsize) {
		this(databaseConfig, pooled, pooledsize, null);
	}

	public EasyormConfig(DatabaseConfig databaseConfig, boolean isPooled,
			int poolSize, String daoPath) {
		this();
		this.databaseConfig = databaseConfig;
		this.isPooled = isPooled;
		this.poolSize = poolSize;
		this.daoPath = daoPath;
	}

	public EasyormConfig(String daoPath, DatabaseConfig databaseConfig) {
		this();
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

	public int getPoolSize() {
		return poolSize;
	}

	public boolean isPooled() {
		return isPooled;
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

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	@Override
	public String toString() {
		String result = null;
		result = String.format(
				"DaoPath:%s DBConfig:%s mapperConfigs:%s XmlsPath:%s", daoPath,
				databaseConfig);
		return result;
	}

	public void setPooled(boolean isPooled) {
		this.isPooled = isPooled;
	}

}
