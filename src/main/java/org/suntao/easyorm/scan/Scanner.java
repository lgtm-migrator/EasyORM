package org.suntao.easyorm.scan;

import java.util.Map;

import org.suntao.easyorm.map.MapStatement;
import org.suntao.easyorm.map.ResultMapConfig;

/**
 * 动态配置扫描
 * 
 * @author suntao
 *
 */
public interface Scanner {
	/**
	 * 获取已经扫描过的包含ID和ResultMap的Map
	 * <p>
	 * 为了保证获取成功请在调用前先调用scan()方法
	 * 
	 * @return
	 */
	Map<String, ResultMapConfig<?>> getScanedResultMap();

	/**
	 * 获取已经扫描过的包含ID和MapStatment的Map
	 * <p>
	 * 为了保证获取成功请在调用前先调用scan()方法
	 * 
	 * @return
	 */
	Map<String, MapStatement> getScannedMapStatement();

	/**
	 * 完成整个扫描并将信息存储在实体中
	 * 
	 * @return 是否成功
	 */
	Boolean scan();
}
