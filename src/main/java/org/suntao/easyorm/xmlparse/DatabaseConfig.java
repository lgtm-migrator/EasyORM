package org.suntao.easyorm.xmlparse;

/**
 * 数据库配置
 * <p>
 * 存储数据库的配置
 * 
 * @author suntao
 * 
 */
public class DatabaseConfig {
	/**
	 * 数据库的JDBC driver
	 */
	private String db_driver;
	/**
	 * 数据库名称
	 */
	private String db_name;
	/**
	 * 数据库密码
	 */
	private String db_password;
	/**
	 * JDBC连接地址
	 */
	private String db_url;
	/**
	 * 数据库用户名
	 */
	private String db_username;

	public DatabaseConfig() {

	}

	public DatabaseConfig(String driver, String jdbcurl, String username,
			String password) {
		super();
		this.db_driver = driver;
		this.db_url = jdbcurl;
		this.db_username = username;
		this.db_password = password;
	}

	public DatabaseConfig(String name, String driver, String jdbcurl,
			String username, String password) {
		this(driver, jdbcurl, username, password);
		this.db_name = name;
	}

	public String getDriver() {
		return db_driver;
	}

	public String getInfoStr() {
		return String.format("%s\t%s\t%s\t%s\t%s", db_name, db_driver, db_url,
				db_username, db_password);
	}

	public String getJdbcurl() {
		return db_url;
	}

	public String getName() {
		return db_name;
	}

	public String getPassword() {
		return db_password;
	}

	public String getUsername() {
		return db_username;
	}

	/**
	 * 为实体设定值
	 * 
	 * @param fieldName
	 *            字段名称
	 * @param value
	 */
	public void set(String fieldName, String value) {
		if (fieldName.equals("db_name")) {
			this.db_name = value;
		} else if (fieldName.equals("db_driver")) {
			this.db_driver = value;
		} else if (fieldName.equals("db_password")) {
			this.db_password = value;
		} else if (fieldName.equals("db_username")) {
			this.db_username = value;
		} else if (fieldName.equals("db_url"))
			this.db_url = value;

	}

	public void setDriver(String driver) {
		this.db_driver = driver;
	}

	public void setJdbcurl(String jdbcurl) {
		this.db_url = jdbcurl;
	}

	public void setName(String name) {
		this.db_name = name;
	}

	public void setPassword(String password) {
		this.db_password = password;
	}

	public void setUsername(String username) {
		this.db_username = username;
	}
}