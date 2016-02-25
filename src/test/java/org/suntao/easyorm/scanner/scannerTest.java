package org.suntao.easyorm.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.suntao.easyorm.annotation.courseMapper;
import org.suntao.easyorm.annotation.userMapper;
import org.suntao.easyorm.map.MapStatement;
import org.suntao.easyorm.map.ResultMapConfig;
import org.suntao.easyorm.scan.defaults.DefaultScanner;

public class scannerTest {
	static DefaultScanner simpleScanner;

	@Before
	public void beforeTest() {
		simpleScanner = new DefaultScanner();
		List<Class<?>> list = new ArrayList();
		list.add(userMapper.class);
		list.add(courseMapper.class);
		simpleScanner.setDaoClasses(list);
		simpleScanner.scan();
	}

	@Test
	public void testFullScan() {
		System.out.println("-------------Full Test------------");
		Map<String, MapStatement> mapStatments = simpleScanner
				.getScannedMapStatement();
		Map<String, ResultMapConfig<?>> resultMaps = simpleScanner
				.getScanedResultMap();
		for (String k : mapStatments.keySet()) {
			MapStatement currentMapStatment = mapStatments.get(k);
			ResultMapConfig<?> currentResultMap = resultMaps.get(k);
			System.out.println(currentMapStatment.getInfoStr());
			Map<String, Integer> paramLocation = currentMapStatment
					.getParamLocation();
			if (paramLocation != null) {
				for (String ks : paramLocation.keySet()) {
					System.out.println(ks + paramLocation.get(ks));
				}
			}
			System.out.println(currentResultMap.getInfoStr());
		}
		System.out.println("-------------Full Test------------");
	}
}
