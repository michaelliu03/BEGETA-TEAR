不学Hadoop学Spark?
<http://dwz.cn/4coM1R>

---

### Hadoop
+ 单机安装，伪分布安装和分布安装
<http://www.aboutyun.com/thread-7567-1-1.html>

+ centos单机安装Hadoop2.6:<http://blog.csdn.net/woshisunxiangfu/article/details/44026207>

####Mapreduce
map，reduce，task,job，shuffe，partition，combiner
map由split来决定，reduce则是由partition来决定
<http://www.aboutyun.com/thread-6945-1-1.html>

MapReduce是分为Mapper任务和Reducer任务，Mapper任务的输出，通过网络传输到Reducer任务端，作为输入。

在Reducer任务中，通常做的事情是对数据进行归约处理。既然数据来源是Mapper任务的输出，那么是否可以在Mapper端对数据进行归约处理，业务逻辑与Reducer端做的完全相同。处理后的数据再传送到Reducer端，再做一次归约。这样的好处是减少了网络传输的数量。
在Mapper进行归约的类称为Combiner
<http://www.aboutyun.com/thread-7093-1-1.html>

hadoop中，combine、partition、shuffle作用分别是什么<http://www.aboutyun.com/thread-7104-1-1.html>

MapReduce在压力测试中的应用
<http://www.aboutyun.com/thread-6946-1-1.html>

####hadoop shell



识别hadoop是32位还是64位
hadoop-2.4.1/lib/native
使用file命令：

file libhadoop.so.1.0.0
 

libhadoop.so.1.0.0: ELF 32-bit LSB shared object, Intel 80386, version 1 (SYSV), dynamically linked, BuildID[sha1]=0xd3669af32f519c52b4e6200a69bec8ad7b26df85, not stripped

