package org.suntao.easyorm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * DAO参数
 * <p>
 * 用于定义当前参数的名称
 * 
 * @author suntao
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
	/**
	 * 变量名
	 * 
	 * @return
	 */
	public String paramname() default "";
}
