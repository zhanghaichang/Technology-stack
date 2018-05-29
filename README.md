# Technology-stack 技术栈

简介


### 基础知识
* 	[java](basic-knowledge/java.md)
*  	[spring](basic-knowledge/spring.md)
*  	[spring boot](basic-knowledge/springboot.md)
*  	[spring cloud](basic-knowledge/springcloud.md)
*	[ibatis](basic-knowledge/ibatis.md)
*	[设计模式](basic-knowledge/常用的设计模式.md)
*	[Log日志](basic-knowledge/Log4j.md)


### 数据库
目前使用最多还是mysql，虽然单机性能比不上oracle，但免费开源，单机成本低且借助于分布式集群，可以有强大的输出能力。

*	[连接池](data-base/database-connection-pool.md)
* 	[事务](data-base/transaction.md)
* 	[分库分表](data-base/分库分表.md)
* 	[id生成器](data-base/id-generate.md)
* 	[读写分离](http://blog.csdn.net/itomge/article/details/6909240)
* 	[SQL调优](data-base/sql-optimize.md)
* 	[其它](data-base/other.md)


### web容器/协议/网络

* [负载均衡](web/load-balance.md)
* 服务器
	* [Nginx](web/Nginx.md)
	* [Tomcat](web/tomcat.md)
* 协议
	* [HTTP 协议](web/http协议.md)
	* [TCP 协议](web/tcp.md)
* [CDN](web/CDN.md)
* [其它](web/other.md)


### 常用三方工具包

* [Google Guava](open-source-framework/Goole-Guava.md)
* [fastJson](open-source-framework/fastJson.md)
* [log4J](http://blog.csdn.net/itomge/article/details/17913607)
* [commons-codec](open-source-framework/commons-codec.md)
* [commons-lang3](open-source-framework/commons-lang3.md)
* [commons-io](open-source-framework/commons-io.md)
* [Quartz](open-source-framework/Quartz.md)
* [HttpClient](open-source-framework/HttpClient.md)
* [Javassist](http://blog.csdn.net/itomge/article/details/7671294)


### 中间件

*	RPC框架
	* [dubbo](middle-software/dubbo.md)
	* [dubbox](https://www.oschina.net/p/dubbox)
	* [motan](https://github.com/weibocom/motan)
	* [Thrift](https://github.com/apache/thrift)
	* [RPC框架性能比较](middle-software/rpc-compare.md)

*   MQ消息
	* [ActiveMQ](https://github.com/apache/activemq)
	* [RabbitMQ](middle-software/RabbitMQ.md)
	* [Kafka](middle-software/kafka.md)
	* [RocketMQ](middle-software/RocketMQ.md)	

*   分布式缓存
	* [redis](open-source-framework/redis.md)
	* [codis]()
	* [memcache](http://blog.csdn.net/itomge/article/details/8035197)

*   本地缓存
	* [Guava](middle-software/guava.md)
	* [ehcache](middle-software/ehcache.md)
	 	
*   搜索
	* [Elasticsearch](middle-software/elasticsearch.md)

*   分布式数据框架
	* [cobar](middle-software/cobar.md)
	* [Mycat](middle-software/mycat.md)
	* [tsharding](middle-software/tsharding.md)
	* [tddl](https://github.com/alibaba/tb_tddl)
	* [sharding-jdbc](middle-software/sharding-jdbc.md)
	* [dbsplit](https://gitee.com/robertleepeak/dbsplit)

*	分布式协调服务
	* [zookeeper](middle-software/zookeeper.md)
		
*   配置管理

	* [super-diamond](other/super-diamond源码分析.md)
	* [disconf](https://www.oschina.net/p/disconf)
	* [apollo](middle-software/apollo.md)

*   分布式文件系统
	* [FastDFS](middle-software/FastDFS.md)

*   分布式任务调度框架

	* [Elastic-Job](https://github.com/elasticjob/elastic-job)
	* [详解当当网的分布式作业框架elastic-job](http://www.infoq.com/cn/articles/dangdang-distributed-work-framework-elastic-job)
	* [TBSchedule](http://blog.csdn.net/taosir_zhang/article/details/50728362)

*  其它
	* [数据库binlog的增量订阅&消费组件](https://github.com/alibaba/canal)
	* [数据库同步系统](https://github.com/alibaba/otter)
	* [TCC-Transaction](middle-software/TCC-Transaction.md)
	* [Netty](middle-software/Netty.md)

### 系统架构 

* [架构经验](system-architecture/architecture-experience.md)
* [经典案例](system-architecture/architecture-good-case.md)
* [通用技术方案选型](system-architecture/technology-selection.md)
* [编码前3000问](system-architecture/编码前3000问.md)



### 运维

*	[快速排查线上问题](ops/online-question.md)
*	[linux常用命令](ops/linux-commands.md)
*	[本地代码调试](ops/本地代码调试.md)
* 	[Docker](ops/docker.md)




### 其它

*	[常用软件工具](other/tool.md)
*	[一致性hash算法](other/一致性hash.md)
*   面试
	* [java面试题](other/java-interview.md)
	* [大数据面试题](other/bigdata-interview.md)
*	[回车与换行的区别](other/回车与换行的区别.md)
*   [github上fork项目后，如何同步更新后面提交](http://blog.csdn.net/qq1332479771/article/details/56087333)

