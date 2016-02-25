package org.suntao.easyorm.executor;

import org.suntao.easyorm.map.MapStatement;

public interface Executor {

	/**
	 * 执行mapStatment
	 * 
	 * @param mapStatment
	 *            sql语句实体
	 * @param params
	 *            参数
	 * @return 结果
	 *         <p>
	 *         此处结果应该是映射后的结果
	 */
	Object execute(MapStatement mapStatment, Object[] params);
}
