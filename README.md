# EASYORM
* 这是一个基于Java的持久化框架
* 本框架主要目的用于教学,使读者了解Java高级特性
* 大量中文注释提供,尽量提供准确的注释
* 如果有相关的改进建议,或者发现了Bug,请联系我

## 简明使用方式
一个可用的基于EasyORM项目,需要JDBC Driver和EasyORM jar
编写需要映射的实体,实体属性的名字需要和数据库记录的列名一致(不要求大小写)
编写DAO接口,使用@SQL注解定义sql语句,SQL语句中的参数以?代替,**请注意,方法的参数顺序和sql语句中的?符号顺序需要一一对应**,返回类型可以为List,Boolean,Integer,或者是你自己定义的实体类.**请注意不要编写返回类型为void的方法**	
编写程序,创建SqlSessionFactory
使用factory.openSession创建SqlSession,使用SqlSession进行数据操作
<BLOCKQUOTE>//源码示例
SqlSessionFactory factory = new DefaultSqlSessionFactory(
				driver, url, username, password); //创建factory
SqlSession sqlSession=factory.openSession(); //创建SqlSession
daoInterface mapper=sqlSession.getMapper(daointerface.class); //获取代理对象
MethodReturnType result=mapper.sqlname(param); //完成一次查询
</BLOCKQUOTE>
也可以直接调用SqlSession的默认方法,但注意传入的实体一定要使用DatabaseModel注解

## 思路简记
* 如果不使用连接池,SqlSession中并不建立连接,只有调用getConnection()时才建立连接,如果使用连接池,getConnection从连接池中取连接
* ProxyHandler调用Executor 
* 将mapstatement传入executor,然后executor通过preparedstatment执行sql语句,获取ResultSet
* DefaultExecutor中调用ResultMapping的方法,完成从行到实体的映射
* 在创建SqlSession时,扫描xml,注解,以及接口方法的返回类型,存储为相应配置实体.
* 在openSession()后,扫描一次接口和xml配置文件
* 当调用DAO接口中的方法,而SqlSession中找不到对应的MapStatement时,会调用Scanner扫描并缓存
* 当需要获取一个实体的所有域的时候,会先从SqlSession中的modelFieldsCache中查找,如果没有,会扫描并缓存
* 当需要获取一个代理的时候,会先在MapperProxyBuilder中的proxyCache中查找,如果没有,会先创建并缓存
* 无论是调用默认方法,还是调用DAO,最终都是交由DefaultExecutor来执行

## TO DO
* ~~XML解析的时候,出错需要提示某一些字符需要转义~~

	`需要确认是否使用XML Schema代替DTD验证`
	`XS复杂度太高,暂时放弃`
    `暂时放弃XML解析`
    
* ~~Executor中,select需要的结果集,需要的parameter应修改为ResultMap,以提高扩展性 -- 2016-2-2~~
* ~~需要用preparedStatement取代statement,无论从安全,性能,可读性等各方面  -- 2016-2-11~~
* ~~需要修改EasyormConfig(以及xml dtd) 需要用户指定实体所在的包~~
* ~~需要修改executor接口的parameter,使用mapstatement替换sqlStr,以提高扩展性~~
* ~~需要确定executor与proxy之间是如何调用的 --2016-2-6~~

	`代理调用解释器,解释器调用结果映射器`

* ~~需要设计一个功能,将相应的mapstatement,转化成可用的sql语句或者preparedstatment可用的形式~~

	`在使用preparedStatement时这个功能价值并不大,可是也方便用于调试`

* ~~SqlSession中应该集成一个数据库连接池化功能,因为建立连接的所消耗资源较大~~
* ~~需要给SqlSessionBuilder创建一个以配置实体构建的构造方法.~~
* ~~需要对JDBC返回的结果进行处理,通过配置文件和反射确定,是返回何种类型的结果~~
* ~~需要确定怎样判定返回单独实体还是列表 --2016-2-6~~

	`通过getGenericReturnType方法可以获取详细的泛型信息,以确定是否返回列表或者是其他容器类)`

