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
	 * 单个实体映射
	 * <p>
	 * 通过resultMap中的class对象,动态构造实体<br>
	 * 并通过反射,遍历实体的fields和相应type,从resultSet获取数据并填充
	 * <p>
	 * 需要注意的是,field中的name需要与column相对应(不区分大小写)
	 * <p>
	 * 此方法只对resultSet当前行操作
	 * 
	 * 
	 * @param objectClass
	 *            需要返回的对象Class
	 * @param resultSet
	 *            查询的结果集
	 * @return 对应实体
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
