###Hbase
<http://www.tuicool.com/m/articles/zE7Nna>

HBase– Hadoop Database，是一个高可靠性、高性能、面向列、可伸缩的分布式存储系统，利用HBase技术可在廉价PC Server上搭建起大规模结构化存储集群。
HBase是GoogleBigtable的开源实现，类似Google Bigtable利用GFS作为其文件存储系统，HBase利用HadoopHDFS作为其文件存储系统；Google运行MapReduce来处理Bigtable中的海量数据，HBase同样利用Hadoop MapReduce来处理HBase中的海量数据；Google Bigtable利用 Chubby作为协同服务，HBase利用Zookeeper作为对应。
HBase位于结构化存储层，Hadoop HDFS为HBase提供了高可靠性的底层存储支持，Hadoop MapReduce为HBase提供了高性能的计算能力，Zookeeper为HBase提供了稳定服务和failover机制。
此外，Pig和Hive还为HBase提供了高层语言支持，使得在HBase上进行数据统计处理变的非常简单。 Sqoop则为HBase提供了方便的RDBMS数据导入功能，使得传统数据库数据向HBase中迁移变的非常方便。
![Hadoop EcoSystem](http://www.aboutyun.com/data/attachment/forum/201312/20/095332ythg8bktjnnietut.jpg)




reference:<http://www.aboutyun.com/thread-6138-1-1.html>


####HTable
![这里写图片描述](http://www.aboutyun.com/data/attachment/forum/201403/06/215743nzhk3ffheilpv73p.jpg)

#####Row key（行主键）
HBase不支持条件查询和Order by等查询，读取记录只能按Row key（及其range）或全表扫描，因此Row key需要根据业务来设计以利用其存储排序特性（Table按Row key字典序排序如1,10,100,11,2）提高性能。

#####Column Family（列族）
在表创建时声明，每个Column Family为一个存储单元。在上例中设计了一个HBase表blog，该表有两个列族：article和author。

#####Column（列）
HBase的每个列都属于一个列族，以列族名为前缀，如列article:title和article:content属于article列族，author:name和author:nickname属于author列族。
Column不用创建表时定义即可以动态新增，同一Column Family的Columns会群聚在一个存储单元上，并依Column key排序，因此设计时应将具有相同I/O特性的Column设计在一个Column Family上以提高性能。同时这里需要注意的是：这个列是可以增加和删除的，这和我们的传统数据库很大的区别。所以他适合非结构化数据。

#####Timestamp
HBase通过row和column确定一份数据，这份数据的值可能有多个版本，不同版本的值按照时间倒序排序，即最新的数据排在最前面，查询时默认返回最新版本。如上例中row key=1的author:nickname值有两个版本，分别为1317180070811对应的“一叶渡江”和1317180718830对应的“yedu”（对应到实际业务可以理解为在某时刻修改了nickname为yedu，但旧值仍然存在）。Timestamp默认为系统当前时间（精确到毫秒），也可以在写入数据时指定该值。

#####Value
每个值通过4个键唯一索引,tableName+RowKey+ColumnKey+Timestamp=>value，例如上例中{tableName=’blog’,RowKey=’1’,ColumnName=’author:nickname’,Timestamp=’ 1317180718830’}索引到的唯一值是“yedu”。

#####存储类型

TableName 是字符串
RowKey 和 ColumnName 是二进制值（Java 类型 byte[]）
Timestamp 是一个 64 位整数（Java 类型 long）
value 是一个字节数组（Java类型 byte[]）。

#####存储结构
可以简单的将HTable的存储结构理解为
![这里写图片描述](http://www.aboutyun.com/data/attachment/forum/201403/06/215743lqq4yy47rwqnw1c5.jpg)

即HTable按Row key自动排序，每个Row包含任意数量个Columns，Columns之间按Column key自动排序，每个Column包含任意数量个Values。理解该存储结构将有助于查询结果的迭代。


####什么情况需要HBase

#####半结构化或非结构化数据
对于数据结构字段不够确定或杂乱无章很难按一个概念去进行抽取的数据适合用HBase。以上面的例子为例，当业务发展需要存储author的email，phone，address信息时RDBMS需要停机维护，而HBase支持动态增加.

#####记录非常稀疏
RDBMS的行有多少列是固定的，为null的列浪费了存储空间。而如上文提到的，HBase为null的Column不会被存储，这样既节省了空间又提高了读性能。

#####多版本数据
如上文提到的根据Row key和Column key定位到的Value可以有任意数量的版本值，因此对于需要存储变动历史记录的数据，用HBase就非常方便了。比如上例中的author的Address是会变动的，业务上一般只需要最新的值，但有时可能需要查询到历史值。

#####超大数据量
当数据量越来越大，RDBMS数据库撑不住了，就出现了读写分离策略，通过一个Master专门负责写操作，多个Slave负责读操作，服务器成本倍增。随着压力增加，Master撑不住了，这时就要分库了，把关联不大的数据分开部署，一些join查询不能用了，需要借助中间层。随着数据量的进一步增加，一个表的记录越来越大，查询就变得很慢，于是又得搞分表，比如按ID取模分成多个表以减少单个表的记录数。经历过这些事的人都知道过程是多么的折腾。采用HBase就简单了，只需要加机器即可，HBase会自动水平切分扩展，跟Hadoop的无缝集成保障了其数据可靠性（HDFS）和海量数据分析的高性能（MapReduce）。

 #####Hbase的优缺点 
1. 列的可以动态增加，并且列为空就不存储数据,节省存储空间.

2. Hbase自动切分数据，使得数据存储自动具有水平scalability.

3. Hbase可以提供高并发读写操作的支持

Hbase的缺点：

1. 不能支持条件查询，只支持按照Row key来查询.

2. 暂时不能支持Master server的故障切换,当Master宕机后,整个存储系统就会挂掉.

####hbase术语及原理
hbase中出现了Region，RegionServer，ROOT- 和.META表，
Region是HBase数据存储和管理的基本单位。一个表中可以包含一个或多个Region。每个Region只能被一个RS（RegionServer）提供服务，RS可以同时服务多个Region，来自不同RS上的Region组合成表格的整体逻辑视图。
HBase中有两张特殊的Table，-ROOT-和.META.
META.：记录了用户表的Region信息，.META.可以有多个regoin
ROOT-：记录了.META.表的Region信息，-ROOT-只有一个region
当我们执行添加、删除数据的时候，相应的hbase的META，ROOT都会有相应的改变。

HBase 各部件的作用
http://www.aboutyun.com/thread-5862-1-1.html
hbase中什么是Region，什么是RegionServer？
http://www.aboutyun.com/thread-7159-1-1.html
HBASE原理简述
http://www.aboutyun.com/thread-7199-1-1.html



####habse  与 hadoop
比如我们的把mysql或则sqlserver放到D盘一样。hadoop提供了介质，hbase存储在hdfs上


####hive与hbase的十大区别与联系
共同点：
1.hbase与hive都是架构在hadoop之上的。都是用hadoop作为底层存储

区别：
2.Hive是建立在Hadoop之上为了减少MapReduce jobs编写工作的批处理系统，HBase是为了支持弥补Hadoop对实时操作的缺陷的项目 。
3.想象你在操作RMDB数据库，如果是全表扫描，就用Hive+Hadoop,如果是索引访问，就用HBase+Hadoop 。
4.Hive query就是MapReduce jobs可以从5分钟到数小时不止，HBase是非常高效的，肯定比Hive高效的多。
5.Hive本身不存储和计算数据，它完全依赖于HDFS和MapReduce，Hive中的表纯逻辑。
6.hive借用hadoop的MapReduce来完成一些hive中的命令的执行
7.hbase是物理表，不是逻辑表，提供一个超大的内存hash表，搜索引擎通过它来存储索引，方便查询操作。
8.hbase是列存储。
9.hdfs作为底层存储，hdfs是存放文件的系统，而Hbase负责组织文件。
10.hive需要用到hdfs存储文件，需要用到MapReduce计算框架。




reference:<http://www.aboutyun.com/thread-7073-1-1.html>

---
在Java Web 项目中使用HBase<http://my.oschina.net/lanzp/blog/398644>
通过Java Api与HBase交互<http://www.cnblogs.com/ggjucheng/p/3381328.html>
HBase 常用Shell命令<http://www.cnblogs.com/nexiyi/p/hbase_shell.html>