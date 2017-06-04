## Elasticsearch: 权威指南
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/>

### 前言
+ Elasticsearch 是一个分布式、可扩展、实时的搜索与数据分析引擎。
+ Elasticsearch 不仅仅只是全文搜索，我们还将介绍结构化搜索、数据分析、复杂的语言处理、地理位置和对象间关联关系等。
我们还将探讨如何给数据建模来充分利用 Elasticsearch 的水平伸缩性，以及在生产环境中如何配置和监视你的集群。
+ 这本权威指南不仅帮助你学习 Elasticsearch，而且带你接触更深入、更有趣的话题，如 集群内的原理 、 分布式文档存储 、 执行分布式检索 和 分片内部原理 ，
这些虽然不是必要的阅读却能让你深入理解其内在机制。

#### 本书导航
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/_navigating_this_book.html>

#### Elasticsearch 参考手册及文档
+ <https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html>
+ <https://www.elastic.co/guide/>
+ <https://github.com/elastic/elasticsearch>

#### 社区
+ 英文社区: <https://discuss.elastic.co/c/elasticsearch/>
+ 中文社区: <https://elasticsearch.cn/>

---

### 基础入门

+ Elasticsearch 也是使用 Java 编写的，它的内部使用 Lucene 做索引与搜索，但是它的目标是使全文检索变得简单，
通过隐藏 Lucene 的复杂性，取而代之的提供一套简单一致的 RESTful API。

+ 然而，Elasticsearch 不仅仅是 Lucene，并且也不仅仅只是一个全文搜索引擎。 它可以被下面这样准确的形容：
  - 一个分布式的实时文档存储，每个字段 可以被索引与搜索
  - 一个分布式实时分析搜索引擎
  - 能胜任上百个服务节点的扩展，并支持 PB 级别的结构化或者非结构化数据
Elasticsearch 将所有的功能打包成一个单独的服务，这样你可以通过程序去访问它提供的简单的 RESTful API 服务，
不论你是使用自己喜欢的编程语言还是直接使用命令行（去充当这个客户端）。

#### 安装并运行
+ `cd elasticsearch-<version>`
  `./bin/elasticsearch`
+ 如果你想把 Elasticsearch 作为一个守护进程在后台运行，那么可以在后面添加参数 -d
+ Sense 是一个 Kibana 应用 它提供交互式的控制台，通过你的浏览器直接向 Elasticsearch 提交请求
`./bin/kibana plugin --install elastic/sense`

#### 和 Elasticsearch 交互
1. Java API (9300 端口)
    - 节点客户端（Node client）
    - 传输客户端（Transport client）
2. RESTful API with JSON over HTTP (端口 9200)
    - 结合 Sense 控制台学习

#### 面向文档
+ Elasticsearch 是 面向文档 的，意味着它存储整个对象或 文档_。Elasticsearch 不仅存储文档，而且 _索引 每个文档的内容使之可以被检索。
在 Elasticsearch 中，你 对文档进行索引、检索、排序和过滤--而不是对行列数据。这是一种完全不同的思考数据的方式，也是 Elasticsearch 能支持复杂全文检索的原因。
+ Elasticsearch 使用 JavaScript Object Notation 或者 JSON 作为文档的序列化格式。
+ 官方 Elasticsearch 客户端 自动为您提供 JSON 转化。

#### 基础概念
+ 索引、搜索及聚合等基础概念, suggestions、geolocation、percolation、fuzzy 与 partial matching 等特性
+ 一个文档代表一个雇员。存储数据到 Elasticsearch 的行为叫做 索引 ，但在索引一个文档之前，需要确定将文档存储在哪里。
  一个 Elasticsearch 集群可以 包含多个 索引 ，相应的每个索引可以包含多个 类型 。 这些不同的类型存储着多个 文档 ，每个文档又有 多个 属性 。
  索引（名词）vs 索引（动词）vs 倒排索引

#### 搜索
+ （检索文档）将 HTTP 命令由 PUT 改为 GET 可以用来检索文档，同样的，可以使用 DELETE 命令来删除文档，以及使用 HEAD 指令来检查文档是否存在。如果想更新已存在的文档，只需再次 PUT 。
+ （轻量搜索）
    - `GET /megacorp/employee/_search` 返回结果放在数组 hits 中。一个搜索默认返回十条结果.
    - `GET /megacorp/employee/_search?q=last_name:Smith` 涉及到一个 查询字符串 （_query-string_） 搜索
+ （使用查询表达式搜索）Query-string 搜索通过命令非常方便地进行临时性的即席搜索 ，但它有自身的局限性  <br/>
Elasticsearch 提供一个丰富灵活的查询语言叫做 查询表达式 ， 它支持构建更加复杂和健壮的查询。
在GET请求上带上body参数
+ (全文搜索) Elasticsearch 默认按照相关性得分排序，即每个文档跟查询的匹配程度。
+ (短语搜索) 精确匹配一系列单词或者短语.对 match 查询稍作调整，使用一个叫做 match_phrase 的查询
+ (高亮搜索)增加一个新的 highlight 参数.返回结果多了一个叫做 highlight 的部分。这个部分包含了 about 属性匹配的文本片段，并以 HTML 标签 <em></em> 封装.
<https://www.elastic.co/guide/en/elasticsearch/reference/master/search-request-highlighting.html>
+ (分析) Elasticsearch 有一个功能叫聚合（aggregations），允许我们基于数据生成一些精细的分析结果。聚合与 SQL 中的 GROUP BY 类似但更强大。
这些聚合并非预先统计，而是从匹配当前查询的文档中即时生成。
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/_analytics.html>

#### 分布式特性
Elasticsearch 可以横向扩展至数百（甚至数千）的服务器节点，同时可以处理PB级数据。Elasticsearch 天生就是分布式的，并且在设计时屏蔽了分布式的复杂性。
Elasticsearch 在分布式方面几乎是透明的。教程中并不要求了解分布式系统、分片、集群发现或其他的各种分布式概念。
#####  Elasticsearch 尽可能地屏蔽了分布式系统的复杂性。这里列举了一些在后台自动执行的操作：
+ 分配文档到不同的容器 或 分片 中，文档可以储存在一个或多个节点中
+ 按集群节点来均衡分配这些分片，从而对索引和搜索过程进行负载均衡
+ 复制每个分片以支持数据冗余，从而防止硬件故障导致的数据丢失
+ 将集群中任一节点的请求路由到存有相关数据的节点
+ 集群扩容时无缝整合新节点，重新分配分片以便从离群节点恢复

有关 Elasticsearch 分布式特性的补充章节。这些章节将介绍有关集群扩容、故障转移(集群内的原理) 、
应对文档存储(分布式文档存储) 、执行分布式搜索(执行分布式检索) ，以及分区（shard）及其工作原理(分片内部原理) 。


