package org.suntao.easyorm.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 注解试验
 * 
 * @author suntao
 *
 */
public class annotationTest {
	public static void main(String[] args) {
		try {
			Class<?> class1 = Class
					.forName("org.suntao.easyorm.annotation.userMapper");
			System.out.println(class1.getName());
			Method[] methods = class1.getMethods();
			for (Method m : methods) {
//				System.out.println(m.getName());
//				System.out.println(m.getGenericReturnType().getTypeName());
//				System.out.println(m.getReturnType().getName());
//				if (m.getReturnType().equals(List.class)) {
//					ParameterizedType parameterizedType = (ParameterizedType) m
//							.getGenericReturnType();
//					System.out.println(parameterizedType.getRawType());
//					Type[] types = parameterizedType.getActualTypeArguments();
//					
//					for (Type t : types) {
//						System.out.println(t.getTypeName());
//					}
//				}
				Annotation[] anns = m.getAnnotations();
				System.out.println(User.class.isPrimitive());
				System.out.println(anns.length);
				Parameter[] parameters = m.getParameters();
				for (Parameter p : parameters) {
					Annotation[] annotations = p.getAnnotations();

					System.out.println(p.getName() + p.getType());// 获取参数名(argx)以及参数类型
					for (Annotation a : annotations) {
						a = a;
						System.out.println(a);
					}
				}
				for (Annotation a : anns) {
					System.out.println(a.annotationType().getName());
					if (a.annotationType().equals(SQL.class)) {
						SQL a1 = (SQL) a;
						System.out.println(a1.value());
					}
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void testAnnFirst() {
		try {
			Method m = (new annoTestClass()).getClass().getDeclaredMethod(
					"getAnn");
			testAnn t = m.getAnnotation(testAnn.class);
			System.out.println(t.comment());
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class annoTestClass {
	@testAnn(id = 10, comment = "hi")
	public String getAnn() {
		return null;
	}
}
