package org.suntao.easyorm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于指定一个域在数据库中的名称
 * 
 * @author suntao
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
	/**
	 * 该字段在Database中的name
	 * 
	 * @return
	 */
	String nameindatabase();
}
