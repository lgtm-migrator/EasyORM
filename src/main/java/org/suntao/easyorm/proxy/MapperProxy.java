package org.suntao.easyorm.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.suntao.easyorm.executor.Executor;
import org.suntao.easyorm.map.MapStatment;
import org.suntao.easyorm.map.ResultMapConfig;
import org.suntao.easyorm.scan.SimpleScanner;

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
	 * 
	 */
	private Map<String, MapStatment> mapStatments;

	private Class<?> interfaceClass;
	/**
	 * log4j
	 **/
	private static Logger logger = Logger.getLogger(MapperProxy.class);

	public MapperProxy(Executor executor,
			Map<String, MapStatment> mapStatments, Class<?> daoInterface) {
		this.executor = executor;
		this.mapStatments = mapStatments;
		this.interfaceClass = daoInterface;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = null;
		String classnameOfMethod = interfaceClass.getName();
		String nameOfMethod = method.getName();
		String key = String.format("%s.%s", classnameOfMethod, nameOfMethod);
		MapStatment mapStatment = mapStatments.get(key);
		if (mapStatment == null) {
			logger.warn(String.format("没有查询到%s的MapStatment,动态生成", key));
			SimpleScanner scanner = new SimpleScanner();
			ResultMapConfig resultMapConfig = scanner
					.scanResultMapConfigOfMethod(method);
			mapStatment = scanner.scanMapStatmentOfMethod(method);
			mapStatment.setResultMap(resultMapConfig);
			mapStatments.put(key, mapStatment);

		}
		result = executor.execute(mapStatment, args);
		return result;
	}
}
