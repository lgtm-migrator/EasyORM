package org.suntao.easyorm.proxy;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.suntao.easyorm.executor.Executor;
import org.suntao.easyorm.map.MapStatment;
import org.suntao.easyorm.session.SqlSession;

public class MapperProxyBuilder {
	/**
	 * log4j
	 **/
	private static Logger logger = Logger.getLogger(MapperProxyBuilder.class);
	/**
	 * 代理缓存
	 * <p>
	 * key为dao接口类Name <br>
	 * value为代理
	 */
	private static Map<String, Object> cache = new HashMap<String, Object>();

	/**
	 * 获取代理对象
	 * <p>
	 * 通过代理对象,可以通过接口,调用相应的方法
	 * 
	 * @param <T>
	 * 
	 * @return 代理对象
	 */
	public static <T> Object getMapperProxy(Class<T> mapperClass,
			Executor executor, Map<String, MapStatment> mapStatments) {
		Object result = null;
		Object cachedProxy = cache.get(mapperClass.getName());
		if (cachedProxy == null) {
			result = Proxy.newProxyInstance(mapperClass.getClassLoader(),
					new Class[] { mapperClass }, new MapperProxy(executor,
							mapStatments, mapperClass));
			cache.put(mapperClass.getName(), result);
			logger.debug(String.format("代理缓存中不存在%s的代理,动态创建并缓存", mapperClass));
		} else {
			result = cachedProxy;
			logger.debug(String.format("成功从缓存中获取代理", cachedProxy));
		}
		return result;
	}
}
