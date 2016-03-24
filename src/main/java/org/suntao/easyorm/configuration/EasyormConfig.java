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
	 * DAO接口所在包名
	 */
	private String daoPackage;
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

	/**
	 * Easyorm配置实体
	 */
	public EasyormConfig() {
		this.databaseConfig = new DatabaseConfig();
		this.daoPackage = null;
		this.isPooled = false;
		this.poolSize = DEFAULT_POOL_SIZE;
	}

	/**
	 * EasyORM配置实体
	 * 
	 * @param databaseConfig
	 *            数据库配置
	 * @param isPooled
	 *            是否池化
	 */
	public EasyormConfig(DatabaseConfig databaseConfig, boolean isPooled) {
		this(databaseConfig, isPooled, DEFAULT_POOL_SIZE, null);
	}

	/**
	 * Easyorm配置实体
	 * 
	 * @param databaseConfig
	 *            数据库配置
	 * @param pooled
	 *            是否池化
	 * @param pooledsize
	 *            池的大小
	 */
	public EasyormConfig(DatabaseConfig databaseConfig, boolean pooled,
			int pooledsize) {
		this(databaseConfig, pooled, pooledsize, null);
	}

	/**
	 * EasyORM 配置实体
	 * 
	 * @param databaseConfig
	 *            数据库配置
	 * @param isPooled
	 *            是否池化
	 * @param poolSize
	 *            数据连接池大小
	 * @param daoPackage
	 *            DAO接口的Package
	 */
	public EasyormConfig(DatabaseConfig databaseConfig, boolean isPooled,
			int poolSize, String daoPackage) {
		this();
		this.databaseConfig = databaseConfig;
		this.isPooled = isPooled;
		this.poolSize = poolSize;
		this.daoPackage = daoPackage;
	}

	public EasyormConfig(DatabaseConfig databaseConfig, String daoPath) {
		super();
		this.databaseConfig = databaseConfig;
		this.daoPackage = daoPath;
	}

	public String getDaoPath() {
		return daoPackage;
	}

	public DatabaseConfig getDatabaseConfig() {
		return databaseConfig;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public boolean isPooled() {
		return isPooled;
	}

	public void setDaoPath(String daoPath) {
		this.daoPackage = daoPath;
	}

	public void setDatabaseConfig(DatabaseConfig dbConfig) {
		this.databaseConfig = dbConfig;
	}

	public void setDbConfig(DatabaseConfig dbConfig) {
		this.databaseConfig = dbConfig;
	}

	public void setPooled(boolean isPooled) {
		this.isPooled = isPooled;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	@Override
	public String toString() {
		String result = null;
		result = String.format(
				"DaoPath:%s DBConfig:%s mapperConfigs:%s XmlsPath:%s",
				daoPackage, databaseConfig);
		return result;
	}

}
