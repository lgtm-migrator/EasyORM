package org.suntao.easyorm.map;

import java.util.HashMap;
import java.util.Map;

/**
 * ResultSet结果映射配置
 * <p>
 * 保存映射所需的配置:<br>
 * 列对应关系<br>
 * id<br>
 * 返回类型<br>
 * 返回实体的class
 * 
 * @author suntao
 * 
 * @param <T>
 */
public class ResultMapConfig<T> {
	/**
	 * 实体Class
	 * <p>
	 * 用于反射时建立实体
	 */
	private Class<T> modelClass;
	/**
	 * 实体的fields与JDBC结果集columns之间名称的对应关系
	 */
	private Map<String, String> properties;

	/**
	 * resultMap的ID 用于查找
	 */
	private String resultMapID;
	/**
	 * 映射后应返回的类型
	 */
	private ResultMappingType resultType;

	public ResultMapConfig() {
		super();
		properties = new HashMap<String, String>();
	}

	/**
	 * 结果映射配置
	 * <p>
	 * 包含从行到实体的相应配置
	 * 
	 * @param modelClass
	 *            需要映射实体类的class
	 * @param properties
	 *            实体类与ResultSet相对应的name
	 * @param resultMapID
	 *            配置的ID
	 *            <p>
	 *            一般是实体的全限定名 + resultMap名<br>
	 *            因为一个实体可能有多个resultMap
	 * @param resultType
	 *            代理返回的类型
	 *            <p>
	 *            对应接口定义的返回类型
	 */
	public ResultMapConfig(Class<T> modelClass, Map<String, String> properties,
			String resultMapID, ResultMappingType resultType) {
		super();
		this.modelClass = modelClass;
		this.properties = properties;
		this.resultMapID = resultMapID;
		this.resultType = resultType;
		if (properties == null)
			properties = new HashMap<String, String>();
	}

	public void addProperty(String nameOfModel, String nameOfColumn) {
		this.properties.put(nameOfModel, nameOfColumn);
	}

	public String getInfoStr() {
		String result = null;
		result = String.format(
				"ID:%s\nMODELNAME:%s\nRESULTTYPE:%s\nPROPERTIESNUMBER:%d",
				getResultMapID(), getModelClass(), getResultType()
						.name(), getProperties().size());
		return result;
	}

	public Class<T> getModelClass() {
		return modelClass;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public String getResultMapID() {
		return resultMapID;
	}

	public ResultMappingType getResultType() {
		return resultType;
	}

	public void setModelClass(Class<T> modelClass) {
		this.modelClass = modelClass;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public void setResultMapID(String resultMapID) {
		this.resultMapID = resultMapID;
	}

	public void setResultType(ResultMappingType resultType) {
		this.resultType = resultType;
	}
}
