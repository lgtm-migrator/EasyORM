package org.suntao.easyorm.map;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 简化结果集映射器
 * <p>
 * 最简单的结果映射<br>
 * 通过反射遍历实体属性并在resultset中查找同名的列并赋值<br>
 * 不对实体的父类的属性进行映射
 * 
 * @author suntao
 *
 */
public class SimpleResultMapping implements ResultMapping {

	@Override
	public Boolean getBoolean(ResultMapConfig<?> resultMap, ResultSet resultSet) {
		/**
		 * 查询结果行数
		 */
		Integer count = getInteger(resultMap, resultSet);
		Boolean result = false;
		if (count > 0)
			result = true;
		else
			result = false;
		return result;
	}

	@Override
	public Integer getInteger(ResultMapConfig<?> resultMap, ResultSet resultSet) {
		Integer result = 0;
		try {
			resultSet.last();
			result = resultSet.getRow();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public <T> List<T> getList(ResultMapConfig<T> resultMap, ResultSet resultSet) {
		List<T> result = new ArrayList<T>();
		try {
			while (resultSet.next()) {
				result.add(getModel(resultMap, resultSet));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 简易映射
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
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getModel(ResultMapConfig<T> resultMap, ResultSet resultSet) {
		Object result = null;
		try {
			// 构造实体
			result = Class.forName(resultMap.getModelClass().getName())
					.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		// 获取实体的各个字段(域)/field(不包含父类)
		Field[] fields = result.getClass().getDeclaredFields();
		// 遍历
		for (Field f : fields) {
			// 使该域可写
			f.setAccessible(true);
			// 获取该域的name
			String fieldName = f.getName();
			// 获取该域的type(class)
			Class<?> type = f.getType();
			try {
				Object value = null;
				// 判断,分别处理
				if (type.equals(String.class))
					value = resultSet.getString(fieldName);
				else if (type.equals(Integer.class) || type.equals(int.class))
					value = resultSet.getInt(fieldName);
				else if (type.equals(Date.class))
					value = resultSet.getDate(fieldName);
				else if (type.equals(Float.class) || type.equals(float.class))
					value = resultSet.getFloat(fieldName);
				else if (type.equals(Long.class) || type.equals(long.class))
					value = resultSet.getLong(fieldName);
				else if (type.equals(BigDecimal.class))
					value = resultSet.getBigDecimal(fieldName);
				else {
					// 此处需要log4j提示 没有相应类型处理方式
					continue;
				}
				// 如果value不为null,就填充(填充null的话会抛出异常,但是数据库中有些项也有可能是null)
				if (value != null)
					f.set(result, value);
			} catch (SQLException e) {
				// 此处需要log4j提示,根据name,没有相应字段
				e.printStackTrace();
				continue;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return (T) result;
	}

	@Override
	public Object mapObject(ResultMapConfig<?> resultMap, ResultSet resultSet) {
		Object result = null;
		try {
			ResultMappingType resultMappingType = resultMap.getResultType();
			switch (resultMappingType) {
			case BOOLEAN:
				result = getBoolean(resultMap, resultSet);
				break;
			case INTEGER:
				result = getInteger(resultMap, resultSet);
				break;
			case MODELLIST:
				result = getList(resultMap, resultSet);
				break;
			case ONEMODEL:
				try {
					if (resultSet.next())
						result = getModel(resultMap, resultSet);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case OTHER:
				/**
				 * TODO
				 * <p>
				 * 此处需要进行提示
				 */
				break;
			default:
				/**
				 * 此处需要进行提示
				 */
				break;
			}
		} catch (NullPointerException nullPointerException) {
			/**
			 * 此处需要提示
			 * <p>
			 * ResultMap没有配置返回类型
			 */
			nullPointerException.printStackTrace();
		}
		return result;
	}
}