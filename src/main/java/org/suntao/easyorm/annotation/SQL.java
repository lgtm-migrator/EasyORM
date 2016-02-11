package org.suntao.easyorm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
