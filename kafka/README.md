Kafka使用入门教程
<http://www.linuxidc.com/Linux/2014-07/104470.htm>


搭建Kafka运行环境
http://www.linuxidc.com/Linux/2014-07/104470p2.htm


ZooKeeper系列3：ZooKeeper命令、命令行工具及简单操作
http://www.cnblogs.com/likehua/p/3999588.html

解决Kafka“Failed to send messages after 3 tries”错误
http://ju.outofmemory.cn/entry/195419
un work..


Kafka Consumer的底层API- SimpleConsumer
<http://www.tuicool.com/articles/j6ZZnaI>

教程：
<http://blog.csdn.net/lizhitao/article/category/2194509>

---
RabbitMQ是一个AMQP实现，传统的messaging queue系统实现，基于Erlang。老牌MQ产品了。AMQP协议更多用在企业系统内，对数据一致性、稳定性和可靠性要求很高的场景，对性能和吞吐量还在其次。

Kafka是linkedin开源的MQ系统，主要特点是基于Pull的模式来处理消息消费，追求高吞吐量，一开始的目的就是用于日志收集和传输，0.8开始支持复制，不支持事务，适合产生大量数据的互联网服务的数据收集业务。

ZeroMQ只是一个网络编程的Pattern库，将常见的网络请求形式（分组管理，链接管理，发布订阅等）模式化、组件化，简而言之socket之上、MQ之下。对于MQ来说，网络传输只是它的一部分，更多需要处理的是消息存储、路由、Broker服务发现和查找、事务、消费模式（ack、重投等）、集群服务等。

作者：xiaodan zhuang
链接：https://www.zhihu.com/question/22480085/answer/23106407
来源：知乎
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

---
RabbitMQ和kafka从几个角度简单的对比:<http://www.cnblogs.com/davidwang456/p/4076097.html>

---

+ 大规模Kafka集群的管理利器: LinkedIn最新开源的Cruise :<https://mp.weixin.qq.com/s/9ou_hm8SrNceEggmeKNQPw>
+ Apache Kafka 1.0：为什么我们等了这么久？:<https://mp.weixin.qq.com/s/Xs-NYcNyt8aM4ZGjDNJeCQ>
+ 跟我学Kafka之NIO通信机制:<https://mp.weixin.qq.com/s?__biz=MjM5MDAxNjkyMA==&mid=2650719402&idx=2&sn=edb79569aae77e1ee8a7db4228decd50&scene=0#wechat_redirect>
+ kafka数据可靠性深度解读:<http://blog.csdn.net/u013256816/article/details/71091774>
+ 浅谈分布式消息技术 Kafka:<https://mp.weixin.qq.com/s/eoEwVsy8PX_jAD_mxrn_1A>
+ Kafka 基本原理:<https://mp.weixin.qq.com/s/ItqhIhC0RMMSIV-L54xeoA>
+ Spark Streaming场景应用-Kafka 数据读取方式:<https://mp.weixin.qq.com/s/MmINr7YLVlLvWch2AMz3xQ>