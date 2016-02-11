package org.suntao.easyorm.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import org.suntao.easyorm.executor.Executor;
import org.suntao.easyorm.map.MapStatment;

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
		result = executor.execute(mapStatment, args);
		return result;
	}
}
