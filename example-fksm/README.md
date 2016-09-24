#### 安装zookeeper
+ `wget http://apache.fayea.com/zookeeper/zookeeper-3.4.9/zookeeper-3.4.9.tar.gz`
+ `tar -zxvf zookeeper-3.4.9.tar.gz`
+ `cd zookeeper-3.4.9`
+ `cp conf/zoo_sample.cfg conf/zoo.cfg`
+ `mkdir tmp`
+ `vi conf/zoo.cfg`
+ `dataDir=/usr/local/soft/zookeeper-3.4.9/tmp`
+ `sh bin/zkServer.sh start`


#### 安装storm
+ `wget http://apache.fayea.com/storm/apache-storm-1.0.2/apache-storm-1.0.2.tar.gz`
+ ` tar -zxvf apache-storm-1.0.2.tar.gz`
+ `cd apache-storm-1.0.2`
+ `export PATH=$PATH:/usr/local/soft/apache-storm-1.0.2/bin` 配置环境变量

##### start server
+ `storm nimbus`
+ `storm supervisor`
+ `storm ui`
+ `http://localhost:8080/index.html`






#### 安装kafka
+ `wget http://apache.fayea.com/kafka/0.10.0.1/kafka_2.11-0.10.0.1.tgz`
+ `tar -zxvf kafka_2.11-0.10.0.1.tgz`
+ `cd kafka_2.11-0.10.0.1`
##### start server
+ ` bin/zookeeper-server-start.sh config/zookeeper.properties` 
(可选) Kafka uses ZooKeeper so you need to first start a ZooKeeper server if you don't already have one. You can use the convenience script packaged with kafka to get a quick-and-dirty single-node ZooKeeper instance.
+ `bin/kafka-server-start.sh config/server.properties`
##### create topic
Let's create a topic named "test" with a single partition and only one replica:
`bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test`
We can now see that topic if we run the list topic command:
`bin/kafka-topics.sh --list --zookeeper localhost:2181`
test

Alternatively, instead of manually creating topics you can also configure your brokers to auto-create topics when a non-existent topic is published to.

##### Send some messages
Kafka comes with a command line client that will take input from a file or from standard input and send it out as messages to the Kafka cluster. By default each line will be sent as a separate message.
Run the producer and then type a few messages into the console to send to the server.

`bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test `
This is a message
This is another message

##### Start a consumer
Kafka also has a command line consumer that will dump out messages to standard output.
` bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic test --from-beginning`
This is a message
This is another message


#### 安装flume
+ `wget http://mirror.bit.edu.cn/apache/flume/1.6.0/apache-flume-1.6.0-bin.tar.gz`
+ `cp conf/flume-conf.properties.template .`
+ `mv flume-conf.properties.template flume-kafa-conf.properties`
+ `touch uplaodKafka.log`
+ `echo 'kxw'  >> uplaodKafka.log`
+ flume-kafa-conf.properties:

<pre>
a1.sources = r1
a1.channels = c1
a1.sinks = k1

a1.sources.r1.type = exec
a1.sources.r1.command = tail -F /usr/local/soft/apache-flume-1.6.0-bin/uplaodKafka.log
a1.sources.r1.channels = c1
a1.channels.c1.type=memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100

#设置Kafka接收器
a1.sinks.k1.type= org.apache.flume.sink.kafka.KafkaSink
#设置Kafka的broker地址和端口号
a1.sinks.k1.brokerList=127.0.0.1:9092
#设置Kafka的Topic
a1.sinks.k1.topic=test
#设置序列化方式
a1.sinks.k1.serializer.class=kafka.serializer.StringEncoder

a1.sinks.k1.channel=c1

</pre>

##### start server
+ `./bin/flume-ng agent -n a1 -c conf -f flume-kafa-conf.properties -Dflume.root.logger=INFO,console`


