package org.suntao.easyorm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SQL注解
 * <p>
 * 用于DAO接口的方法,指定该方法的sql语句
 * 
 * @author suntao
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SQL {
	/**
	 * SQL语句
	 * <p>
	 * 例如:<br>
	 * select * from user where id = ?
	 * 
	 * @return
	 */
	public String value() default "";
}
