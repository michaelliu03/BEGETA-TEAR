###Hadoop
在Hadoop的系统中，会有一台Master，主要负责NameNode的工作以及JobTracker的工作。JobTracker的主要职责就是启动、跟踪和调度各个Slave的任务执行。还会有多台Slave，每一台Slave通常具有DataNode的功能并负责TaskTracker的工作。TaskTracker根据应用要求来结合本地数据执行Map任务以及Reduce任务。
说到这里，就要提到分布式计算最重要的一个设计点：Moving Computation is Cheaper than Moving Data。就是在分布式处理中，移动数据的代价总是高于转移计算的代价。简单来说就是分而治之的工作，需要将数据也分而存储，本地任务处理本地数据然后归总，这样才会保证分布式计算的高效性。



####Hadoop Common
Hadoop体系最底层的一个模块，为Hadoop各子项目提供各种工具，如：配置文件和日志操作等。

####Avro
Avro是doug cutting主持的RPC项目，有点类似Google的protobuf和Facebook的thrift。avro用来做以后hadoop的RPC，使hadoop的RPC模块通信速度更快、数据结构更紧凑。

####Chukwa
Chukwa是基于Hadoop的大集群监控系统，由yahoo贡献。

####HBase
基于Hadoop Distributed File System，是一个开源的，基于列存储模型的分布式数据库。
HBase以Google BigTable为蓝本。项目的目标就是快速在主机内数十亿行数据中定位所需的数据并访问它。HBase利用MapReduce来处理内部的海量数据。同时Hive和Pig都可以与HBase组合使用，Hive和Pig还为HBase提供了高层语言支持，使得在HBase上进行数据统计处理变的非常简单。
但为了授权随机存储数据，HBase也做出了一些限制：例如Hive与HBase的性能比原生在HDFS之上的Hive要慢4-5倍。同时HBase大约可存储PB级的数据，与之相比HDFS的容量限制达到30PB。HBase不适合用于ad-hoc分析，HBase更适合整合大数据作为大型应用的一部分，包括日志、计算以及时间序列数据。

####HDFS
分布式文件系统 Hadoop Distributed File System（Hadoop的核心）
在处理大数据的过程中，当Hadoop集群中的服务器出现错误时，整个计算过程并不会终止。同时HFDS可保障在整个集群中发生故障错误时的数据冗余。当计算完成时将结果写入HFDS的一个节点之中。

大数据的文件存储技术 
在大数据领域中，较为出名的海量文件存储技术有Google的GFS和Hadoop的HDFS，HDFS是GFS的开源实现。它们均采用分布式存储的方式存储数据，用冗余存储的模式保证数据可靠性，文件块被复制存储在不同的存储节点上，默认存储三份副本。


#####分布式文件系统基本的几个特点：
1. 对于整个集群有单一的命名空间。
2. 数据一致性。适合一次写入多次读取的模型，客户端在文件没有被成功创建之前无法看到文件存在。
3. 文件会被分割成多个文件块，每个文件块被分配存储到数据节点上，而且根据配置会由复制文件块来保证数据的安全性。

#####整个HDFS三个重要角色：
NameNode、DataNode和Client。NameNode可以看作是分布式文件系统中的管理者，主要负责管理文件系统的命名空间、集群配置信息和存储块的复制等。NameNode会将文件系统的Meta-data存储在内存中，这些信息主要包括了文件信息、每一个文件对应的文件块的信息和每一个文件块在DataNode的信息等。DataNode是文件存储的基本单元，它将Block存储在本地文件系统中，保存了Block的Meta-data，同时周期性地将所有存在的Block信息发送给NameNode。Client就是需要获取分布式文件系统文件的应用程序。这里通过三个操作来说明他们之间的交互关系。
#####文件写入：
1. Client向NameNode发起文件写入的请求。
2. NameNode根据文件大小和文件块配置情况，返回给Client它所管理部分DataNode的信息。
3. Client将文件划分为多个Block，根据DataNode的地址信息，按顺序写入到每一个DataNode块中。
#####文件读取：
1. Client向NameNode发起文件读取的请求。
2. NameNode返回文件存储的DataNode的信息。
3. Client读取文件信息。
#####文件Block复制：
1. NameNode发现部分文件的Block不符合最小复制数或 者部分DataNode失效。
2. 通知DataNode相互复制Block。
3. DataNode开始直接相互复制。

####Hive
hive类似CloudBase，也是基于hadoop分布式计算平台上的提供data warehouse的sql功能的一套软件。使得存储在hadoop里面的海量数据的汇总，即席查询简单化。hive提供了一套QL的查询语言，以sql为基础，使用起来很方便。

####MapReduce
实现了MapReduce编程框架
在Map前还可能会对输入的数据有Split（分割）的过程，保证任务并行效率，在Map之后还会有Shuffle（混合）的过程，对于提高Reduce的效率以及减小数据传输的压力有很大的帮助。

####Pig
Pig是SQL-like语言，是在MapReduce上构建的一种高级查询语言，把一些运算编译进MapReduce模型的Map和Reduce中，并且用户可以定义自己的功能。Yahoo网格运算部门开发的又一个克隆Google的项目Sawzall。
Pig是一种编程语言，它简化了Hadoop常见的工作任务。Pig相比Hive相对轻量，它主要的优势是相比于直接使用Hadoop Java APIs可大幅削减代码量。

#### Yarn
资源管理平台


####ZooKeeper
Zookeeper是Google的Chubby一个开源的实现。它是一个针对大型分布式系统的可靠协调系统，提供的功能包括：配置维护、名字服务、分布式同步、组服务等。ZooKeeper的目标就是封装好复杂易出错的关键服务，将简单易用的接口和性能高效、功能稳定的系统提供给用户。

####Sqoop
功能主要是从关系数据库导入数据到Hadoop，并可直接导入到HFDS或Hive。

####Flume
设计旨在直接将流数据或日志数据导入HDFS。

####Chukwa

###summary
+ 改善数据访问：HBase、Sqoop以及Flume
+ 负责协调工作流程的ZooKeeper和Oozie
+  机器学习：Mahout (机器学习算法的 mapreduce 实现库）
+ ELK
    - ElasticSearch
    - Logstash
    - Kibana




</br>
reference：<http://blog.sina.com.cn/s/blog_4d1865f001013xuf.html>


----
<http://www.tuicool.com/m/articles/zE7Nna>
<http://m.oschina.net/blog/355298>
<http://www.aboutyun.com/thread-6855-1-1.html>
<http://www.aboutyun.com/thread-7394-1-1.html>
