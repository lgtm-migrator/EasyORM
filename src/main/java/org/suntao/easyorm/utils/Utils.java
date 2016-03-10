package org.suntao.easyorm.utils;

import java.lang.reflect.Field;
import java.sql.Date;
import java.util.Map;
import java.util.logging.Logger;

/**
 * EasyORM实用类
 * 
 * @author suntao
 *
 */
public class Utils {

	private static Logger logger = Logger.getLogger(Utils.class.getName());

	/**
	 * 从java.util.date转化为java.sql.date
	 * 
	 * @param date
	 *            java.util.Date 实例
	 * @return date java.sql.Date 实例
	 */
	public static Date convertSqlDate(java.util.Date date) {
		Date result = null;
		result = new Date(date.getTime());
		return result;
	}

	/**
	 * 从缓存中获取Class获取Field数组<br>
	 * 如果还没有缓存的话,将先缓存
	 * 
	 * @param cachedClassesFieldsMap
	 *            <p>
	 *            存储了ClassNamde和Class.getDeclaredFields()的Map
	 * @param clazz
	 *            <p>
	 *            类
	 * @return
	 */
	public static Field[] getFieldFromCache(
			Map<String, Field[]> cachedClassesFieldsMap, Class<?> clazz) {
		Field[] result = null;
		if (cachedClassesFieldsMap.containsKey(clazz.getName())) {
			result = cachedClassesFieldsMap.get(clazz.getName());
		} else {
			logger.info("缓存" + clazz.getName() + "的所有域");
			result = clazz.getDeclaredFields();
			cachedClassesFieldsMap.put(clazz.getName(), result);
		}
		return result;
	}
}
