lucene,solr,elasticsearch,ansj,sphix


---

### elk (mac)
<https://my.oschina.net/itblog/blog/547250>

#### logstash
+ `wget https://download.elastic.co/logstash/logstash/logstash-2.4.0.tar.gz`



    
#### elasticsearch
+ `bin/elasticsearch` (如果你想把 Elasticsearch 作为一个守护进程在后台运行，那么可以在后面添加参数 -d)
+ `http://localhost:9200/`启动Elasticsearch
+ 安装Marvel(Elasticsearch的可视化管理和监控工具) <https://www.elastic.co/downloads/marvel>
+ `curl http://127.0.0.1:9200/_nodes/_local/plugins`  查看节点上的插件列表，检查列表中是否含有 marvel
+ `/bin/plugin install mobz/elasticsearch-head`
+ `http://localhost:9200/_plugin/head/`

#### kibana


---
ES完全能满足10亿数据量，5k吞吐量的常见搜索业务需求

<https://github.com/elastic/elasticsearch/issues/22207>
Lastly, please note that Elastic does not officially support Elasticsearch on Windows 7.




---

#### 版本历史
<http://www.tuicool.com/articles/qYvUfuz>

+ (2016年7月26日) ELK，是 Elasticsearch 、 Logstash 、 Kibana 三个产品的首字母缩写，现在 Elastic 又新增了一个新的开源项目成员： Beats。
同时由于现在的版本比较混乱，每个产品的版本号都不一样， Elasticsearch和Logstash目前是2.3.4；Kibana是4.5.3；Beats是1.2.3；
版本号太乱了有没有，什么版本的 ES 用什么版本的 Kibana ？有没有兼容性问题？
所以我们打算将这些的产品版本号也统一一下，即 v5.0 ，为什么是 5.0 ，因为 Kibana 都 4.x 了，下个版本就只能是 5.0 了，
其他产品就跟着跳跃一把，第一个 5.0 正式版将在今年的秋季发布，目前最新的测试版本是： 5.0 Alpha 4

+ 这个链接介绍了很多新特性,后面要仔细理解!!!!

---

实操:启用多个节点,关闭其中一个,看是否故障转移

1. FlakeID 模式生成 自动生成id
2. 节点数量与索引主分片和副本分片的关系?
3. 中文分析器




---

#### 谓词
+ PUT 谓词(“使用这个 URL 存储这个文档”)， 
+ POST 谓词(“存储文档在这个 URL 命名空间下”)
+ GET 查询
+ DEDLETE 删除
+ HEAD 检查文档是否存在

当发送请求的时候， 为了扩展负载，更好的做法是轮询集群中所有的节点。

---

+ 问题:es深分页，记录上一页的id作为下一页的条




