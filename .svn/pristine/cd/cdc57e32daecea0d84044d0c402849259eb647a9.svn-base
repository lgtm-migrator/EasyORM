package org.suntao.easyorm.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

interface book {
	String getCode();

	String getName(String str);
}

class bookProxy implements InvocationHandler {

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = null;
		if (method.getName().equals("getName"))
			result = args[0];
		return result;
	}

}

/**
 * 代理试验
 * <p>
 * 如何在只有接口的情况下调用接口的方法
 * 
 * @author suntao
 *
 */
public class proxytest {
	public static void main(String[] args) {
		book b = null;
		b = (book) Proxy.newProxyInstance(book.class.getClassLoader(),
				new Class[] { book.class }, new bookProxy());
		System.out.println(b.getName("hi"));
	}
}