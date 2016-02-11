package org.suntao.easyorm.map;

import java.lang.reflect.Type;

/**
 * 结果映射配置项
 * <p>
 * 记录实体的每一个属性与列对应的关系
 * 
 * @author suntao
 * 
 */
public class ResultMapConfigItem {
	/**
	 * 在resultset中的name
	 */
	private String nameInColume;
	/**
	 * 在实体中的name
	 */
	private String nameInModel;
	/**
	 * 类型
	 */
	private Type type;

	public ResultMapConfigItem() {

	}

	public ResultMapConfigItem(String nameInModel, String nameInColume, Type type) {
		super();
		this.nameInModel = nameInModel;
		this.nameInColume = nameInColume;
		this.type = type;
	}

	public String getNameInColume() {
		return nameInColume;
	}

	public String getNameInModel() {
		return nameInModel;
	}

	public Type getType() {
		return type;
	}

	public void setNameInColume(String nameInColume) {
		this.nameInColume = nameInColume;
	}

	public void setNameInModel(String nameInModel) {
		this.nameInModel = nameInModel;
	}

	public void setType(Type type) {
		this.type = type;
	}

}
