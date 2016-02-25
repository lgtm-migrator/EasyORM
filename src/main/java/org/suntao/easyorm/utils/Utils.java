package org.suntao.easyorm.utils;

import java.sql.Date;

/**
 * EasyORM实用类
 * 
 * @author suntao
 *
 */
public class Utils {
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
}
