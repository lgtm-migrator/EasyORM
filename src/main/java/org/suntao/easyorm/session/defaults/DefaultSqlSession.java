package org.suntao.easyorm.session.defaults;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.management.modelmbean.ModelMBean;
import javax.net.ssl.SSLEngineResult.Status;

import org.apache.log4j.Logger;
import org.suntao.easyorm.annotation.DataBaseModel;
import org.suntao.easyorm.executor.Executor;
import org.suntao.easyorm.executor.defaults.DefaultExecutor;
import org.suntao.easyorm.map.MapStatement;
import org.suntao.easyorm.map.ResultMapConfig;
import org.suntao.easyorm.map.ResultMapping;
import org.suntao.easyorm.map.ResultMappingType;
import org.suntao.easyorm.map.defaults.DefaultResultMapping;
import org.suntao.easyorm.proxy.MapperProxyBuilder;
import org.suntao.easyorm.scan.Scanner;
import org.suntao.easyorm.scan.defaults.DefaultScanner;
import org.suntao.easyorm.session.SqlSession;
import org.suntao.easyorm.xmlparse.DatabaseConfig;
import org.suntao.easyorm.xmlparse.EasyormConfig;

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
	/**
	 * log4j
	 */
	private static Logger logger = Logger.getLogger(SqlSession.class);

	/**
	 * 创建一个SqlSession
	 * <p>
	 * 将会初始化MapStatement缓存<br>
	 * 请务必确认有数据库配置
	 * 
	 * @param easyormConfig
	 */
	public DefaultSqlSession(EasyormConfig easyormConfig) {
		this.easyormConfig = easyormConfig;
		this.databaseConfig = easyormConfig.getDatabaseConfig();
		this.resultMapping = new DefaultResultMapping();
		this.executor = new DefaultExecutor(this, this.resultMapping);
		if (scanner == null)
			scanner = new DefaultScanner(easyormConfig);
		scanner.scan();
		this.mapStatementCache = scanner.getScannedMapStatement();
		check();
	}

	/**
	 * 校验本SqlSession是否可以完成工作
	 */
	public void check() {
		logger.debug(String.format("开始校验配置实体"));
		if (easyormConfig != null) {
			logger.debug(easyormConfig + "\n如果当中存在null则说明该属性没有初始化");
		}
		if (databaseConfig == null) {
			logger.fatal("致命错误,退出程序,必须配置数据库否则EasyOrm无法使用");
			System.exit(1);
		}
		logger.debug("尝试获取连接");
		try {
			returnConnection(getConnection());
		} catch (Exception e) {
			logger.warn("创建和归还连接失败(更有可能是归还数据库连接失败)" + e.toString());
		}
		logger.debug("校验完成,EasyORM可以完成对连接的创建和关闭");
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
				logger.warn("执行默认方法的条件检验失败,因为传入的对象是null");
			else {
				logger.warn("没有DatabaseModel注解");
			}
		}
		return result;
	}

	@Override
	public Integer deleteByPrimaryKey(Object obj) {
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
			logger.error("执行本方法失败,参数为空或者没有DatabaseModel注解");
		}
		return result;
	}

	@Override
	public Connection getConnection() {
		Connection result = null;
		try {
			Class.forName(databaseConfig.getDriver());
			result = DriverManager.getConnection(databaseConfig.getJdbcurl(),
					databaseConfig.getUsername(), databaseConfig.getPassword());
		} catch (ClassNotFoundException e) {
			logger.fatal("致命错误,程序退出,JDBC Driver 没有找到\n请确认driver名称正确,请确认相应jar包已加入Referenced Libraries");
			System.exit(1);
		} catch (SQLException e) {
			logger.error("JDBC 连接失败或者存在问题,请检查");
		} catch (NullPointerException e) {
			logger.fatal("致命错误,程序退出,JDBC 数据库配置不存在,请检查\n数据库配置详情:"
					+ databaseConfig);
			System.exit(1);
		}
		if (result != null)
			logger.debug("创建了一个数据库连接" + result);
		else
			logger.warn("创建了一个空连接,请检查网络是否连接");
		return result;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> T getMapper(Class<T> mapperClass) {
		logger.debug(String.format("尝试获取%s的代理", mapperClass));
		T result = null;
		result = (T) MapperProxyBuilder.getMapperProxy(mapperClass,
				this.executor, mapStatementCache);
		if (result != null) {
			logger.debug("代理获取成功");
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
	 */
	@Override
	public Integer insert(Object obj) {
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
			Field[] fields = obj.getClass().getDeclaredFields();
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
					logger.debug("默认方法产生Sql语句为:" + sql);
					// 缓存
					mapStatementCache.put(willBeCachedMapStatement.getId(),
							willBeCachedMapStatement);
					mapStatement = willBeCachedMapStatement;
				}
			}
			result = (int) executor.execute(mapStatement, params);
		} else {
			logger.warn("请检查是否传入了null引用,请确认实体拥有DatabaseModel注解");
		}
		return result;
	}

	@Override
	public void returnConnection(Connection conn) {
		logger.debug("向SqlSession返回了一个数据库连接" + conn);
		try {
			conn.close();
			logger.debug(String.format("连接 %s已关闭", conn));
		} catch (SQLException e) {
			logger.warn(String.format("连接 %s关闭失败", conn));
		} catch (NullPointerException e) {
			logger.error("请检查为何向SqlSession返回了null连接");
			e.printStackTrace();
		}
	}

	@Override
	public <T> List<T> selectALL(Class<T> modelClass) {
		List<T> result = null;
		if (modelClass != null
				&& modelClass.getAnnotation(DataBaseModel.class) != null) {
			DataBaseModel dataBaseModel = modelClass
					.getAnnotation(DataBaseModel.class);
			String tableName = dataBaseModel.tablename();
			String key = modelClass.getName() + ".selectAll";
			MapStatement mapStatement = mapStatementCache.get(key);
			if (mapStatement == null) {
				logger.debug("缓存中不存在" + key + "的MapStatement,动态生成并缓存");
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
			logger.error("请确认传入的参数不为null,并且有DatabaseModel注解");
		}

		return result;
	}

	@Override
	public <T> T selectByPrimaryKey(T obj) {
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
			logger.error("执行本方法失败,参数为空或者没有DatabaseModel注解");
		}
		return result;
	}

	@Override
	public Integer updateByPrimaryKey(Object obj) {
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
			Field[] fields = obj.getClass().getDeclaredFields();
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
			logger.debug("执行语句" + sqlStr);

		} else {
			logger.error("执行本方法失败,参数为空或者没有DatabaseModel注解");
		}
		return result;
	}
}
