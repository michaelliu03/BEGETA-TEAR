lucene,solr,elasticsearch,ansj,sphix


---

### elk (mac)
<https://my.oschina.net/itblog/blog/547250>

#### logstash
+ `wget https://download.elastic.co/logstash/logstash/logstash-2.4.0.tar.gz`



    
#### elasticsearch
+ `bin/elasticsearch` (如果你想把 Elasticsearch 作为一个守护进程在后台运行，那么可以在后面添加参数 -d)
+ `http://localhost:9200/`启动Elasticsearch
+ 默认情况下，Elastic 只允许本机访问，如果需要远程访问，可以修改 Elastic 安装目录的config/elasticsearch.yml文件，
去掉network.host的注释，将它的值改成0.0.0.0，然后重新启动 Elastic。设成0.0.0.0让任何人都可以访问。线上服务不要这样设置，要设成具体的 IP。
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
+ DELETE 删除
+ HEAD 检查文档是否存在

当发送请求的时候， 为了扩展负载，更好的做法是轮询集群中所有的节点。

---

+ 问题:es深分页，记录上一页的id作为下一页的条

---

####  Elasticsearch 使用中文分词
+ `./bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v5.5.1/elasticsearch-analysis-ik-5.5.1.zip`
+ 这里使用_analyze api对中文段落进行分词，测试一下： 

<pre>
GET _analyze
{
  "analyzer":"ik_max_word",
  "text":"中华人民共和国国歌"
}
</pre>

如果使用ik_smart,则会尽可能少的返回词语

+ 安装elasticsearch-analysis-pinyin分词器 (<https://www.cnblogs.com/xing901022/p/5910139.html>)
  pinyin分词器可以让用户输入拼音，就能查找到相关的关键词。比如在某个商城搜索中，输入shuihu，就能匹配到水壶。这样的体验还是非常好的。

---

+ 使用elasticsearch-jdbc工具，编写脚本文件，抽取数据到es中
+ Elasticsearch Suggester- Google在用户刚开始输入的时候是自动补全的，而当输入到一定长度，如果因为单词拼写错误无法补全，就开始尝试提示相似的词。
那么类似的功能在Elasticsearch里如何实现呢？ 答案就在Suggesters API。 Suggesters基本的运作原理是将输入的文本分解为token，然后在索引的字典里查找相似的term并返回。 
+ Lucene的api中有实现查询文章相似度的接口，叫MoreLikeThis。Elasticsearch封装了该接口，通过Elasticsearch的More like this查询接口，我们可以非常方便的实现基于内容的推荐。
                           
                           



