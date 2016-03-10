package org.suntao.easyorm.session.defaults;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.suntao.easyorm.annotation.DataBaseModel;
import org.suntao.easyorm.configuration.DatabaseConfig;
import org.suntao.easyorm.configuration.EasyormConfig;
import org.suntao.easyorm.exceptions.ConnectionPoolException;
import org.suntao.easyorm.exceptions.EasyormConfigException;
import org.suntao.easyorm.executor.Executor;
import org.suntao.easyorm.executor.defaults.DefaultExecutor;
import org.suntao.easyorm.map.MapStatement;
import org.suntao.easyorm.map.ResultMapConfig;
import org.suntao.easyorm.map.ResultMapping;
import org.suntao.easyorm.map.ResultMappingType;
import org.suntao.easyorm.map.defaults.DefaultResultMapping;
import org.suntao.easyorm.pool.ConnectionPool;
import org.suntao.easyorm.proxy.MapperProxyBuilder;
import org.suntao.easyorm.scan.Scanner;
import org.suntao.easyorm.scan.defaults.DefaultScanner;
import org.suntao.easyorm.session.SqlSession;
import org.suntao.easyorm.utils.Utils;

/**
 * SqlSession实现
 * 
 * @author suntao
 *
 */
public class DefaultSqlSession implements SqlSession {
	/**
	 * 数据库配置
	 */
	private DatabaseConfig databaseConfig;
	/**
	 * EasyORM配置
	 */
	private EasyormConfig easyormConfig;
	/**
	 * MapStatement缓存
	 */
	private Map<String, MapStatement> mapStatementCache;
	/**
	 * 反射配置扫描器
	 */
	private Scanner scanner;
	/**
	 * 结果映射器
	 */
	private ResultMapping resultMapping;
	/**
	 * JDBC解释器
	 */
	private Executor executor;

	private static Logger logger = Logger.getLogger(SqlSession.class.getName());
	/**
	 * 连接池
	 */
	private ConnectionPool connectionPool;
	/**
	 * 缓存的实体域对象<br>
	 * Key=ClassName<br>
	 * Value=Class.getDeclaredFields
	 */
	private Map<String, Field[]> modelFieldsCache;

	private boolean isPooled = false;

	/**
	 * 创建一个SqlSession
	 * <p>
	 * 将会初始化MapStatement缓存<br>
	 * 请务必确认有数据库配置
	 * 
	 * @param easyormConfig
	 * @throws EasyormConfigException
	 */
	public DefaultSqlSession(EasyormConfig easyormConfig)
			throws EasyormConfigException {
		this.easyormConfig = easyormConfig;
		this.databaseConfig = easyormConfig.getDatabaseConfig();
		this.mapStatementCache = new HashMap<String, MapStatement>();
		this.modelFieldsCache = new HashMap<String, Field[]>();
		this.resultMapping = new DefaultResultMapping(modelFieldsCache);
		this.executor = new DefaultExecutor(this, this.resultMapping);
		this.isPooled = easyormConfig.isPooled();
		this.scanner = new DefaultScanner(easyormConfig);
		if (easyormConfig.getDaoPath() != null) {
			scanner.scan();
			this.mapStatementCache = scanner.getScannedMapStatement();
		}
		if (isPooled)
			this.connectionPool = new ConnectionPool(databaseConfig,
					easyormConfig.getPoolSize());
		if (!check())// 如果检查失败
			throw new EasyormConfigException("对于EasyORM配置检查失败,请确认配置了数据库并且可以连接");
	}

