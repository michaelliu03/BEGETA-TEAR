+ `bin/solr start -e cloud -noprompt` #  -e表示要启动一个现有的例子，例子名称是cloud，cloud这个例子是以SolrCloud方式启动的
+ <http://localhost:8983/solr/#/>

##### Indexing Data
+ `bin/post -c gettingstarted docs/` # 导入文档,查询显示默认的字段,id为文件的地址

<pre>
{
        "id":"/usr/local/soft/solr-6.2.1/docs/solr-map-reduce/org/apache/solr/hadoop/SolrReducer.html",
        "stream_size":[21434],
        "date":["2016-09-15T00:00:00Z"],
        "x_parsed_by":["org.apache.tika.parser.DefaultParser",
          "org.apache.tika.parser.html.HtmlParser"],
        "stream_content_type":["text/html"],
        "dc_title":["SolrReducer (Solr 6.2.1 API)"],
        "content_encoding":["UTF-8"],
        "content_type_hint":["text/html; charset=utf-8"],
        "resourcename":["/usr/local/soft/solr-6.2.1/docs/solr-map-reduce/org/apache/solr/hadoop/SolrReducer.html"],
        "title":["SolrReducer (Solr 6.2.1 API)"],
        "content_type":["text/html; charset=UTF-8"],
        "_version_":1588672041504997376
}
</pre>
+ `bin/post -c gettingstarted example/exampledocs/*.xml`
+ `bin/post -c gettingstarted example/exampledocs/books.json`
+ `bin/post -c gettingstarted example/exampledocs/books.csv`


<pre>
{
  "responseHeader":{
    "zkConnected":true,
    "status":0,
    "QTime":12,
    "params":{
      "q":"GB18030",
      "indent":"on",
      "wt":"json",
      "_":"1515075598264"}},
  "response":{"numFound":2,"start":0,"maxScore":11.345557,"docs":[
      {
        "id":"GB18030TEST",
        "name":["Test with some GB18030 encoded characters"],
        "features":["No accents here",
          "这是一个功能",
          "This is a feature (translated)",
          "这份文件是很有光泽",
          "This document is very shiny (translated)"],
        "price":[0.0],
        "inStock":[true],
        "_version_":1588672415395741696},
      {
        "id":"/usr/local/soft/solr-6.2.1/docs/quickstart.html",
        "stream_size":[32006],
        "x_parsed_by":["org.apache.tika.parser.DefaultParser",
          "org.apache.tika.parser.html.HtmlParser"],
        "stream_content_type":["text/html"],
        "dc_title":["Solr Quick Start"],
        "content_encoding":["UTF-8"],
        "content_type_hint":["text/html; charset=UTF-8"],
        "resourcename":["/usr/local/soft/solr-6.2.1/docs/quickstart.html"],
        "title":["Solr Quick Start"],
        "content_type":["text/html; charset=UTF-8"],
        "_version_":1588671979225874432}]
  }}
</pre>

+ 界面查询高亮失败??

+ <http://localhost:8983/solr/#/gettingstarted/query>
+ `curl "http://localhost:8983/solr/gettingstarted/select?indent=on&q=*:*&wt=json"`
+ `curl "http://localhost:8983/solr/gettingstarted/select?wt=json&indent=true&q=foundation"`

##### Cleanup
`bin/solr stop -all ; rm -Rf example/cloud/`

#### solr常用命令总结
+ <http://blog.csdn.net/matthewei6/article/details/50620600>

+ solr界面和查询参数注解:<http://blog.csdn.net/pizi995/article/details/54286097>

+ bin/solr start        启动单机版
+ bin/solr start -cloud        启动分布式版本
+ 如果是单机版要创建core，如果是分布式的要创建collection
+ 停止solr bin/solr stop -all


---

<https://cwiki.apache.org/confluence/display/solr/Apache+Solr+Reference+Guide>

---

+ solr 从零学习开始(1)--整体了解solr
<http://www.aboutyun.com/thread-7017-1-1.html>

1)    基于Lucene自己进行封装实现站内搜索。工作量及扩展性都较大，不采用。
2)    调用Google、Baidu的API实现站内搜索。同第三方搜索引擎绑定太死，无法满足后期业务扩展需要，暂时不采用。
3)    基于Compass+Lucene实现站内搜索。适合于对数据库驱动的应用数据进行索引，尤其是替代传统的like‘%expression%’来实现对varchar或clob等字段的索引，对于实现站内搜索是一种值得采纳的方案。但在分布式处理、接口封装上尚需要自己进行一定程度的封装。
4)    基于Solr实现站内搜索。封装及扩展性较好，提供了较为完备的解决方案，因此在门户社区中采用此方案。

Solr服务原理 
Solr对外提供标准的http接口来实现对数据的索引的增加、删除、修改、查询。在Solr中，用户通过向部署在servlet 容器中的Solr Web应用程序发送 HTTP 请求来启动索引和搜索。Solr接受请求，确定要使用的适当SolrRequestHandler，然后处理请求。通过 HTTP 以同样的方式返回响应。默认配置返回Solr的标准 XML 响应，也可以配置Solr的备用响应格式。
1.3.1     索引 
可以向Solr索引servlet传递四个不同的索引请求：
1)     add/update允许向Solr添加文档或更新文档。直到提交后才能搜索到这些添加和更新。
2)     commit 告诉Solr，应该使上次提交以来所做的所有更改都可以搜索到。
3)     optimize 重构 Lucene 的文件以改进搜索性能。索引完成后执行一下优化通常比较好。如果更新比较频繁，则应该在使用率较低的时候安排优化。一个索引无需优化也可以正常地运行。优化是一个耗时较多的过程。
4)     delete 可以通过 id 或查询来指定。按 id 删除将删除具有指定 id 的文档；按查询删除将删除查询返回的所有文档。


##### 架构分析
+ 一般搜索功能开发最少需要三台服务器
1. Web应用服务器：
表现层：接收搜索条件，并返回渲染的视图
业务层：使用solrj调用solr服务器的服务
如果数据库数据发生变更，要更新数据库并更新索引库
持久层：查询数据
2. 数据库服务 
3. Solr服务器

+ 使用Solr索引MySQL数据
<http://www.cnblogs.com/luxiaoxun/p/4442770.html>

+ 全文检索引擎 Solr 系列（2）— 全文检索基本原理
<https://mp.weixin.qq.com/s/sajet1EbDID3k0eawcTUig>

---

+ 分库分表后的关联查询，大段文本的模糊查询，这些要如何实现呢？显然传统的数据库没有很好的解决办法，这时可以借助专业的检索工具。
  全文检索工具 Solr 不仅简单易用性能好，而且支持海量数据高并发，只需实现系统两边数据的准实时或定时同步即可
  
+ 实时双写mysql和solr, 定时把mysql的数据更新到solr, 历史数据从mysql导入到solr生成索引(使用DataImportHandler导入并索引数据)

+ 配置IK中文分析器:<http://blog.csdn.net/sunqingzhong44/article/details/71367519>(包含zk操作)

+ Solr中的Schema类似于关系数据库中的表结构，它以schema.xml的文本形式存在在conf目录下，在添加文当到索引中时需要指定Schema，Schema文件主要包含三部分：字段（Field）、字段类型（FieldType）、唯一键（uniqueKey）


+ 搭建solrCloud 要先启动zk服务, 否则无法管理schema文件??




