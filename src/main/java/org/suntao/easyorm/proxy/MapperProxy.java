package org.suntao.easyorm.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.log4j.Logger;
import org.suntao.easyorm.executor.Executor;
import org.suntao.easyorm.map.MapStatement;
import org.suntao.easyorm.map.ResultMapConfig;
import org.suntao.easyorm.scan.defaults.DefaultScanner;

/**
 * mapper接口代理处理类
 * 
 * @author suntao
 *
 */
public class MapperProxy implements InvocationHandler {
	/**
	 * 解释器
	 */
	private Executor executor;
	/**
	 * MapStatement缓存
	 */
	private Map<String, MapStatement> mapStatementsCache;

	private Class<?> interfaceClass;
	/**
	 * log4j
	 **/
	private static Logger logger = Logger.getLogger(MapperProxy.class);

	public MapperProxy(Executor executor,
			Map<String, MapStatement> mapStatementsCache, Class<?> daoInterface) {
		this.executor = executor;
		this.mapStatementsCache = mapStatementsCache;
		this.interfaceClass = daoInterface;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = null;
		String classnameOfMethod = interfaceClass.getName();
		String nameOfMethod = method.getName();
		String key = String.format("%s.%s", classnameOfMethod, nameOfMethod);
		MapStatement mapStatement = mapStatementsCache.get(key);
		if (mapStatement == null) {
			logger.warn(String.format("没有查询到%s的MapStatment,动态生成", key));
			DefaultScanner scanner = new DefaultScanner();
			ResultMapConfig<?> resultMapConfig = scanner
					.scanResultMapConfigOfMethod(method);
			mapStatement = scanner.scanMapStatmentOfMethod(method);
			mapStatement.setResultMap(resultMapConfig);
			mapStatement.setId(key);
			mapStatementsCache.put(key, mapStatement);

		}
		result = executor.execute(mapStatement, args);
		return result;
	}

	@Override
	public String toString() {
		return String.format("MapperProxy,%s", interfaceClass);
	}
}
