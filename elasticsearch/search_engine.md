#### Lucene
+ <https://github.com/Kingson4Wu/SearchEngine>
+ <https://github.com/Kingson4Wu/lucene_demo>


#### elastic
+ <https://mp.weixin.qq.com/s/npXpXgiLZxTV93YgykInwg>
1. Node 与 Cluster
    - Elastic 本质上是一个分布式数据库，允许多台服务器协同工作，每台服务器可以运行多个 Elastic 实例。
    - 单个 Elastic 实例称为一个节点（node）。一组节点构成一个集群（cluster）。
2. Index
    - Elastic 会索引所有字段，经过处理后写入一个反向索引（Inverted Index）。查找数据的时候，直接查找该索引。
    - 所以，Elastic 数据管理的顶层单位就叫做 Index（索引）。它是单个数据库的同义词。每个 Index （即数据库）的名字必须是小写。
3. Document
    - index 里面单条的记录称为 Document（文档）。许多条 Document 构成了一个 Index。
    - 同一个 Index 里面的 Document，不要求有相同的结构（scheme），但是最好保持相同，这样有利于提高搜索效率。
4. Type
    - Document 可以分组，比如weather这个 Index 里面，可以按城市分组（北京和上海），也可以按气候分组（晴天和雨天）。这种分组就叫做 Type，它是虚拟的逻辑分组，用来过滤 Document。
    - 不同的 Type 应该有相似的结构（schema），举例来说，id字段不能在这个组是字符串，在另一个组是数值。这是与关系型数据库的表的一个区别。性质完全不同的数据（比如products和logs）应该存成两个 Index，而不是一个 Index 里面的两个 Type（虽然可以做到）。 
    - 根据规划，Elastic 6.x 版只允许每个 Index 包含一个 Type，7.x 版将会彻底移除 Type。
    
<pre>
$ curl -X PUT 'localhost:9200/accounts' -d '
 {
   "mappings": {
     "person": {
       "properties": {
         "user": {
           "type": "text",
           "analyzer": "ik_max_word",
           "search_analyzer": "ik_max_word"
         },
         "title": {
           "type": "text",
           "analyzer": "ik_max_word",
           "search_analyzer": "ik_max_word"
         },
         "desc": {
           "type": "text",
           "analyzer": "ik_max_word",
           "search_analyzer": "ik_max_word"
         }
       }
     }
   }
 }'    
</pre> 

1. 首先新建一个名称为accounts的 Index，里面有一个名称为person的 Type。person有三个字段。 
2. 这三个字段都是中文，而且类型都是文本（text），所以需要指定中文分词器，不能使用默认的英文分词器。
3. analyzer是字段文本的分词器，search_analyzer是搜索词的分词器。ik_max_word分词器是插件ik提供的，可以对文本进行最大数量的分词。
   
+ Elastic 的查询非常特别，使用自己的查询语法，要求 GET 请求带有数据体。(找到那篇文章?放哪里了??)

<pre>
   $ curl 'localhost:9200/accounts/person/_search'  -d '
    {
      "query" : { "match" : { "desc" : "软件" }}
    }' 
</pre>  

   
    