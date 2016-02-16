package org.suntao.easyorm.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mysql.jdbc.Driver;

/**
 * 连接池
 * <p>
 * 暂不可用
 * 
 * @author suntao
 *
 */
public class ConnectionPool {
	private String driver;
	private String url;
	private String username;
	private String passwd;
	private int poolSize;
	public static final int DEFAULT_POOL_SIZE = 10;

	private Map<Connection, ConnectionContent> connectionMap;
	/**
	 * log4j
	 **/
	private static Logger logger = Logger.getLogger(ConnectionPool.class);

	public ConnectionPool(String driver, String url, String username,
			String passwd) {
		super();
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.passwd = passwd;
		this.poolSize = DEFAULT_POOL_SIZE;
		initPool();
	}

	public ConnectionPool(String driver, String url, String username,
			String passwd, int poolSize) {
		super();
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.passwd = passwd;
		this.poolSize = poolSize;
		initPool();
	}

	private void initPool() {
		try {
			connectionMap = new HashMap<Connection, ConnectionPool.ConnectionContent>();
			Class.forName(driver);
			for (int i = 0; i < poolSize; i++) {
				Connection currentConn = DriverManager.getConnection(url,
						username, passwd);
				connectionMap.put(currentConn, new ConnectionContent(
						currentConn));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void releasePool() {
		for (Connection conn : connectionMap.keySet()) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		connectionMap.clear();
	}

	public Connection getConnection() {
		Connection result = null;
		for (ConnectionContent cc : connectionMap.values()) {
			if (!cc.isUse())
				result = cc.getConnection();
		}
		return result;
	}

	public Boolean returnConnection(Connection conn) {
		Boolean result = false;
		try {
			connectionMap.get(conn).returnConntion();
		} catch (Exception e) {
			result = false;
		}

		return result;
	}

	class ConnectionContent {
		Connection conn;
		Boolean isUse;

		public ConnectionContent(Connection conn) {
			super();
			this.conn = conn;
			isUse = false;
		}

		public Connection getConnection() {
			return conn;
		}

		/**
		 * 正在使用返回true,没有使用返回false
		 * 
		 * @return
		 */
		public Boolean isUse() {
			return isUse;
		}

		public void returnConntion() {
			this.isUse = false;
		}
	}
}
