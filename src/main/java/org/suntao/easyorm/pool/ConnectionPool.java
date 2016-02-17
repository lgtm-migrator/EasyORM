package org.suntao.easyorm.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.mysql.jdbc.Driver;

/**
 * 连接池
 * <p>
 * 使用线程安全的ConcurrentHashMap作为存储连接的容器
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
	private Boolean isInit;
	public static final int DEFAULT_POOL_SIZE = 10;

	private Map<Connection, Boolean> connectionsMap;
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
		this.isInit = false;
		this.connectionsMap = new ConcurrentHashMap<Connection, Boolean>();
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
		this.isInit = false;
		this.connectionsMap = new ConcurrentHashMap<Connection, Boolean>(
				poolSize);
		initPool();
	}

	private void initPool() {
		try {

			Class.forName(driver);
			for (int i = 0; i < poolSize; i++) {
				Connection currentConn = DriverManager.getConnection(url,
						username, passwd);
				logger.debug("连接" + currentConn + "已创建");
				if (currentConn != null)
					connectionsMap.put(currentConn, false);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		isInit = true;
		logger.debug("共创建" + connectionsMap.size() + "个连接");

	}

	/**
	 * 获取该池是否已经初始化
	 * 
	 * @return
	 */
	public Boolean isInit() {
		return isInit;
	}

	public void releasePool() {
		for (Connection conn : connectionsMap.keySet()) {
			try {
				conn.close();
				logger.debug("连接" + conn + "已关闭");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		connectionsMap.clear();
		logger.debug("释放完成");
	}

	public synchronized Connection getConnection() {
		Connection result = null;
		for (Connection conn : connectionsMap.keySet()) {
			if (!connectionsMap.get(conn)) {
				connectionsMap.replace(conn, true);
				result = conn;
				break;
			}
		}
		if (result != null)
			logger.debug("连接" + result + "被取出");
		else
			logger.warn("连接池没有更多连接,返回null");
		return result;
	}

	public synchronized Boolean returnConnection(Connection conn) {
		Boolean result = false;
		try {
			connectionsMap.replace(conn, false);
			logger.debug("连接" + conn + "回到连接池");
			result = true;
		} catch (Exception e) {
			result = false;
		}

		return result;
	}
}
