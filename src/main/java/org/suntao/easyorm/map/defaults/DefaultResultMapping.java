package org.suntao.easyorm.map.defaults;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.suntao.easyorm.map.ResultMapConfig;
import org.suntao.easyorm.map.ResultMapping;
import org.suntao.easyorm.map.ResultMappingType;
import org.suntao.easyorm.utils.Utils;

/**
 * 简化结果集映射器
 * <p>
 * 简单的结果映射<br>
 * 通过反射,遍历实体属性,在resultset中查找同名的列并赋值<br>
 * 不对实体父类的属性进行映射<br>
 * 在(不久的)未来会加入用户自定义类型的映射
 * 
 * @author suntao
 *
 */
public class DefaultResultMapping implements ResultMapping {
	private static Logger logger = Logger.getLogger(ResultMapping.class
			.getName());
	private Map<String, Field[]> cachedClassesFields;

	public DefaultResultMapping() {
		this.cachedClassesFields = new HashMap<String, Field[]>();
	}

	public DefaultResultMapping(Map<String, Field[]> cachedClassFields) {
		super();
		this.cachedClassesFields = cachedClassFields;
	}

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
				try {
					result.add(getModel(resultMap, resultSet));
				} catch (InstantiationException e) {
					// 当无法创建实体的时候,打印并跳出循环
					e.printStackTrace();
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getModel(ResultMapConfig<T> resultMap, ResultSet resultSet)
			throws InstantiationException {
		Object result = null;
		Class<?> resultClass = null;
		String resultClassName = null;
		try {
			// 动态构造实体
			result = Class.forName(resultMap.getModelClass().getName())
					.newInstance();
			resultClass = result.getClass();
			resultClassName = resultClass.getName();
		} catch (InstantiationException e) {
			// 如果该实体没有相应无参构造方法 提示并返回null
			logger.severe("请为实体: " + resultMap.getModelClass().getName()
					+ " 添加无参的构造方法,以动态构造实体\n当前无法构造实体,所以返回null");
			throw new InstantiationException("请为实体: "
					+ resultMap.getModelClass().getName() + " 添加无参的构造方法");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		// 从缓存中获取所有域(如果没有将先缓存)
		Field[] fields = Utils.getFieldFromCache(cachedClassesFields,
				resultClass);
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
					// TO DO 对其它对象进行映射
					logger.warning(String.format("没有Type{%s}的相应处理方式",
							type.toString()));
					continue;
				}
				// 如果value不为null,就填充(填充null的话会抛出异常,但是数据库中有些项也有可能是null)
				if (value != null)
					f.set(result, value);
			} catch (SQLException e) {
				logger.warning(String.format(
						"Class:%s,Field:%s,该字段在ResultSet中不存在", result
								.getClass().getName(), fieldName));
				// e.printStackTrace();
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
		logger.info(String.format("开始一次结果映射,返回类型为%s,目标实体为%s",
				resultMap.getResultType(), resultMap.getModelClass()));
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
					if (resultSet.next()) {
						try {
							result = getModel(resultMap, resultSet);
						} catch (InstantiationException e) {
							e.printStackTrace();
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			case OTHER:
				logger.warning("不可映射这种类型,请检查ResultMapConfig,返回null");
				logger.warning(resultMap.toString());
				result = null;
				break;
			default:
				logger.severe("出现了枚举不存在的类型,或者说本方法没有进行处理,请检查源码");
				break;
			}
		} catch (NullPointerException nullPointerException) {
			nullPointerException.printStackTrace();
		}
		logger.info("结果映射完成");
		return result;
	}
}
