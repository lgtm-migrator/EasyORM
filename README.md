# EASYORM
* 这是一个基于Java的持久化框架
* 本框架主要目的用于教学,使读者了解Java高级特性
* 大量中文注释提供,尽量提供准确的注释
* 如果对于某段code,有更好的解决方式,欢迎联系我
* 虽然已经足够小心,但难免出现bug,如若发现,请联系我

## 简明使用方式
* 本项目依赖于log4j,使用时需要将log4j(1.2.17以上版本)添加到Referenced Libraries,以及JDBCDriver和EasyORM本身,即,一个可用的基于EasyORM框架的项目至少需要3个库
* 编写需要映射的实体,实体属性的名字需要和数据库记录的列名一致(不要求大小写)
* 编写DAO接口,使用@SQL注解定义sql语句,SQL语句中的参数以?代替,**请注意,方法的参数顺序和sql语句中的?符号顺序需要一一对应**,返回类型可以为List,Boolean,Integer,或者是你自己定义的实体类.**请注意不要编写返回类型为void的方法**	
* 编写程序,创建SqlSessionFactory,现在推荐使用`DefaultSqlSessionFactory(String JDBCDriver, String url,
			String username, String passwd)`构造方法创建factory,xml配置文件的使用文档还在编写过程中
***
* SqlSessionFactory sessionFactory = new DefaultSqlSessionFactory(
				driver, url, username, password); //创建factory
* SqlSession sqlSession=factory.openSession(); //创建SqlSession
* daoInterface mapper=sqlSession.getMapper(daointerface.class); //获取代理对象
* MethodReturnType result=mapper.sqlname(param); //完成一次查询


## 思路简记
* 如果不使用连接池,SqlSession中并不建立连接,只有调用getConnection()时才建立连接,如果使用连接池,getConnection从连接池中取连接
* 代理调用Executor 
* SimpleExecutor中调用相应的反射方法,完成从行到实体的映射
* 将mapstatment传入executor,然后executor通过preparedstatment执行sql语句
* 在创建SqlSession时,扫描xml,注解,以及接口方法的返回类型,存储为相应配置实体.
* 在openSession()后,扫描一次接口和xml配置文件
* 当调用DAO接口中的方法,而SqlSession中找不到对应的MapStatment时,会调用Scanner扫描该方法并存储到MapStatment

## TO DO
* xml解析的时候,出错需要提示某一些字符需要转义

	`需要确认是否使用XML Schema代替DTD验证`

* ~~Executor中,select需要的结果集,需要的parameter应修改为ResultMap,以提高扩展性 -- 2016-2-2~~
* ~~需要用preparedStatment取代statment,无论从安全,性能,可读性等各方面  -- 2016-2-11~~
* ~~需要修改EasyormConfig(以及xml dtd) 需要用户指定实体所在的包~~
* ~~需要修改executor接口的parameter,使用mapstatment替换sqlStr,以提高扩展性~~
* ~~需要确定executor与proxy之间是如何调用的 --2016-2-6~~

	`代理调用解释器,解释器调用结果映射器`

* 需要设计一个功能,将相应的mapstatment,转化成可用的sql语句或者preparedstatment可用的形式

	`在使用preparedStatment时这个功能价值并不大,可是也方便用于调试`

* SqlSession中应该集成一个数据库连接池化功能,否则建立连接速度太慢了
* ~~需要给SqlSessionBuilder创建一个以配置实体构建的构造方法.~~
* ~~需要对JDBC返回的结果进行处理,通过配置文件和反射确定,是返回何种类型的结果~~
* ~~需要确定怎样判定返回单独实体还是列表 --2016-2-6~~

	`通过getGenericReturnType方法可以获取详细的泛型信息,以确定是否返回列表或者是其他容器类)`

* ~~MapStatment中的成员变量params需要将类型修改为Map,以保存相应变量的id和value~~
* ~~mappersconfig应该存储为Map --2016-2-7~~
* ~~需要确定是以xml配置文件优先,还是以注解优先 (在配置文件中确定,给用户选择) --2016-2-7~~
* ~~需要确定参数和MapStatment的对应关系~~

	`参数位置存储在mapstatment中,参数本身在运行的时候传递给executor`

* 需要确定反射是否可以获取方法真正的参数名而不是argN

	`暂时没有想到方法,待以后扩展`
	`已经确认反射无法获取方法的参数名`

* ~~需要了解如何根据包名获取包内所有类,否则只有让用户提供~~

	~~`需要使用非常复杂的流程获取,暂时需要用户手动提供`~~
	`借用互联网上的一个现成方法并封装在Scanner内`

* 需要对参数进行封装

	`需要对参数进行封装,以使用实体的属性查询`

* 现在使用SqlSession中的returnConnection关闭连接,之后需要设计一个连接管理器ConnectionManager

	`可以将连接池集成在连接管理器中`

* 需要确定是否可以让DAO接口直接返回ResultSet

	`需要确定ResultSet是否可以关闭Connection`

* 需要确定是否可以让DefaultSqlSession持有一个Connection
* ~~需要对代理进行缓存以提高性能,避免每次getMapper(somedaointerface)创建新代理~~

	`对代理预缓存的话,直接通过遍历DAO接口创建代理即可完成`

* 需要确定是否放弃通过XML配置


## 知识储备
* log4j
* Exception
* 注解
* 泛型
* JDBC,PreparedStatment
* Class 类加载器
* 反射,Reflection
* 动态代理,Proxy
* XML解析,DTD,XML Schema

## 更新日志
* 2016年2月11日 第一次整合测试完成

	`从配置到完成查询功能基本完成,唯一的不足是,不能通过定义DAO的package进行扫描`
	
* 2016年2月12日 整合log4j
* 2016年2月13日 整合DAO接口扫描
* 2016年2月14日 添加Apache2 开源协议/缓存代理对象/优化了Executor的执行流程
* 2016年2月15日 优化Executor执行流程/添加mapper dtd

## LICENSE
[UNDER THE APACHE LICENSE VERSION 2.0](http://www.apache.org/licenses/LICENSE-2.0 )

***
**SunTao UESTC mrls@live.cn**