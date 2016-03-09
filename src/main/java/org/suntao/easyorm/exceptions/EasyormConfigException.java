package org.suntao.easyorm.exceptions;

/**
 * EasyORM在配置的时候出现了Exception<br>
 * 可能是由于配置无法连接数据库<br>
 * 或者某些关键字段为空
 * 
 * @author suntao
 *
 */
public class EasyormConfigException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5663370174743000308L;

	public EasyormConfigException() {
	}

	public EasyormConfigException(String eStr) {
		super(eStr);
	}

}
