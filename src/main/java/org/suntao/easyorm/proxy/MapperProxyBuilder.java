package org.suntao.easyorm.proxy;

import java.lang.reflect.Proxy;
import java.util.Map;

import org.apache.log4j.Logger;
import org.suntao.easyorm.executor.Executor;
import org.suntao.easyorm.map.MapStatment;

public class MapperProxyBuilder {
	/**
	 * log4j
	 **/
	private static Logger logger = Logger.getLogger(MapperProxyBuilder.class);

	/**
	 * 获取代理对象
	 * <p>
	 * 通过代理对象,可以通过接口,调用相应的而方法
	 * 
	 * @param <T>
	 * 
	 * @return 代理对象
	 */
	public static <T> Object getMapperProxy(Class<T> mapperClass,
			Executor executor, Map<String, MapStatment> mapStatments) {
		Object result = null;
		result = Proxy.newProxyInstance(mapperClass.getClassLoader(),
				new Class[] { mapperClass }, new MapperProxy(executor,
						mapStatments, mapperClass));
		return result;
	}
}
