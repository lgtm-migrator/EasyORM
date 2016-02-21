package org.suntao.easyorm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库实体注解
 * <p>
 * 使用这一个注解主要是方便执行默认的insert/update/delete等方法
 * 
 * @author suntao
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataBaseModel {
	/**
	 * Table名
	 * <p>
	 * 该实体对应的table名<br>
	 * 在Insert/delete/update中会使用到
	 * 
	 * @return
	 */
	public String tablename();

	/**
	 * 主键
	 * <p>
	 * 数据库主键,唯一 <br>
	 * update/delete用到的key
	 * <p>
	 * 填入此实体中一个域的名称
	 * 
	 * @return
	 */
	public String primarykey();

	/**
	 * 主键是否自增
	 * <p>
	 * 默认false
	 * <p>
	 * 如果设为true<br>
	 * 插入的时候,无论主键是何值,都会被视作null
	 * 
	 * @return
	 */
	public boolean autoincrease() default false;

}
