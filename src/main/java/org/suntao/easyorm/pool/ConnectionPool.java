package org.suntao.easyorm.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

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

	/**
	 * 创建一个连接池并初始化
	 * <p>
	 * 连接池的容量为10 <br>
	 * <p>
	 * 当连接池中的连接全部被使用的时候<br>
	 * 会创建新的连接,但是此连接并不会被加入池中<br>
	 * 当不在池内的连接被返回时即被关闭<br>
	 * 如果某一些数据库没有用户密码(例如SQLite),请使用null
	 * 
	 * @param driver
	 *            JDBC Driver
	 * @param url
	 *            URL
	 * @param username
	 *            用户名
	 * @param passwd
	 *            密码
	 */
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

	/**
	 * 创建一个连接池并初始化
	 * <p>
	 * 当连接池中的连接全部被使用的时候<br>
	 * 会创建新的连接,但是此连接并不会被加入池中<br>
	 * 当不在池内的连接被返回时即被关闭<br>
	 * 如果某一些数据库没有用户密码(例如SQLite),请使用null
	 * 
	 * @param driver
	 * @param url
	 * @param username
	 * @param passwd
	 * @param poolSize
	 *            池的大小
	 */
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

	/**
	 * 从连接池中取一个连接
	 * <p>
	 * 请注意请不要关闭此连接<br>
	 * 使用完之后请使用returnConnection<br>
	 * 返回此连接到连接池
	 * 
	 * @return
	 */
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
		else {
			logger.warn("请注意此连接池已经用尽,现在创建了一个新的连接以响应请求");
			result = getNewConnectionFromJDBC();
		}
		return result;
	}

	private synchronized Connection getNewConnectionFromJDBC() {
		Connection result = null;
		try {
			result = DriverManager.getConnection(url, username, passwd);
			logger.debug("创建一个新连接" + result);
		} catch (SQLException e) {
			logger.debug("创建连接失败");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 初始化数据连接池
	 */
	private void initPool() {
		try {
			Class.forName(driver);
			for (int i = 0; i < poolSize; i++) {
				Connection currentConn = getNewConnectionFromJDBC();
				if (currentConn != null) {
					connectionsMap.put(currentConn, false);
					logger.debug("连接" + currentConn + "加入连接池");
				}
			}
		} catch (ClassNotFoundException e) {
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

	/**
	 * 获取连接池大小
	 * 
	 * @return size
	 */
	public int PoolSize() {
		return poolSize;
	}

	/**
	 * 释放连接池
	 */
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

	/**
	 * 返回连接
	 * <p>
	 * 向池内返回一个连接<br>
	 * 注意返回连接之后就,请不要再使用此连接<br>
	 * 如果返回了一个不在池内的连接<br>
	 * 该连接将直接被关闭
	 * <p>
	 * 同步的方法<br>
	 * 同一时间只有一个线程能访问
	 * 
	 * @param conn
	 *            数据库连接
	 * @return 是否成功
	 */
	public synchronized Boolean returnConnection(Connection conn) {
		Boolean result = false;

		// 当参数为null
		if (conn == null) {
			logger.debug("请不要向池中返回一个null");
			result = false;
		}
		// 如果本池包含池连接
		else if (connectionsMap.containsKey(conn)) {
			try {
				connectionsMap.replace(conn, false);
				logger.debug("连接" + conn + "回到连接池");
				result = true;
			} catch (Exception e) {
				result = false;
				e.printStackTrace();
			}
		}
		// 如若不包含此连接
		else {
			try {
				logger.debug("不属于池的连接" + conn + "返回到池内,被关闭");
				conn.close();
				result = true;
			} catch (SQLException e) {
				result = false;
				e.printStackTrace();
			}
		}

		return result;
	}
}