* ~~MapStatment中的成员变量params需要将类型修改为Map,以保存相应变量的id和value~~
* ~~mappersconfig应该存储为Map --2016-2-7~~
* ~~需要确定是以xml配置文件优先,还是以注解优先 (在配置文件中确定,给用户选择) --2016-2-7~~
* ~~需要确定参数和MapStatment的对应关系~~

	`参数位置存储在mapstatment中,参数本身在运行的时候传递给executor`

* ~~需要确定反射是否可以获取方法真正的参数名而不是argN~~

	`暂时没有想到方法,待以后扩展`
	`已经确认反射无法获取方法的参数名`

* ~~需要了解如何根据包名获取包内所有类,否则只有让用户提供~~

	~~`需要使用非常复杂的流程获取,暂时需要用户手动提供`~~
	`借用互联网上的一个现成方法并封装在Scanner内`

* 需要对参数进行封装

	`需要对参数进行封装,以使用实体的属性查询`

* ~~现在使用SqlSession中的returnConnection关闭连接,之后需要设计一个连接管理器ConnectionManager~~

	`可以将连接池集成在连接管理器中`

* 需要确定是否可以让DAO接口直接返回ResultSet

	`需要确定ResultSet是否可以关闭Connection`
	`ResultSet可以获取Connection并关闭`

* ~~需要确定是否可以让DefaultSqlSession持有一个Connection~~

    `DefaultSqlSession在非池化的情况下,将不持有连接`
    
* ~~需要对代理进行缓存以提高性能,避免每次getMapper(somedaointerface)创建新代理~~

	`对代理预缓存的话,直接通过遍历DAO接口创建代理即可完成`

* ~~需要确定是否放弃通过XML配置~~

	`现在认为1.0版本并不需要XML配置,等到2.0版本重构时再重新考虑`
	`并且为了简化整个配置,XML文件尽量不要引入`

* ~~需要对连接池加上同步锁,以保证线程安全~~

	`连接池需要更多测试,以保证连接不丢失/不被意外关闭`

* ~~需要对DefaultExecutor添加几个默认的方法,支持save,update,delete等方法~~

* ~~需要确认是否可以用AOP,取代log4j进行调试~~
	`使用Java原生Log替代log4j`
* ~~准备剥离关于XML文件的处理部分~~
* ~~需要删除不必要的代码部分,以提升可读性~~
* ~~需要对默认方法抛出SQL Exception,如果可以抛出相关语句~~
* ~~需要将默认的logger提示方式改为Java自带的log~~
* ~~需要将某些log改为抛出Exception~~
* ~~将一个实体的Field找一个地方缓存~~
    `为了唯一,不能放在MapStatement和ResultMapConfig里面`
    `在结果映射中添加了针对域的缓存`
    `缓存放在SqlSession中,其它地方使用引用访问`
* 进行结果映射的时候,如若发现有用户自定义的类,需要进行扫描(缓存),并递归进行填充

## 知识储备
* ~~log4j~~ 已从项目移除
* Exception
* 注解
* 泛型
* JDBC,PreparedStatement
* Class,类加载器
* 反射,Reflection
* 动态代理,Proxy
* ~~XML解析,DTD,XML Schema~~ 已从项目移除

## 更新日志
* 2016年2月11日 第一次整合测试完成
* 2016年2月12日 整合log4j
* 2016年2月13日 整合DAO接口扫描
* 2016年2月14日 添加Apache2 开源协议/缓存代理对象/优化了Executor的执行流程
* 2016年2月15日 优化Executor执行流程/添加mapper dtd
* 2016年2月18日 添加连接池并持续测试可靠性(预计需要一周时间)
* 2016年2月21-24日 为SqlSession添加几个默认方法
* 2016年2月25日 各种更名,提高可读性
* 2016年3月8日 整合连接池,移除XML配置
* 2016年3月9日 移除log4j,使用Java原生的Logger
* 2016年3月10日 添加对Field[]的缓存

## LICENSE
[UNDER THE APACHE LICENSE VERSION 2.0](http://www.apache.org/licenses/LICENSE-2.0 )

***
**SunTao UESTC mrls@live.cn**