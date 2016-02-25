package org.suntao.easyorm.map;

import java.util.HashMap;
import java.util.Map;

/**
 * 定义的单独的数据库语句
 * 
 * @author suntao
 *
 */
public class MapStatement {
	/**
	 * statment的ID 查询用
	 */
	private String id;
	/**
	 * 相应名称的参数在proxy中传递的位置
	 * <p>
	 * 因为proxy中的参数以数组形式出现<br>
	 * 为提高扩展性,记录参数所在位置
	 */
	private Map<String, Integer> paramLocation;
	/**
	 * 存储结果映射所需的配置
	 */
	private ResultMapConfig<?> resultMap;
	/**
	 * statment的内容 将使用在statment中
	 */
	private String statmentSQL;

	public MapStatement() {
		super();
		this.paramLocation = new HashMap<String, Integer>();
		this.id = "";
		this.statmentSQL = "";
		this.resultMap = new ResultMapConfig();
	}

	public MapStatement(String id, String statmentSQL) {
		this();
		this.id = id;
		this.statmentSQL = statmentSQL;
	}
	
	
	public void setReturnType(ResultMappingType type){
		if(this.resultMap!=null){
			this.resultMap=new ResultMapConfig();
		}
		this.resultMap.setResultType(type);
	}

	public void setReturnTypeInteger() {
		ResultMapConfig<?> config = new ResultMapConfig(null, null, null,
				ResultMappingType.INTEGER);
		this.resultMap = config;
	}

	public String getId() {
		return id;
	}

	public String getInfoStr() {
		String result = null;
		result = String.format(
				"ID:%s\nSIZEOFLOCATION:%s\nRESULTMAP:%s\nSTATMENTSQL:%s",
				getId(), getParamLocation().size(), getResultMap(),
				getStatmentSQL());
		return result;
	}

	public Map<String, Integer> getParamLocation() {
		return paramLocation;
	}

	public ResultMapConfig<?> getResultMap() {
		return resultMap;
	}

	/**
	 * 返回一个完整的sql语句
	 * <p>
	 * 用于调试
	 * 
	 * @return sql语句
	 */
	public String getSqlStr() {
		String result = null;
		if (statmentSQL != null) {
			// TO DO
			result = statmentSQL;
		}
		return result;
	}

	public String getStatmentSQL() {
		return statmentSQL;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setParamLocation(Map<String, Integer> paramLocation) {
		this.paramLocation = paramLocation;
	}

	public void setResultMap(ResultMapConfig<?> resultMap) {
		this.resultMap = resultMap;
	}

	public void setStatementSQL(String statmentSQL) {
		this.statmentSQL = statmentSQL;
	}

	/**
	 * 返回本实体的ID
	 */
	@Override
	public String toString() {
		return this.id;
	}
}
