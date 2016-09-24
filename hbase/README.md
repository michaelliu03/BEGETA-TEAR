#### install
+ `wget http://apache.fayea.com/hbase/stable/hbase-1.2.3-bin.tar.gz`
+ `tar -zxvf hbase-1.2.3-bin.tar.gz`

+ `vi hbase-site.xml`

```xml
<configuration>
 <property>
    <name>hbase.rootdir</name>
    <value>file:///usr/local/soft/hbase</value>
  </property>
  <property>
    <name>hbase.zookeeper.property.dataDir</name>
    <value>/usr/local/soft/hbase-1.2.3/zookeeper</value>
  </property>

</configuration>
```

使用自带的zk

+ `vi hbase-evn.sh`
export HBASE_MANAGES_ZK=false (使用自带的,这步忽略)
使用独立的ZooKeeper时需要修改HBASE_MANAGES_ZK值为false，为不使用默认ZooKeeper实例。

##### start server
+ `bin/start-hbase.sh`

Connect to HBase
+ `./bin/hbase shell`
<https://hbase.apache.org/book.html#quickstart>


<pre>
➜  hbase-1.2.3 git:(master) ✗ ./bin/hbase shell
2016-09-24 23:50:31,098 WARN  [main] util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
HBase Shell; enter 'help<RETURN>' for list of supported commands.
Type "exit<RETURN>" to leave the HBase Shell
Version 1.2.3, rbd63744624a26dc3350137b564fe746df7a721a4, Mon Aug 29 15:13:42 PDT 2016

hbase(main):001:0> create 'test', 'cf'
0 row(s) in 1.5670 seconds

=> Hbase::Table - test
hbase(main):002:0>
</pre>

关闭shell
+ hbase(main):014:0> exit

停止 HBase
`./bin/stop-hbase.sh`

#### cluster install
+ 集群安装需要先安装Hadoop
<http://blog.csdn.net/hguisu/article/details/7244413>