	/**
	 * 校验本SqlSession是否可以完成工作
	 */
	public boolean check() {
		boolean result = true;
		if (easyormConfig != null && databaseConfig != null) {
			// 如果EasyORM配置实体和数据库配置实体都存在
			result = true;
		} else
			// 否则
			result = false;
		try {
			returnConnection(getConnection());
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 检查默认SQL方法的参数是否合法
	 * <p>
	 * 是否不为空并且是否有DatabaseModel注解
	 * 
	 * @param obj
	 * @return
	 */
	private Boolean defaultSqlMethodParamCheck(Object obj) {
		Boolean result = false;
		if (obj != null
				&& obj.getClass().getAnnotation(DataBaseModel.class) != null)
			result = true;
		else {
			if (obj == null)
				logger.warning("执行默认方法的条件检验失败,因为传入的对象是null");
			else {
				logger.warning("没有DatabaseModel注解");
			}
		}
		return result;
	}

	@Override
	public Integer deleteByPrimaryKey(Object obj) throws SQLException {
		Integer result = -1;
		DataBaseModel dataBaseModel = null;
		if (defaultSqlMethodParamCheck(obj)) {
			Class<?> objClass = obj.getClass();
			dataBaseModel = objClass.getAnnotation(DataBaseModel.class);
			String tableName = dataBaseModel.tablename();
			String primaryKey = dataBaseModel.primarykeyname();
			String objClassName = objClass.getName();
			String key = objClassName + ".delete";
			Object[] param = null;
			try {
				Field primaryKeyField = objClass.getDeclaredField(primaryKey);
				primaryKeyField.setAccessible(true);
				param = new Object[1];
				param[0] = primaryKeyField.get(obj);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			MapStatement mapStatement = mapStatementCache.get(key);
			if (mapStatement != null) {
				executor.execute(mapStatement, param);
			} else {
				String sqlStr = String.format("delete from %s where %s = ?",
						tableName, primaryKey);
				MapStatement willBeCachedMapStatement = new MapStatement(key,
						sqlStr);
				willBeCachedMapStatement.setReturnTypeInteger();
				mapStatementCache.put(key, willBeCachedMapStatement);
				executor.execute(willBeCachedMapStatement, param);
			}
		} else {
			logger.severe("执行本方法失败,参数为空或者没有DatabaseModel注解");
		}
		return result;
	}

	@Override
	public Connection getConnection() {
		Connection result = null;
		if ((isPooled && connectionPool != null)) {
			try {
				result = connectionPool.getConnection();
			} catch (ConnectionPoolException e) {
				e.printStackTrace();
			}
		} else {
			try {
				Class.forName(databaseConfig.getDriver());
				result = DriverManager.getConnection(
						databaseConfig.getJdbcurl(),
						databaseConfig.getUsername(),
						databaseConfig.getPassword());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		return result;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> T getMapper(Class<T> mapperClass) {
		logger.info(String.format("尝试获取%s的代理", mapperClass));
		T result = null;
		result = (T) MapperProxyBuilder.getMapperProxy(mapperClass,
				this.executor, mapStatementCache);
		if (result != null) {
			logger.info("代理获取成功");
		}
		return result;
	}

	/**
	 * 默认insert方法
	 * <p>
	 * 
	 * 成功执行主要流程返回值代表影响条数<br>
	 * 主要流程执行失败将返回-1
	 * 
	 * @param obj
	 *            数据库实体
	 * @throws SQLException
	 */
	@Override
	public Integer insert(Object obj) throws SQLException {
		Integer result = -1;
		/**
		 * 数据库实体注解
		 */
		DataBaseModel annoConfig = null;
		// 检验对象是否为空,是否有DatabaseModel注解
		if (defaultSqlMethodParamCheck(obj)) {
			// 对象类名
			String objClassName = obj.getClass().getName();
			// 查询key
			String key = objClassName + ".insert";
			// 查询是否有相应MapStatement
			MapStatement mapStatement = mapStatementCache.get(key);
			// 对象所有域
			Field[] fields = Utils.getFieldFromCache(modelFieldsCache,
					obj.getClass());
			// Executor使用的参数
			Object[] params = new Object[fields.length];
			annoConfig = obj.getClass().getAnnotation(DataBaseModel.class);
			// 实体主键名
			String primarykeyname = null;
			// 实体主键是否自增
			Boolean autoincrease = null;
			// 实体对应的表名
			String tablename = null;
			primarykeyname = annoConfig.primarykeyname();
			autoincrease = annoConfig.autoincrease();
			tablename = annoConfig.tablename();
			// 遍历所有域,填充参数
			for (int i = 0; i < fields.length; i++) {
				Field currentField = fields[i];
				try {
					currentField.setAccessible(true);
					if (autoincrease
							&& currentField.getName().equals(primarykeyname))// 如若当前域的名字和主键名相同,并且主键自增的话,传入null
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
			if (mapStatement == null) {
				// 创建MapStatement后执行
				if (annoConfig != null) {
					MapStatement willBeCachedMapStatement = new MapStatement();
					willBeCachedMapStatement.setId(key);
					// 生成Sql
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
					// 将SQL装入MapStatement中
					willBeCachedMapStatement.setStatementSQL(sql);
					willBeCachedMapStatement
							.setReturnType(ResultMappingType.INTEGER);
					logger.info("默认方法产生Sql语句为:" + sql);
					// 缓存
					mapStatementCache.put(willBeCachedMapStatement.getId(),
							willBeCachedMapStatement);
					mapStatement = willBeCachedMapStatement;
				}
			}
			result = (int) executor.execute(mapStatement, params);
		} else {
			logger.warning("请检查是否传入了null引用,请确认实体拥有DatabaseModel注解");
		}
		return result;
	}

	@Override
	public void returnConnection(Connection conn) {
		if (isPooled) {
			try {
				connectionPool.returnConnection(conn);
			} catch (ConnectionPoolException e) {
				e.printStackTrace();
			}
		} else {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public <T> List<T> selectALL(Class<T> modelClass) throws SQLException {
		List<T> result = null;
		if (modelClass != null
				&& modelClass.getAnnotation(DataBaseModel.class) != null) {
			DataBaseModel dataBaseModel = modelClass
					.getAnnotation(DataBaseModel.class);
			String tableName = dataBaseModel.tablename();
			String key = modelClass.getName() + ".selectAll";
			MapStatement mapStatement = mapStatementCache.get(key);
			if (mapStatement == null) {
				logger.info("缓存中不存在" + key + "的MapStatement,动态生成并缓存");
				String sqlStr = "select * from " + tableName;
				MapStatement willBeCachedMapStatement = new MapStatement();
				willBeCachedMapStatement.setStatementSQL(sqlStr);
				ResultMapConfig<T> config = new ResultMapConfig<T>(modelClass,
						null, key, ResultMappingType.MODELLIST);
				willBeCachedMapStatement.setResultMap(config);
				mapStatementCache.put(key, willBeCachedMapStatement);
				mapStatement = willBeCachedMapStatement;
			}
			result = (List<T>) executor.execute(mapStatement, null);
		} else {
			logger.severe("请确认传入的参数不为null,并且有DatabaseModel注解");
		}

		return result;
	}

	@Override
	public <T> T selectByPrimaryKey(T obj) throws SQLException {
		T result = null;
		DataBaseModel dataBaseModel = null;
		if (defaultSqlMethodParamCheck(obj)) {
			Class<T> objClass = (Class<T>) obj.getClass();
			dataBaseModel = objClass.getAnnotation(DataBaseModel.class);
			String tableName = dataBaseModel.tablename();
			String primaryKeyName = dataBaseModel.primarykeyname();
			String objClassName = objClass.getName();
			String key = objClassName + ".selectByPrimaryKey";
			MapStatement mapStatement = mapStatementCache.get(key);
			Object[] params = new Object[1];
			try {
				Field primaryKeyField = obj.getClass().getDeclaredField(
						primaryKeyName);
				primaryKeyField.setAccessible(true);
				params[0] = primaryKeyField.get(obj);
			} catch (NoSuchFieldException | SecurityException
					| IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			if (mapStatement == null) {
				String sqlStr = "select * from " + tableName + " where "
						+ primaryKeyName + "=?";
				MapStatement willBeCachedMapStatement = new MapStatement();
				willBeCachedMapStatement
						.setReturnType(ResultMappingType.ONEMODEL);
				ResultMapConfig<T> config = new ResultMapConfig<T>(objClass,
						null, key, ResultMappingType.ONEMODEL);
				willBeCachedMapStatement.setResultMap(config);
				willBeCachedMapStatement.setStatementSQL(sqlStr);
				willBeCachedMapStatement.setId(key);
				mapStatementCache.put(key, willBeCachedMapStatement);
				mapStatement = willBeCachedMapStatement;
			}
			result = (T) executor.execute(mapStatement, params);
		} else {
			logger.severe("执行本方法失败,参数为空或者没有DatabaseModel注解");
		}
		return result;
	}

	@Override
	public Integer updateByPrimaryKey(Object obj) throws SQLException {
		int result = -1;
		DataBaseModel dataBaseModel = null;
		if (defaultSqlMethodParamCheck(obj)) {
			Class<?> objClass = obj.getClass();
			dataBaseModel = objClass.getAnnotation(DataBaseModel.class);
			String tableName = dataBaseModel.tablename();
			String primaryKeyName = dataBaseModel.primarykeyname();
			String objClassName = objClass.getName();
			String key = objClassName + ".updateByPrimaryKey";
			String sqlStr = "update " + tableName + " set ";
			MapStatement mapStatement = mapStatementCache.get(key);
			Object[] params = null;
			Field[] fields = Utils.getFieldFromCache(modelFieldsCache,
					objClass);
			params = new Object[fields.length];
			int index = 0;
			try {
				for (Field f : fields) {
					f.setAccessible(true);
					// 如果不是主键
					if (!f.getName().equals(primaryKeyName)) {
						params[index] = f.get(obj);
						sqlStr += f.getName() + "=?";
						index++;
						if (index < fields.length - 1) {
							sqlStr += ",";
						}
					} else {
						params[fields.length - 1] = f.get(obj);// 最后是主键参数
					}
				}
			} catch (SecurityException | IllegalArgumentException
					| IllegalAccessException e) {
				e.printStackTrace();
			}
			sqlStr += " where " + primaryKeyName + "=?";
			if (mapStatement == null) {
				MapStatement willBeCachedMapStatement = new MapStatement();
				willBeCachedMapStatement
						.setReturnType(ResultMappingType.INTEGER);
				willBeCachedMapStatement.setStatementSQL(sqlStr);
				willBeCachedMapStatement.setId(key);
				mapStatementCache.put(key, willBeCachedMapStatement);
				mapStatement = willBeCachedMapStatement;
			}
			result = (int) executor.execute(mapStatement, params);
			logger.info("执行语句" + sqlStr);

		} else {
			logger.severe("执行本方法失败,参数为空或者没有DatabaseModel注解");
		}
		return result;
	}

	@Override
	public void destroy() {
		if (isPooled && this.connectionPool != null)
			this.connectionPool.releasePool();
		this.modelFieldsCache.clear();
		this.mapStatementCache.clear();
		this.databaseConfig = null;
		this.easyormConfig = null;
		this.executor = null;
		this.scanner = null;
		this.resultMapping = null;
		System.gc();
	}
}
