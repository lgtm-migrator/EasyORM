package org.suntao.easyorm.map;

import java.sql.ResultSet;
import java.util.List;

/**
 * 结果映射器
 * 
 * @author suntao
 *
 */
public interface ResultMapping {
	/**
	 * 获取查询结果是否成功
	 * <p>
	 * 影响条数>0则判断为成功
	 * 
	 * @param resultMap
	 * @param resultSet
	 * @return
	 */
	public Boolean getBoolean(ResultMapConfig<?> resultMap, ResultSet resultSet);

	/**
	 * 返回查询结果影响的条数
	 * 
	 * @param resultMap
	 * @param resultSet
	 * @return
	 */
	public Integer getInteger(ResultMapConfig<?> resultMap, ResultSet resultSet);

	/**
	 * 将查询结果集整个映射为实体的列表
	 * 
	 * @param resultMap
	 * @param resultSet
	 * @return
	 */
	public <T> List<T> getList(ResultMapConfig<T> resultMap, ResultSet resultSet);

	/**
	 * 将查询结果映射为相应实体
	 * 
	 * @param resultMap
	 * @param resultSet
	 * @return
	 */
	public <T> T getModel(ResultMapConfig<T> resultMap, ResultSet resultSet);

	/**
	 * 根据resultMap自动映射相应的实体
	 * 
	 * @param resultMap
	 * @param resultSet
	 *            查询结果集
	 * @return 返回相应实体的引用
	 */
	public Object mapObject(ResultMapConfig<?> resultMap, ResultSet resultSet);
}
