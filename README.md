# EASYORM
* 这是一个基于Java的持久化框架
* 本框架主要目的用于教学,使读者了解Java高级特性
* 大量中文注释提供,尽量提供准确的注释
* 由于本人在架构设计和代码方面的经验不足,所以代码不怎么好看,请谅解,此外,框架构成有可能大幅度改变
* 如果使用者对于某一个方法,有更好的解决方式,欢迎联系我
* 虽然已经足够小心,但难免出现bug,如若发现请联系我

## 包简介
* org.suntao.easyorm.map 实体映射
* org.suntao.easyorm.xmlparse xml文件解析
* org.suntao.easyorm.session 数据库连接
* org.suntao.easyorm.proxy 代理相关 
* org.suntao.easyorm.annotation 注解
* org.suntao.easyorm.scan 配置扫描器
* org.suntao.easyorm.executor SQL解释器

## 思路简记
* 如果不使用连接池,SqlSession中并不建立连接,只有调用getConnection()时才建立连接,如果使用连接池,getConnection从连接池中取连接
* 代理调用Executor 
* SimpleExecutor中调用相应的反射方法,完成从行到实体的映射
* 将mapstatment传入executor,然后executor通过preparedstatment执行sql语句
* 在创建SqlSession时,扫描xml,注解,以及接口方法的返回类型,存储为相应配置实体.

## TO DO
* xml解析的时候,出错需要提示某一些字符需要转义
* ~~Executor中,select需要的结果集,需要的parameter应修改为ResultMap,以提高扩展性 -- 2016-2-2~~
* ~~需要用preparedStatment取代statment,无论从安全,性能,可读性等各方面  -- 2016-2-11~~
* ~~需要修改EasyormConfig(以及xml dtd) 需要用户指定实体所在的包~~
* ~~需要修改executorj接口的parameter,使用mapstatment替换sqlStr,以提高扩展性 -- 2016-2-4 ~~
* ~~需要确定executor与proxy之间是如何调用的 --2016-2-6~~
> 代理调用解释器,解释器调用结果映射器
* 需要设计一个功能,将相应的mapstatment,转化成可用的sql语句或者preparedstatment可用的形式
> 在使用preparedStatment时这个功能价值并不大,可是也方便用于调试
* SqlSession中应该集成一个数据库连接池化功能,否则建立连接速度太慢了
* ~~需要给SqlSessionBuilder创建一个以配置实体构建的构造方法.~~
* ~~需要对JDBC返回的结果进行处理,通过配置文件和反射确定,是返回何种类型的结果~~
* ~~需要确定怎样判定返回单独实体还是列表 --2016-2-6~~
> 通过getGenericReturnType方法可以获取详细的泛型信息,以确定是否返回列表或者是其他容器类)
* ~~MapStatment中的成员变量params需要将类型修改为Map,以保存相应变量的id和value~~
* ~~mappersconfig应该存储为Map --2016-2-7 ~~
* ~~需要确定是以xml配置文件优先,还是以注解优先 (在配置文件中确定,给用户选择) --2016-2-7 ~~
* ~~需要确定参数和MapStatment的对应关系~~
> 参数位置存储在mapstatment中,参数本身在运行的时候传递给exector
* 需要确定反射是否可以获取方法真正的参数名而不是argN
> 暂时没有想到方法,待以后扩展
* 需要了解如何根据包名获取包内所有类,否则只有让用户提供
> 需要使用非常复杂的流程获取,暂时需要用户手动提供
* 需要对参数进行封装
> 需要对参数进行封装,以使用实体的属性查询
* 现在使用SqlSession中的returnConnection关闭连接,之后需要设计一个连接管理器ConnectionManager
> 可以将连接池集成在连接管理器中
* 需要确定是否可以直接映射ResultSet

## 知识储备
* log4j
* Exception
* 注解
* 泛型
* JDBC,PreparedStatment
* Class 类加载器
* 反射
* 动态代理
* xml解析

## 更新日志
* 2016年2月11日 第一次整合测试完成
> 从配置到完成查询功能基本完成,唯一的不足是dao接口不能自动扫描

***
** SunTao UESTC mrls@live.cn **