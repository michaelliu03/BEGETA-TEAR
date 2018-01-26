
+ Flume-NG源码分析-整体结构及配置载入分析:<http://www.jianshu.com/p/0187459831af>
---

### Flume
+ <http://flume.apache.org/>
+ Flume是Cloudera提供的一个高可用的，高可靠的，分布式的海量日志采集、聚合和传输的系统，Flume支持在日志系统中定制各类数据发送方，用于收集数据；同时，Flume提供对数据进行简单处理，并写到各种数据接受方（可定制）的能力。
+ 当前Flume有两个版本Flume 0.9X版本的统称Flume-og，Flume1.X版本的统称Flume-ng。由于Flume-ng经过重大重构，与Flume-og有很大不同.
    1. Flume OG:Flume original generation 即Flume 0.9.x版本
    2. Flume NG:Flume next generation ，即Flume 1.x版本
+ Flume NG，它摒弃了Master和zookeeper，collector也没有了，web配置台也没有了，只剩下source，sink和channel，此时一个Agent的概念包括source,channel和sink，完全由一个分布式系统变成了传输工具。
+ Flume NG 旨在比起 Flume OG 变得明显更简单，更小，更容易部署。
    
+ 对日志收集的三个核心要素:
    1. Source：日志从哪里产生 (可以处理各种类型、各种格式的日志数据)
    2. Sink：日志会被推送到什么地方
    3. Channel：Source通过什么样的渠道，送到Sink (对采集到的数据进行简单的缓存，可以存放在memory、jdbc、file)
    (可以把Channel理解为一个queue，Source adds the events and Sink removes it.)
    
+ Flume架构以及应用介绍: <http://blog.csdn.net/a2011480169/article/details/51544664>    
+ Event
    - flume的核心是把数据从数据源(source)收集过来，在将收集到的数据送到指定的目的地(sink)。为了保证输送的过程一定成功，在送到目的地(sink)之前，会先缓存数据(channel),待数据真正到达目的地(sink)后，flume再删除自己缓存的数据, 这种机制保证了数据传输的可靠性与安全性 。
    - 在整个数据的传输的过程中，流动的是event，即事务保证是在event级别进行的。那么什么是event呢？—–event将传输的数据进行封装，是flume传输数据的基本单位，如果是文本文件，通常是一行记录，event也是事务的基本单位。
    - 一个完整的event包括：event headers、event body、event信息(即文本文件中的单行记录)
+ Agent:使用JVM 运行Flume。每台机器运行一个agent，但是可以在一个agent中包含多个sources和sinks。  
    - agent里面包含3个核心的组件：source—->channel—–>sink,类似生产者、仓库、消费者的架构。 
    
+ flume可以支持多级flume的agent，即flume可以前后相继，例如sink可以将数据写到下一个agent的source中
+ flume还支持扇入(fan-in)、扇出(fan-out)。所谓扇入就是source可以接受多个输入，所谓扇出就是sink可以将数据输出多个目的地destination中。
    
+ Spooling Directory Source：监听一个指定的目录，即只要应用程序向这个指定的目录中添加新的文件，source组件就可以获取到该信息，并解析该文件的内容，然后写入到channle。写入完成后，标记该文件已完成或者删除该文件。
+ Spooling Directory Source的两个注意事项
    1. ①If a file is written to after being placed into the spooling directory, Flume will print an error to its log file and stop processing.
    即：拷贝到spool目录下的文件不可以再打开编辑
    2. ②If a file name is reused at a later time, Flume will print an error to its log file and stop processing.
    即：不能将具有相同文件名字的文件拷贝到这个目录下
+ sink=logger  表示日志，：控制台  
   
<pre>    
① NetCat Source：监听一个指定的网络端口，即只要应用程序向这个端口里面写数据，这个source组件 
就可以获取到信息。 
②Spooling Directory Source：监听一个指定的目录，即只要应用程序向这个指定的目录中添加新的文 
件，source组件就可以获取到该信息，并解析该文件的内容，然后写入到channle。写入完成后，标记 
该文件已完成或者删除该文件。 
③Exec Source：监听一个指定的命令，获取一条命令的结果作为它的数据源 
常用的是tail -F file指令，即只要应用程序向日志(文件)里面写数据，source组件就可以获取到日志(文件)中最新的内容 。 
④Avro Source：监听一个指定的Avro 端口，通过Avro 端口可以获取到Avro client发送过来的文件 。即只要应用程序通过Avro 端口发送文件，source组件就可以获取到该文件中的内容。    
</pre>

