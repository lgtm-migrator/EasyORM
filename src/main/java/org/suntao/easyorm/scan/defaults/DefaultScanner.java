package org.suntao.easyorm.scan.defaults;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import org.suntao.easyorm.annotation.Param;
import org.suntao.easyorm.annotation.SQL;
import org.suntao.easyorm.configuration.EasyormConfig;
import org.suntao.easyorm.map.MapStatement;
import org.suntao.easyorm.map.ResultMapConfig;
import org.suntao.easyorm.map.ResultMappingType;
import org.suntao.easyorm.scan.Scanner;

/**
 * 简化扫描器
 * <p>
 * 只扫描所在接口明确定义的方法,不扫描父类的方法
 * 
 * @author suntao
 *
 */
public class DefaultScanner implements Scanner {
	private List<Class<?>> daoClasses;
	private String daoPath;
	private Map<String, MapStatement> scannedMapStatment;
	private Map<String, ResultMapConfig<?>> scannedResultMap;
	private static Logger logger = Logger.getLogger(Scanner.class.getName());

	public DefaultScanner() {
		super();
	}

	public DefaultScanner(String daoPath) {
		super();
		this.daoPath = daoPath;
		this.daoClasses = new ArrayList<Class<?>>();
		scanDaoClasses();
	}

	public DefaultScanner(EasyormConfig easyormConfig) {
		super();
		this.daoPath = easyormConfig.getDaoPath();
		this.daoClasses = new ArrayList<Class<?>>();
		scanDaoClasses();
	}

	public List<Class<?>> getDaoClasses() {
		return daoClasses;
	}

	public String getDaoPath() {
		return daoPath;
	}

	@Override
	public Map<String, ResultMapConfig<?>> getScanedResultMap() {
		return scannedResultMap;
	}

	@Override
	public Map<String, MapStatement> getScannedMapStatement() {
		return scannedMapStatment;
	}

