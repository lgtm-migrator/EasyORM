package org.suntao.easyorm.map;

/**
 * 结果映射种类
 * <p>
 * 结果映射种类<br>
 * 与DAO中的方法返回类型相对应
 * 
 * @author suntao
 *
 */
public enum ResultMappingType {
	/**
	 * 返回布尔值
	 * <p>
	 * 如果影响条数大于0 则返回true<br>
	 * 否则返回false
	 */
	BOOLEAN,
	/**
	 * 返回整型
	 * <p>
	 * 返回影响条数<br>
	 * 如果是select语句这返回查询的条数
	 */
	INTEGER,
	/**
	 * 返回实体列表
	 * <p>
	 * 如果查询语句有多行结果,这返回映射后的实体列表
	 */
	MODELLIST,
	/**
	 * 返回单个实体
	 * <p>
	 * 如果查询有多个实体,则返回第一条
	 */
	ONEMODEL,
	/**
	 * dao接口定义了不能处理的返回
	 */
	OTHER

}
