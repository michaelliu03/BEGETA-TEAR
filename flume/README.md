<http://www.open-open.com/lib/view/open1435801555638.html>
<http://shiyanjun.cn/archives/915.html>
<http://blog.sina.com.cn/s/blog_7de9d5d80101hpdn.html>
<http://www.tuicool.com/articles/q2umQb>
<http://blog.itpub.net/23987042/viewspace-1114331/>
<http://www.aboutyun.com/thread-8917-1-1.html>

---

+ Flume-NG源码分析-整体结构及配置载入分析:<http://www.jianshu.com/p/0187459831af>

#### FLUME日志收集
+ <http://www.blogjava.net/paulwong/archive/2013/10/31/405860.html>
+ Flume采用了三层架构，分别为agent，collector和storage，每一层均可以水平扩展。其中，所有agent和collector由master统一管理，这使得系统容易监控和维护，且master允许有多个（使用ZooKeeper进行管理和负载均衡），这就避免了单点故障问题。
+ Flume采用了分层架构：分别为agent，collector和storage。其中，agent和collector均由两部分组成：source和sink，source是数据来源，sink是数据去向。
+ Flume使用两个组件：Master和Node，Node根据在Master shell或web中动态配置，决定其是作为Agent还是Collector。
+ agent的作用是将数据源的数据发送给collector. collector的作用是将多个agent的数据汇总后，加载到storage中。它的source和sink与agent类似。

