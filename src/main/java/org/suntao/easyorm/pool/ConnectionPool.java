package org.suntao.easyorm.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.suntao.easyorm.configuration.DatabaseConfig;
import org.suntao.easyorm.exceptions.ConnectionPoolException;
import org.suntao.easyorm.exceptions.GetConnectionException;

/**
 * 连接池
 * <p>
 * 使用线程安全的ConcurrentHashMap作为存储连接的容器<br>
 * 所有对该容器的访问共享一把锁
 * 
 * @author suntao
 *
 */
public class ConnectionPool {
	public static final int DEFAULT_POOL_SIZE = 10;

	private static Logger logger = Logger.getLogger(ConnectionPool.class
			.getName());
	private Map<Connection, Boolean> connectionsMap;
	private String driver;
	private Boolean isInit;
	private String passwd;
	private int poolSize;
	private String url;
	private String username;

	public ConnectionPool(DatabaseConfig config) {
		this(config.getDriver(), config.getJdbcurl(), config.getUsername(),
				config.getPassword());
	}

	public ConnectionPool(DatabaseConfig config, int poolSize) {
		this(config.getDriver(), config.getJdbcurl(), config.getUsername(),
				config.getPassword(), poolSize);
	}

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
		this(driver, url, username, passwd, DEFAULT_POOL_SIZE);
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
	 * @throws ConnectionPoolException
	 */
	public synchronized Connection getConnection()
			throws ConnectionPoolException {
		Connection result = null;
		if (isInit()) {
			for (Connection conn : connectionsMap.keySet()) {
				if (!connectionsMap.get(conn)) {
					connectionsMap.replace(conn, true);
					result = conn;
					break;
				}
			}
			if (result != null)
				logger.info("连接" + result + "被取出");
			else {
				logger.warning("请注意此连接池已经用尽,现在创建了一个新的连接以响应请求");
				try {
					result = getNewConnectionFromJDBC();
				} catch (GetConnectionException e) {
					e.printStackTrace();
				}
			}
		} else {
			throw new ConnectionPoolException("因为连接池还没有初始化,所以返回连接失败");
		}
		return result;
	}

	/**
	 * 从 JDBC 获取一个新的连接
	 * <p>
	 * 创建新的连接<br>
	 * 一般用于初始化连接池<br>
	 * 也可用于连接池不足时创建临时连接
	 * <p>
	 * 线程安全,同步方法
	 * 
	 * @return 连接
	 * @throws GetConnectionException
	 */
	private synchronized Connection getNewConnectionFromJDBC()
			throws GetConnectionException {
		Connection result = null;
		try {
			result = DriverManager.getConnection(url, username, passwd);
			logger.info("创建一个新连接" + result);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (result == null)
			throw new GetConnectionException();
		return result;
	}

	/**
	 * 初始化数据连接池
	 */
	private void initPool() {
		if (!isInit) {// 如果还没有初始化
			try {
				Class.forName(driver);
				for (int i = 0; i < poolSize; i++) {
					Connection currentConn = getNewConnectionFromJDBC();
					if (currentConn != null) {
						connectionsMap.put(currentConn, false);
						logger.info("连接" + currentConn + "加入连接池");
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (GetConnectionException e) {
				logger.severe("初始化连接池时无法获取有效连接");
				e.printStackTrace();
			}
			isInit = true;
			logger.info("共创建" + connectionsMap.size() + "个连接");
		}
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
		return connectionsMap.size();
	}

	/**
	 * 释放连接池
	 */
	public void releasePool() {
		for (Connection conn : connectionsMap.keySet()) {
			try {
				conn.close();
				logger.info("连接" + conn + "已关闭");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		connectionsMap.clear();
		logger.info("释放完成");
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
	 * @throws ConnectionPoolException
	 */
	public synchronized Boolean returnConnection(Connection conn)
			throws ConnectionPoolException {
		Boolean result = false;
		if (isInit()) { // 当参数为null
			if (conn == null) {
				logger.info("请不要向池中返回一个null");
				result = false;
			}
			// 如果本池包含该连接
			else if (connectionsMap.containsKey(conn)) {
				try {
					connectionsMap.replace(conn, false);
					logger.info("连接" + conn + "回到连接池");
					result = true;
				} catch (Exception e) {
					result = false;
					e.printStackTrace();
				}
			}
			// 如若不包含此连接
			else {
				try {
					logger.info("不属于池的连接" + conn + "返回到池内,被关闭");
					conn.close();
					result = true;
				} catch (SQLException e) {
					result = false;
					e.printStackTrace();
				}
			}
		} else {
			throw new ConnectionPoolException("该连接池已经关闭,返回连接失败");
		}
		return result;
	}
}