	@Override
	public Boolean scan() {
		Boolean result = false;
		Map<String, MapStatement> scanedMapStatment = new HashMap<String, MapStatement>();
		Map<String, ResultMapConfig<?>> scanedResultMap = new HashMap<String, ResultMapConfig<?>>();
		logger.info("开始对DAO接口的扫描,并存储为MapStatment和ResultMap");
		try {
			for (Class<?> currentClass : daoClasses) {
				for (Method currentMethod : currentClass.getDeclaredMethods()) {
					String id = null;
					id = String.format("%s.%s", currentClass.getName(),
							currentMethod.getName());
					ResultMapConfig<?> currentScanningResultMap = scanResultMapConfigOfMethod(currentMethod);
					MapStatement currentScanningMapStatment = scanMapStatmentOfMethod(currentMethod);
					currentScanningMapStatment
							.setResultMap(currentScanningResultMap);
					currentScanningMapStatment.setId(id);
					currentScanningResultMap.setResultMapID(id);
					scanedMapStatment.put(id, currentScanningMapStatment);
					scanedResultMap.put(id, currentScanningResultMap);
				}
			}
			this.scannedResultMap = scanedResultMap;
			this.scannedMapStatment = scanedMapStatment;
			logger.info(String.format(
					"扫描完成,扫描的MapStatment的size为%d,ResultMap的size为:%d",
					scanedMapStatment.size(), scanedResultMap.size()));
			result = true;
		} catch (Exception e) {
			logger.severe("由于各种原因扫描失败");
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	/**
	 * 根据daopath寻找DAO的各个class并存储在daoclasses中
	 */
	public void scanDaoClasses() {
		this.daoClasses = new ArrayList<Class<?>>();
		if (daoClasses != null && daoPath != null) {
			try {
				Set<Class<?>> classes = getClasses(daoPath);
				for (Class<?> c : classes) {
					daoClasses.add(c);
				}
			} catch (Exception e) {
				logger.warning("根据package获取Class列表发生错误,Exception:"
						+ e.getMessage());
			}
		}

	}

	/**
	 * 扫描一个方法的Mapstatment
	 * 
	 * @param method
	 * @return
	 */
	public MapStatement scanMapStatmentOfMethod(Method method) {
		MapStatement result = new MapStatement();
		SQL sql;
		String statmentSQL = null;
		sql = method.getAnnotation(SQL.class);
		if (sql != null && !sql.value().isEmpty())
			statmentSQL = sql.value();
		Parameter[] parameters = method.getParameters();
		Map<String, Integer> paramLocation = new HashMap<String, Integer>();
		if (parameters != null && parameters.length > 0) {
			for (int i = 0; i < parameters.length; i++) {
				Parameter currentParameter = parameters[i];
				Integer currentIndexOfParameter = i;
				Param paramAnno = currentParameter.getAnnotation(Param.class);
				String paramName = null;
				if (paramAnno != null && !paramAnno.paramname().isEmpty()) {
					paramName = paramAnno.paramname();
				} else {
					paramName = currentParameter.getName();
				}
				paramLocation.put(paramName, currentIndexOfParameter);
			}

		}
		result.setStatementSQL(statmentSQL);
		result.setParamLocation(paramLocation);
		return result;
	}

	/**
	 * 针对你方法的扫描ResultMapConfig
	 * 
	 * @param method
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ResultMapConfig scanResultMapConfigOfMethod(Method method) {
		ResultMapConfig resultMap = new ResultMapConfig();
		/**
		 * DAO方法返回类型
		 * <p>
		 * 返回的是List,Boolean还是其他
		 */
		Class<?> returnClass = method.getReturnType();
		/**
		 * 返回实体类型
		 * <p>
		 * 自定义的实体类<br>
		 * 返回列表和单个实体需要使用
		 */
		Class<?> modelClass = null;
		String resultMapId = null;// 此resultMap的ID
		ResultMappingType resultMappingType = null;
		/**
		 * 按返回类型分别处理
		 * <p>
		 * 这部分code是不完备的<br>
		 * 重构的时候需要仔细测试
		 */
		if (returnClass.equals(Integer.class) || returnClass.equals(int.class)) {
			resultMappingType = ResultMappingType.INTEGER;
		} else if (returnClass.equals(Boolean.class)
				|| returnClass.equals(boolean.class)) {
			resultMappingType = ResultMappingType.BOOLEAN;
		} else if (returnClass.equals(List.class)) {
			// 强制转换以获取泛型参数
			ParameterizedType parameterizedType = (ParameterizedType) method
					.getGenericReturnType();
			Type[] types = parameterizedType.getActualTypeArguments();
			if (types.length != 1) {
				logger.severe("List 参数过多过少");
				// 如果List的参数过多或者过少= =,这应该不会发生
				resultMappingType = ResultMappingType.OTHER;
			}
			try {
				modelClass = Class.forName(types[0].getTypeName());
			} catch (ClassNotFoundException e) {
				logger.severe(String.format("并没有%s这个类%s",
						types[0].getTypeName(), e));
			}
			resultMappingType = ResultMappingType.MODELLIST;
		} else if (returnClass.equals(Void.class)) {
			logger.severe("方法不应该定义空返回类型,如果只想执行语句的话,可以定义int返回");
			resultMappingType = ResultMappingType.OTHER;
		} else if (!returnClass.isPrimitive()) {
			modelClass = method.getReturnType();
			resultMappingType = ResultMappingType.ONEMODEL;
		} else {
			logger.warning(String.format("当前类型不受到支持,%s", returnClass));
			resultMappingType = ResultMappingType.OTHER;
		}
		resultMap.setResultMapID(resultMapId);
		resultMap.setModelClass(modelClass);
		resultMap.setResultType(resultMappingType);
		return resultMap;
	}

	public void setDaoClasses(List<Class<?>> daoClasses) {
		this.daoClasses = daoClasses;
	}

	public void setDaoPath(String daoPath) {
		this.daoPath = daoPath;
	}

	/**
	 * 从包package中获取所有的Class
	 * 
	 * @author From Internet Website<br>
	 *         http://guoliangqi.iteye.com/blog/644876
	 * @param packname
	 * @return
	 */
	public static Set<Class<?>> getClasses(String packname) {

		// 第一个class类的集合
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		// 是否循环迭代
		boolean recursive = false;
		// 获取包的名字 并进行替换
		String packageName = packname;
		String packageDirName = packageName.replace('.', '/');
		// 定义一个枚举的集合 并进行循环来处理这个目录下的things
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader()
					.getResources(packageDirName);
			// 循环迭代下去
			while (dirs.hasMoreElements()) {
				// 获取下一个元素
				URL url = dirs.nextElement();
				// 得到协议的名称
				String protocol = url.getProtocol();
				// 如果是以文件的形式保存在服务器上
				if ("file".equals(protocol)) {
					// System.err.println("file类型的扫描");
					// 获取包的物理路径
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					// 以文件的方式扫描整个包下的文件 并添加到集合中
					findAndAddClassesInPackageByFile(packageName, filePath,
							recursive, classes);
				} else if ("jar".equals(protocol)) {
					// 如果是jar包文件
					// 定义一个JarFile
					// System.err.println("jar类型的扫描");
					JarFile jar;
					try {
						// 获取jar
						jar = ((JarURLConnection) url.openConnection())
								.getJarFile();
						// 从此jar包 得到一个枚举类
						Enumeration<JarEntry> entries = jar.entries();
						// 同样的进行循环迭代
						while (entries.hasMoreElements()) {
							// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
							JarEntry entry = entries.nextElement();
							String name = entry.getName();
							// 如果是以/开头的
							if (name.charAt(0) == '/') {
								// 获取后面的字符串
								name = name.substring(1);
							}
							// 如果前半部分和定义的包名相同
							if (name.startsWith(packageDirName)) {
								int idx = name.lastIndexOf('/');
								// 如果以"/"结尾 是一个包
								if (idx != -1) {
									// 获取包名 把"/"替换成"."
									packageName = name.substring(0, idx)
											.replace('/', '.');
								}
								// 如果可以迭代下去 并且是一个包
								if ((idx != -1) || recursive) {
									// 如果是一个.class文件 而且不是目录
									if (name.endsWith(".class")
											&& !entry.isDirectory()) {
										// 去掉后面的".class" 获取真正的类名
										String className = name.substring(
												packageName.length() + 1,
												name.length() - 6);
										try {
											// 添加到classes
											classes.add(Class
													.forName(packageName + '.'
															+ className));
										} catch (ClassNotFoundException e) {
											// log
											// .severe("添加用户自定义视图类错误 找不到此类的.class文件");
											e.printStackTrace();
										}
									}
								}
							}
						}
					} catch (IOException e) {
						// log.severe("在扫描用户定义视图时从jar包获取文件出错");
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return classes;
	}

	/**
	 * 以文件的形式来获取包下的所有Class
	 * 
	 * @author From Internet Website<br>
	 *         http://guoliangqi.iteye.com/blog/644876
	 * 
	 * @param packageName
	 * @param packagePath
	 * @param recursive
	 * @param classes
	 */
	public static void findAndAddClassesInPackageByFile(String packageName,
			String packagePath, final boolean recursive, Set<Class<?>> classes) {
		// 获取此包的目录 建立一个File
		File dir = new File(packagePath);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			// log.warn("用户定义包名 " + packageName + " 下没有任何文件");
			return;
		}
		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles(new FileFilter() {
			// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
			@Override
			public boolean accept(File file) {
				return (recursive && file.isDirectory())
						|| (file.getName().endsWith(".class"));
			}
		});
		// 循环所有文件
		for (File file : dirfiles) {
			// 如果是目录 则继续扫描
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(
						packageName + "." + file.getName(),
						file.getAbsolutePath(), recursive, classes);
			} else {
				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0,
						file.getName().length() - 6);
				try {
					// 添加到集合中去
					// classes.add(Class.forName(packageName + '.' +
					// className));
					// 经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
					classes.add(Thread.currentThread().getContextClassLoader()
							.loadClass(packageName + '.' + className));
				} catch (ClassNotFoundException e) {
					// log.severe("添加用户自定义视图类错误 找不到此类的.class文件");
					e.printStackTrace();
				}
			}
		}
	}
}
