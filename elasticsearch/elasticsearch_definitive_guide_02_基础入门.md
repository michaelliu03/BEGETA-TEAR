## 基础入门

### 集群内的原理
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/distributed-cluster.html>
快速阅览该章节，将来有需要时再次查看。

+ 虽然 Elasticsearch 可以获益于更强大的硬件设备，但是垂直扩容是有极限的。 
真正的扩容能力是来自于水平扩容--为集群添加更多的节点，并且将负载压力和稳定性分散到这些节点中。

#### 空集群
+ 一个运行中的 Elasticsearch 实例称为一个 节点，而集群是由一个或者多个拥有相同 cluster.name 配置的节点组成， 
它们共同承担数据和负载的压力。当有节点加入集群中或者从集群中移除节点时，集群将会重新平均分布所有的数据。
我们可以将请求发送到 集群中的任何节点，包括主节点。 每个节点都知道任意文档所处的位置，并且能够将我们的请求直接转发到存储我们所需文档的节点。

#### 集群健康
+ Elasticsearch 的集群监控信息中包含了许多的统计数据，其中最为重要的一项就是 集群健康 ， 它在 status 字段中展示为 green 、 yellow 或者 red 。
+ `GET /_cluster/health`
+ status 字段指示着当前集群在总体上是否工作正常。它的三种颜色含义如下：
  - green
  所有的主分片和副本分片都正常运行。
  - yellow
  所有的主分片都正常运行，但不是所有的副本分片都正常运行。
  - red
  有主分片没能正常运行。
  
#### 添加索引
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/_add-an-index.html>  
  
+ 我们往 Elasticsearch 添加数据时需要用到索引 —— 保存相关数据的地方。 索引实际上是指向一个或者多个物理 分片 的 逻辑命名空间 。
+ 一个分片是一个底层的 工作单元，它仅保存了全部数据中的一部分。一个分片是一个Lucene 的实例，本身就是一个完整的搜索引擎。
我们的文档被存储和索引到分片内，但是应用程序是直接与索引而不是与分片进行交互。
+ Elasticsearch 是利用分片将数据分发到集群内各处的。分片是数据的容器，文档保存在分片内，分片又被分配到集群内的各个节点里。 
当你的集群规模扩大或者缩小时， Elasticsearch 会自动的在各节点中迁移分片，使得数据仍然均匀分布在集群里。

+ 一个分片可以是 主分片或者 副本分片。 索引内任意一个文档都归属于一个主分片，所以主分片的数目决定着索引能够保存的最大数据量。
(索引包含多个主分片,主分片包含多个文档), 技术上来说，一般一个主分片最大能够存储 Integer.MAX_VALUE - 128 个文档
+ 一个副本分片只是一个主分片的拷贝。 副本分片作为硬件故障时保护数据不丢失的冗余备份，并为搜索和返回文档等读操作提供服务。
在索引建立的时候就已经确定了主分片数，但是副本分片数可以随时修改。
+ 索引在默认情况下会被分配5个主分片,但是可以在创建时修改
<pre>
PUT /blogs
{
   "settings" : {
      "number_of_shards" : 3,
      "number_of_replicas" : 1
   }
}
</pre>
3个主分片和一份副本（每个主分片拥有一个副本分片）

+ 拥有一个索引的单节点集群,集群的健康状况为 yellow 则表示全部 主 分片都正常运行（集群可以正常服务所有请求），
但是 副本分片没有全部处在正常状态。 实际上，所有3个副本分片都是 unassigned —— 它们都没有被分配到任何节点。 
在同一个节点上既保存原始数据又保存副本是没有意义的，因为一旦失去了那个节点，我们也将丢失该节点上的所有副本数据。

#### 添加故障转移
+ 当集群中只有一个节点在运行时，意味着会有一个单点故障问题——没有冗余。
+ 启动第二个节点为了测试第二个节点启动后的情况，你可以在同一个目录内，完全依照启动第一个节点的方式来启动一个新节点。多个节点可以共享同一个目录。
当你在同一台机器上启动了第二个节点时，只要它和第一个节点有同样的 cluster.name 配置，它就会自动发现集群并加入到其中。 
但是在不同机器上启动节点的时候，为了加入到同一集群，你需要配置一个可连接到的单播主机列表。
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/important-configuration-changes.html#unicast>

+ 拥有两个节点的集群——所有主分片和副本分片都已被分配.cluster-health 现在展示的状态为 green ，这表示所有6个分片（包括3个主分片和3个副本分片）都在正常运行。

#### 水平扩容
+ 拥有三个节点的集群——为了分散负载而对分片进行重新分配
+ 分片是一个功能完整的搜索引擎，它拥有使用一个节点上的所有资源的能力。 我们这个拥有6个分片（3个主分片和3个副本分片）的索引可以最大扩容到6个节点，
每个节点上存在一个分片，并且每个分片拥有所在节点的全部资源。
+ 更多的扩容-主分片的数目在索引创建时 就已经确定了下来。实际上，这个数目定义了这个索引能够 存储 的最大数据量。（实际大小取决于你的数据、硬件和使用场景。）
但是，读操作——搜索和返回数据——可以同时被主分片 或 副本分片所处理，所以当你拥有越多的副本分片时，也将拥有越高的吞吐量。
在运行中的集群上是可以动态调整副本分片数目的 ，我们可以按需伸缩集群。
<pre>
PUT /blogs/_settings
{
   "number_of_replicas" : 2
}
</pre>
把副本数从默认的 1 增加到 2
+ 如果只是在相同节点数目的集群上增加更多的副本分片并不能提高性能，因为每个分片从节点上获得的资源会变少。 你需要增加更多的硬件资源来提升吞吐量。
但是更多的副本分片数提高了数据冗余量

#### 应对故障
+ 关闭的节点是一个主节点.而集群必须拥有一个主节点来保证正常工作，所以发生的第一件事情就是选举一个新的主节点
+ 检查集群的状况，我们看到的状态将会为 red ：不是所有主分片都在正常工作.接着副本分片提升为主分片， 此时集群的状态将会为 yellow 。
(不是 green,拥有所有的三个主分片，但是同时设置了每个主分片需要对应2份副本分片，而此时只存在一份副本分片。)
+ 重新启动 Node 1 ，集群可以将缺失的副本分片再次进行分配.如果 Node 1 依然拥有着之前的分片，它将尝试去重用它们，同时仅从主分片复制发生了修改的数据文件。


---

### 数据输入和输出

#### 文档
+ 一个 对象 是基于特定语言的内存的数据结构。 为了通过网络发送或者存储它，我们需要将它表示成某种标准的格式。 JSON 是一种以人可读的文本表示对象的方法。 
+ Elastcisearch 是分布式的 文档 存储。它能存储和检索复杂的数据结构--序列化成为JSON文档--以 实时 的方式。
+ 在 Elasticsearch 中， 每个字段的所有数据 都是 默认被索引的。 即每个字段都有为了快速检索设置的专用倒排索引。

#### 文档元数据
+ 一个文档不仅仅包含它的数据 ，也包含 元数据 —— 有关 文档的信息。 三个必须的元数据元素如下：
    - _index
    文档在哪存放
    - _type
    文档表示的对象类别
    - _id
    文档唯一标识
+ 索引名，这个名字必须小写，不能以下划线开头，不能包含逗号
+  _type 命名可以是大写或者小写，但是不能以下划线或者句号开头，不应该包含逗号， 并且长度限制为256个字符. 
+ ID 是一个字符串， 当它和 _index 以及 _type 组合就可以唯一确定 Elasticsearch 中的一个文档。 当你创建一个新的文档，要么提供自己的 _id ，要么让 Elasticsearch 帮你生成。

#### 索引文档
+ 在 Elasticsearch 中每个文档都有一个版本号。当每次对文档进行修改时（包括删除）， _version 的值会递增。
+ 如果你的数据没有自然的 ID， Elasticsearch 可以帮我们自动生成 ID 。  <br/>
请求的结构调整为： 不再使用 PUT 谓词(“使用这个 URL 存储这个文档”)， 而是使用 POST 谓词(“存储文档在这个 URL 命名空间下”)。 <br/>
自动生成的 ID 是 URL-safe、 基于 Base64 编码且长度为20个字符的 GUID 字符串。 这些 GUID 字符串由可修改的 FlakeID 模式生成，这种模式允许多个节点并行生成唯一 ID ，且互相之间的冲突概率几乎为零。

#### 取回一个文档
+ GET 请求的响应体包括 {"found": true}.请求一个不存在的文档，仍旧会得到一个 JSON 响应体，但是 found 将会是 false 。 
此外， HTTP 响应码将会是 404 Not Found ，而不是 200 OK 。
+ 返回文档的一部分: 单个字段能用 _source 参数请求得到，多个字段也能使用逗号分隔的列表来指定。
`GET /website/blog/123?_source=title,text`
+ 只想得到 _source 字段，不需要任何元数据，你能使用 _source 端点
`GET /website/blog/123/_source`

#### 检查文档是否存在 
HEAD 请求没有返回体，只返回一个 HTTP 请求报头.如果文档存在， Elasticsearch 将返回一个 200 ok 的状态码.
若文档不存在， Elasticsearch 将返回一个 404 Not Found 的状态码
`curl -i -XHEAD http://localhost:9200/website/blog/123`

#### 更新整个文档
在 Elasticsearch 中文档是 不可改变 的，不能修改它们。 相反，如果想要更新现有的文档，需要 重建索引 或者进行替换
在响应体中，Elasticsearch 增加了 _version 字段值,created 标志设置成 false ，是因为相同的索引、类型和 ID 的文档已经存在。

#### 创建新文档
当我们索引一个文档， 怎么确认我们正在创建一个完全新的文档，而不是覆盖现有的呢？

+ 确保创建一个新文档的最简单办法是，使用索引请求的 POST 形式让 Elasticsearch 自动生成唯一 _id
+ 如果已经有自己的 _id ，那么我们必须告诉 Elasticsearch ，只有在相同的 _index 、 _type 和 _id 不存在时才接受我们的索引请求。
    - 第一种方法使用 op_type 查询 -字符串参数 `PUT /website/blog/123?op_type=create`
    - 第二种方法是在 URL 末端使用 /_create `PUT /website/blog/123/_create`
+ 成功201 Created,失败409 Conflict

#### 删除文档
+ 返回一个 200 ok 的 HTTP 响应码,字段 _version 值增加 ,如果文档没有 找到，我们将得到 404 Not Found, _version 值仍然会增加
(这是 Elasticsearch 内部记录本的一部分，用来确保这些改变在跨多节点时以正确的顺序执行。)
+ 正如已经在更新整个文档中提到的，删除文档不会立即将文档从磁盘中删除，只是将文档标记为已删除状态。
随着你不断的索引更多的数据，Elasticsearch 将会在后台清理标记为已删除的文档。

#### 处理冲突
变更越频繁，读数据和更新数据的间隙越长，也就越可能丢失变更。

+ 在数据库领域中，有两种方法通常被用来确保并发更新时变更不会丢失
    - 悲观并发控制
    这种方法被关系型数据库广泛使用，它假定有变更冲突可能发生，因此阻塞访问资源以防止冲突。 一个典型的例子是读取一行数据之前先将其锁住，确保只有放置锁的线程能够对这行数据进行修改。
    - 乐观并发控制
    Elasticsearch 中使用的这种方法假定冲突是不可能发生的，并且不会阻塞正在尝试的操作。 然而，如果源数据在读写当中被修改，更新将会失败。应用程序接下来将决定该如何解决冲突。 
    例如，可以重试更新、使用新的数据、或者将相关情况报告给用户。

#### 乐观并发控制
+ Elasticsearch 是分布式的。当文档创建、更新或删除时， 新版本的文档必须复制到集群中的其他节点。
Elasticsearch 也是异步和并发的，这意味着这些复制请求被并行发送，并且到达目的地时也许 顺序是乱的 。
Elasticsearch 需要一种方法确保文档的旧版本不会覆盖新的版本。
+ Elasticsearch 使用 _version 号来确保变更以正确顺序得到执行。如果旧版本的文档在新版本之后到达，它可以被简单的忽略。
+ 利用 _version 号来确保 应用中相互冲突的变更不会导致数据丢失。我们通过指定想要修改文档的 version 号来达到这个目的。 如果该版本不是当前版本号，我们的请求将会失败。
    - `PUT /website/blog/1?version=1` 通过重建文档的索引来保存修改，我们指定 version 为我们的修改会被应用的版本, 请求成功，并且响应体告诉我们 _version 已经递增到 2
    失败,Elasticsearch 返回 409 Conflict HTTP 响应码
    - 所有文档的更新或删除 API，都可以接受 version 参数，这允许你在代码中使用乐观的并发控制，这是一种明智的做法。    
    
##### 通过外部系统使用版本控制
+ 一个常见的设置是使用其它数据库作为主要的数据存储，使用 Elasticsearch 做数据检索， 
这意味着主数据库的所有更改发生时都需要被复制到 Elasticsearch ，如果多个进程负责这一数据同步，你可能遇到类似于之前描述的并发问题。
+ Elasticsearch 中通过增加 version_type=external 到查询字符串的方式重用这些相同的版本号， 版本号必须是大于零的整数
+ 外部版本号的处理方式和我们之前讨论的内部版本号的处理方式有些不同， Elasticsearch 不是检查当前 _version 和请求中指定的版本号是否相同， 
而是检查当前 _version 是否 小于 指定的版本号。 如果请求成功，外部的版本号作为文档的新 _version 进行存储。
`PUT /website/blog/2?version=5&version_type=external`

???!! 注意新版本的es是不是也是这种规则

#### 文档的部分更新
使用 update API 我们还可以部分更新文档

+  update API 简单使用与之前描述相同的 检索-修改-重建索引 的处理过程。
区别在于这个过程发生在分片内部，这样就避免了多次请求的网络开销。通过减少检索和重建索引步骤之间的时间，
我们也减少了其他进程的变更带来冲突的可能性。
+ update 请求最简单的一种形式是接收文档的一部分作为 doc 的参数， 它只是与现有的文档进行合并。对象被合并到一起，
覆盖现有的字段，增加新的字段。 `POST /website/blog/1/_update`

##### 使用脚本部分更新文档
+ 脚本可以在 update API中用来改变 _source 的字段内容， 它在更新脚本中称为 ctx._source 。 
例如，我们可以使用脚本来增加博客文章中 views 的数量.
<pre>
POST /website/blog/1/_update
{
   "script" : "ctx._source.views+=1"
}
</pre>

+ 用 Groovy 脚本编程 (默认禁用)
    - 可以使用存储在每个节点的 config/scripts/ 目录下的 Groovy 脚本
    - <https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-scripting.html>
    
+ 其他
    - 
    <pre>
    POST /website/blog/1/_update
    {
       "script" : "ctx._source.tags+=new_tag",
       "params" : {
          "new_tag" : "search"
       }
    }
    </pre>
    - 
    <pre>
    POST /website/blog/1/_update
    {
       "script" : "ctx.op = ctx._source.views == count ? 'delete' : 'none'",
        "params" : {
            "count": 1
        }
    }
    </pre>

##### 更新的文档可能尚不存在
使用 upsert 参数，指定如果文档不存在就应该先创建它

<pre>
POST /website/pageviews/1/_update
{
   "script" : "ctx._source.views+=1",
   "upsert": {
       "views": 1
   }
}    
</pre>

##### 更新和冲突
如果冲突发生了，我们唯一需要做的就是尝试再次更新。

+ 可以通过 设置参数 retry_on_conflict 来自动完成， 这个参数规定了失败之前 update 应该重试的次数，它的默认值为 0 。
`POST /website/pageviews/1/_update?retry_on_conflict=5`

#### 取回多个文档
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/_Retrieving_Multiple_Documents.html>

Elasticsearch 的速度已经很快了，但甚至能更快。 将多个请求合并成一个，避免单独处理每个请求花费的网络时延和开销。 
如果你需要从 Elasticsearch 检索很多文档，那么使用 multi-get 或者 mget API 来将这些检索请求放在一个请求中，
将比逐个文档请求更快地检索到全部文档。

+ mget API 要求有一个 docs 数组作为参数，每个 元素包含需要检索文档的元数据， 包括 _index 、 _type 和 _id 。
如果你想检索一个或者多个特定的字段，那么你可以通过 _source 参数来指定这些字段的名字
+ 即使有某个文档没有找到，上述请求的 HTTP 状态码仍然是 200 。事实上，即使请求 没有 找到任何文档，
它的状态码依然是 200 --因为 mget 请求本身已经成功执行。 为了确定某个文档查找是成功或者失败，你需要检查 found 标记。

#### 代价较小的批量操作
与 mget 可以使我们一次取回多个文档同样的方式， bulk API 允许在单个步骤中进行多次 create 、 index 、 update 或 delete 请求。 
如果你需要索引一个数据流比如日志事件，它可以排队和索引数百或数千批次。

<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/bulk.html>

额,这个东西常用吗???! 新版本有没有优化???
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/distrib-multi-doc.html#bulk-format>

---

### 分布式文档存储

#### 路由一个文档到一个分片中
+ Elasticsearch 如何知道一个文档应该存放到哪个分片中呢？
`shard = hash(routing) % number_of_primary_shards`
routing 是一个可变值，默认是文档的 _id
<br/>
这就解释了为什么我们要在创建索引的时候就确定好主分片的数量 并且永远不会改变这个数量：因为如果数量变化了，
那么所有之前路由的值都会无效，文档也再也找不到了。

+ 你可能觉得由于 Elasticsearch 主分片数量是固定的会使索引难以进行扩容。
实际上当你需要时有很多技巧可以轻松实现扩容。我们将会在扩容设计一章中提到更多有关水平扩展的内容。

+ 所有的文档 API（ get 、 index 、 delete 、 bulk 、 update 以及 mget ）都接受一个叫做 routing 的路由参数 ，
通过这个参数我们可以自定义文档到分片的映射。

#### 主分片和副本分片如何交互
我们可以发送请求到集群中的任一节点。 每个节点都有能力处理任意请求。 每个节点都知道集群中任一文档位置，所以可以直接将请求转发到需要的节点上。

+ 当发送请求的时候， 为了扩展负载，更好的做法是轮询集群中所有的节点。

??? 节点数量与索引主分片和副本分片的关系?? 看不明白
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/distrib-write.html>

##### 取回一个文档
为了读取请求，协调节点在每次请求的时候将选择不同的副本分片来达到负载均衡；通过轮询所有的副本分片。

##### 局部更新文档
+ 基于文档的复制
    - 当主分片把更改转发到副本分片时， 它不会转发更新请求。 相反，它转发完整文档的新版本。
    请记住，这些更改将会异步转发到副本分片，并且不能保证它们以发送它们相同的顺序到达。 
    如果Elasticsearch仅转发更改请求，则可能以错误的顺序应用更改，导致得到损坏的文档。
    (最后一句话不能完全理解??)
    
##### 多文档模式
mget 和 bulk API 的 模式类似于单文档模式。区别在于协调节点知道每个文档存在于哪个分片中。 
它将整个多文档请求分解成 每个分片 的多文档请求，并且将这些请求并行转发到每个参与节点。
协调节点一旦收到来自每个节点的应答，就将每个节点的响应收集整理成单个响应，返回给客户端

---

### 搜索——最基本的工具

+ 搜索（search） 可以做到：
    - 在类似于 gender 或者 age 这样的字段 上使用结构化查询，join_date 这样的字段上使用排序，就像SQL的结构化查询一样。
    - 全文检索，找出所有匹配关键字的文档并按照相关性（relevance） 排序后返回结果。

+ 很多搜索都是开箱即用的，为了充分挖掘 Elasticsearch 的潜力，你需要理解以下三个概念：
  - 映射（Mapping）
  描述数据在每个字段内如何存储
  - 分析（Analysis）
  全文是如何处理使之可以被搜索的
  - 领域特定查询语言（Query DSL）
  Elasticsearch 中强大灵活的查询语言
以上提到的每个点都是一个大话题，我们将在 深入搜索 一章详细阐述它们 
   
#### 空搜索
+ `GET /_search` 返回集群中所有索引下的所有文档
   
+ hits(hits 数组中只有 10 个文档),took(执行整个搜索请求耗费了多少毫秒),shards(在查询中参与分片的总数，以及这些分片成功了多少个失败了多少个)
+ timeout(告诉我们查询是否超时), 指定 timeout: `GET /_search?timeout=10ms`
(timeout 不是停止执行查询，它仅仅是告知正在协调的节点返回到目前为止收集的结果并且关闭连接。在后台，其他的分片可能仍在执行查询即使是结果已经被发送了。)
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/empty-search.html#empty-search>  
 
#### 分页
+ `GET /_search?size=5&from=10` 
+ 考虑到分页过深以及一次请求太多结果的情况，结果集在返回之前先进行排序。 但请记住一个请求经常跨越多个分片，每个分片都产生自己的排序结果，这些结果需要进行集中排序以保证整体顺序是正确的。
+ 在分布式系统中深度分页!!!
<pre>
理解为什么深度分页是有问题的，我们可以假设在一个有 5 个主分片的索引中搜索。 当我们请求结果的第一页（结果从 1 到 10 ），每一个分片产生前 10 的结果，并且返回给 协调节点 ，协调节点对 50 个结果排序得到全部结果的前 10 个。

现在假设我们请求第 1000 页--结果从 10001 到 10010 。所有都以相同的方式工作除了每个分片不得不产生前10010个结果以外。 然后协调节点对全部 50050 个结果排序最后丢弃掉这些结果中的 50040 个结果。

可以看到，在分布式系统中，对结果排序的成本随分页的深度成指数上升。这就是 web 搜索引擎对任何查询都不要返回超过 1000 个结果的原因。
</pre>

+ 如何 能够 有效获取大量的文档:<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/reindex.html>

#### 轻量搜索

+ `GET /_all/tweet/_search?q=tweet:elasticsearch`
+ 有两种形式的 搜索 API：一种是 “轻量的” 查询字符串 版本，要求在查询字符串中传递所有的 参数，
另一种是更完整的 请求体 版本，要求使用 JSON 格式和更丰富的查询表达式作为搜索语言。
+ \+ 前缀表示必须与查询条件匹配。类似地， - 前缀表示一定不与查询条件匹配。
+ _all 字段.`GET /_search?q=mary` 返回包含 mary 的所有文档
当索引一个文档的时候，Elasticsearch 取出所有字段的值拼接成一个大的字符串，作为 _all 字段进行索引。
当 _all 字段不再有用的时候，可以将它置为失效:<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/root-object.html#all-field>

##### 更复杂的查询
+ `+name:(mary john) +date:>2014-09-10 +(aggregations geo)` 转码之后可读性很差(不推荐直接向用户暴露查询字符串搜索功能，除非对于集群和数据来说非常信任他们)
name 字段中包含 mary 或者 john
date 值大于 2014-09-10
_all_ 字段包含 aggregations 或者 geo

---

### 映射和分析
+ 数据在 _all 字段与 data 字段的索引方式不同,通过请求 gb 索引中 tweet 类型的_映射_（或模式定义），让我们看一看 Elasticsearch 是如何解释我们文档结构的: `GET /gb/_mapping/tweet`
+  代表 精确值 （它包括 string 字段）的字段和代表 全文 的字段。这个区别非常重要——它将搜索引擎和所有其他数据库区别开来

#### 精确值 VS 全文
Elasticsearch 中的数据可以概括的分为两类：精确值和全文。

+ 精确值很容易查询。结果是二进制的：要么匹配查询，要么不匹配。
+ 查询全文数据要微妙的多。我们问的不只是“这个文档匹配查询吗”，而是“该文档匹配查询的程度有多大？”换句话说，该文档与给定查询的相关性如何？
(涉及语义分词等) <br/>
为了促进这类在全文域中的查询，Elasticsearch 首先 分析 文档，之后根据结果创建 倒排索引 。

#### 倒排索引
+ Elasticsearch 使用一种称为 倒排索引 的结构，它适用于快速的全文搜索。一个倒排索引由文档中所有不重复词的列表构成，对于其中每个词，有一个包含它的文档列表。
+ 你只能搜索在索引中出现的词条，所以索引文本和查询字符串必须标准化为相同的格式。
+ 分词和标准化的过程称为 分析 .

#### 分析与分析器
+ 分析 包含下面的过程：
    - 首先，将一块文本分成适合于倒排索引的独立的 词条 ，
    - 之后，将这些词条统一化为标准格式以提高它们的“可搜索性”
+ 字符过滤器, 分词器, Token 过滤器

##### 内置分析器
+ 标准分析器
标准分析器是Elasticsearch默认使用的分析器。(它是分析各种语言文本最常用的选择。它根据 Unicode 联盟 定义的 单词边界 划分文本。删除绝大部分标点。最后，将词条小写。)
+ 简单分析器
简单分析器在任何不是字母的地方分隔文本，将词条小写。
+ 空格分析器
空格分析器在空格的地方划分文本。
+ 语言分析器
特定语言分析器可用于 很多语言。它们可以考虑指定语言的特点。

##### 什么时候使用分析器
当我们 索引 一个文档，它的全文域被分析成词条以用来创建倒排索引。

##### 测试分析器
使用 analyze API 来看文本是如何被分析的。在消息体里，指定分析器和要分析的文本

<pre>
GET /_analyze
{
  "analyzer": "standard",
  "text": "Text to analyze"
}
</pre>

##### 指定分析器
手动指定这些域的映射

#### 映射
为了能够将时间域视为时间，数字域视为数字，字符串域视为全文或精确值字符串， Elasticsearch 需要知道每个域中数据的类型。这个信息包含在映射中。
详见<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/mapping.html>

##### 核心简单域类型
Elasticsearch 支持 如下简单域类型：

+ 字符串: string
+ 整数 : byte, short, integer, long
+ 浮点数: float, double
+ 布尔型: boolean
+ 日期: date

当你索引一个包含新域的文档--之前未曾出现-- Elasticsearch 会使用 动态映射 ，通过JSON中基本数据类型，尝试猜测域类型

##### 查看映射
通过 /_mapping ，我们可以查看 Elasticsearch 在一个或多个索引中的一个或多个类型的映射 。

##### 自定义域映射
+ 自定义映射允许你执行下面的操作：
    - 全文字符串域和精确值字符串域的区别
    - 使用特定语言分析器
    - 优化域以适应部分匹配
    - 指定自定义数据格式
    - 还有更多

+ 自定义分析器:<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/custom-analyzers.html>

##### 更新映射
当你首次 创建一个索引的时候，可以指定类型的映射。你也可以使用 /_mapping 为新类型（或者为存在的类型更新映射）增加映射。  

+ 尽管你可以 增加_ 一个存在的映射，你不能 _修改 存在的域映射。如果一个域的映射已经存在，那么该域的数据可能已经被索引。如果你意图修改这个域的映射，索引的数据可能会出错，不能被正常的搜索。
+ 创建一个新索引，指定 tweet 域使用 english 分析器(索引还没有文档)

<pre>
PUT /gb 
{
  "mappings": {
  ...
  }
</pre>

#### 复杂核心域类型

##### 多值域
对于数组，没有特殊的映射需求。任何域都可以包含0、1或者多个值，就像全文域分析得到多个词条。
数组中所有的值必须是相同数据类型的。数组是以多值域 索引的—可以搜索，但是无序的。

##### 空域
在 Lucene 中是不能存储 null 值的，所以我们认为存在 null 值的域为空域。它们将不会被索引.

##### 多层级对象
一个 JSON 原生数据类是 对象 -- 在其他语言中称为哈希，哈希 map，字典或者关联数组。
##### 内部对象是如何索引的
转化

<pre>
{
    "tweet":            [elasticsearch, flexible, very],
    "user.id":          [@johnsmith],
    "user.gender":      [male],
    "user.age":         [26],
    "user.name.full":   [john, smith],
    "user.name.first":  [john],
    "user.name.last":   [smith]
}
</pre>

简单扁平的文档中，没有 user 和 user.name 域。Lucene 索引只有标量和简单值，没有复杂数据结构。

##### 内部对象数组
每个多值域只是一包无序的值，而不是有序数组。
详见:<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/nested-objects.html>

---

### 请求体查询
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/full-body-search.html>

+ 简易 查询 —query-string search— 对于用命令行进行点对点（ad-hoc）查询是非常有用的。 
然而，为了充分利用查询的强大功能，你应该使用 请求体 search API， 之所以称之为请求体查询(Full-Body Search)，
因为大部分参数是通过 Http 请求体而非查询字符串来传递的。

+ 相对于使用晦涩难懂的查询字符串的方式，一个带请求体的查询允许我们使用 查询领域特定语言（query domain-specific language） 或者 Query DSL 来写查询语句。

#### 一个带请求体的 GET 请求？
+ 某些特定语言（特别是 JavaScript）的 HTTP 库是不允许 GET 请求带有请求体的。 事实上，一些使用者对于 GET 请求可以带请求体感到非常的吃惊。
+ 而事实是这个RFC文档 RFC 7231— 一个专门负责处理 HTTP 语义和内容的文档 — 并没有规定一个带有请求体的 GET 请求应该如何处理！
结果是，一些 HTTP 服务器允许这样子，而有一些 — 特别是一些用于缓存和代理的服务器 — 则不允许。
+ 对于一个查询请求，Elasticsearch 的工程师偏向于使用 GET 方式，因为他们觉得它比 POST 能更好的描述信息检索（retrieving information）的行为。
然而，因为带请求体的 GET 请求并不被广泛支持，所以 search API 同时支持 POST 请求：

<pre>
POST /_search
{
  "from": 30,
  "size": 10
}
</pre>

类似的规则可以应用于任何需要带请求体的 GET API。


+ <http://www.cnblogs.com/nankezhishi/archive/2012/06/09/getandpost.html#!comments>
+ 不是所有客户端都支持发起带有body的HTTP GET请求，比如jQuery就直接限制了:<http://www.developerq.com/article/1511062370>

#### 查询表达式
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/query-dsl-intro.html>

祥看文档

#### 查询与过滤
Elasticsearch 使用的查询语言（DSL） 拥有一套查询组件，这些组件可以以无限组合的方式进行搭配。
这套组件可以在以下两种情况下使用：过滤情况（filtering context）和查询情况（query context）。

+ 过滤（filters）已经从技术上被排除了，同时所有的查询（queries）拥有变成不评分查询的能力。
  
  然而，为了明确和简单，我们用 "filter" 这个词表示不评分、只过滤情况下的查询。你可以把 "filter" 、 "filtering query" 和 "non-scoring query" 这几个词视为相同的。
  
  相似的，如果单独地不加任何修饰词地使用 "query" 这个词，我们指的是 "scoring query" 

##### 性能差异
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/_queries_and_filters.html#_性能差异>

+ 过滤查询（Filtering queries）-结果会被缓存到内存中以便快速读取
+ 评分查询（scoring queries） - 查询结果并不缓存 
+ 倒排索引（inverted index）- 一个简单的评分查询在匹配少量文档时可能与一个涵盖百万文档的filter表现的一样好，甚至会更好。
但是在一般情况下，一个filter 会比一个评分的query性能更优异，并且每次都表现的很稳定。

##### 如何选择查询与过滤
通常的规则是，使用 查询（query）语句来进行 全文 搜索或者其它任何需要影响 相关性得分 的搜索。除此以外的情况都使用过滤（filters)。

#### 最重要的查询
+ match_all 查询
+ match 查询
+ multi_match 查询
+ range 查询
+ term 查询
+ terms 查询
+ exists 查询和 missing 查询

#### 组合多查询
+ bool 查询
    - must
      文档 必须 匹配这些条件才能被包含进来。
    - must_not
      文档 必须不 匹配这些条件才能被包含进来。
    - should
      如果满足这些语句中的任意语句，将增加 _score ，否则，无任何影响。它们主要用于修正每个文档的相关性得分。
    - filter
      必须 匹配，但它以不评分、过滤模式来进行。这些语句对评分没有贡献，只是根据过滤标准来排除或包含文档。
如果没有 must 语句，那么至少需要能够匹配其中的一条 should 语句。但，如果存在至少一条 must 语句，则对 should 语句的匹配没有要求。

##### 增加带过滤器（filtering）的查询
+ 通过将 range 查询移到 filter 语句中，我们将它转成不评分的查询，将不再影响文档的相关性排名。由于它现在是一个不评分的查询，可以使用各种对 filter 查询有效的优化手段来提升性能。
+ 所有查询都可以借鉴这种方式。将查询移到 bool 查询的 filter 语句中，这样它就自动的转成一个不评分的 filter 了。

##### constant_score 查询
+ 将一个不变的常量评分应用于所有匹配的文档。它被经常用于你只需要执行一个 filter 而没有其它查询（例如，评分查询）的情况下。
+ term 查询被放置在 constant_score 中，转成不评分的 filter。这种方式可以用来取代只有 filter 语句的 bool 查询。

#### 验证查询
validate-query API 可以用来验证查询是否合法。

+ `GET /gb/tweet/_validate/query`
+ `GET /gb/tweet/_validate/query?explain`

---

### 排序与相关性
默认情况下，返回的结果是按照 相关性 进行排序的——最相关的文档排在最前。

#### 排序
为了按照相关性来排序，需要将相关性表示为一个数值。在 Elasticsearch 中， 相关性得分 由一个浮点数进行表示，并在搜索结果中通过 _score 参数返回， 默认排序是 _score 降序。

+ 我们使用的是 filter （过滤），这表明我们只希望获取匹配 user_id: 1 的文档，并没有试图确定这些文档的相关性。
实际上文档将按照随机顺序返回，并且每个文档都会评为零分。如果评分为零对你造成了困扰，你可以使用 constant_score 查询进行替代.

##### 按照字段的值排序
+ `"sort": { "date": { "order": "desc" }}` , date 字段的值表示为自 epoch (January 1, 1970 00:00:00 UTC)以来的毫秒数
字段将会默认升序排序

##### 多级排序
<pre>
 "sort": [
        { "date":   { "order": "desc" }},
        { "_score": { "order": "desc" }}
    ]
</pre>

+ Query-string 搜索 也支持自定义排序，可以在查询字符串中使用 sort 参数：
`GET /_search?sort=date:desc&sort=_score&q=search`

##### 字段多值的排序
对于数字或日期，你可以将多值字段减为单值，这可以通过使用 min 、 max 、 avg 或是 sum 排序模式 。 

<pre>
"sort": {
    "dates": {
        "order": "asc",
        "mode":  "min"
    }
}
</pre>

#### 字符串排序与多字段
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/multi-fields.html>

为了以字符串字段进行排序，这个字段应仅包含一项： 整个 not_analyzed 字符串。 但是我们仍需要 analyzed 字段，这样才能以全文进行查询

<pre>
"tweet": { 
    "type":     "string",
    "analyzer": "english",
    "fields": {
        "raw": { 
            "type":  "string",
            "index": "not_analyzed"
        }
    }
}
</pre>

tweet 主字段与之前的一样: 是一个 analyzed 全文字段。新的 tweet.raw 子字段是 not_analyzed.
使用 tweet 字段用于搜索，tweet.raw 字段用于排序

+ 以全文 analyzed 字段排序会消耗大量的内存。获取更多信息请看 聚合与分析。
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/aggregations-and-analysis.html>

#### 什么是相关性?
fuzzy 查询会计算与关键词的拼写相似程度，terms 查询会计算 找到的内容与关键词组成部分匹配的百分比，
但是通常我们说的 relevance 是我们用来计算全文本字段的值相对于全文本检索词相似程度的算法。

+ Elasticsearch 的相似度算法 被定义为检索词频率/反向文档频率， TF/IDF ，包括以下内容：
    - 检索词频率
    检索词在该字段出现的频率？出现频率越高，相关性也越高。 字段中出现过 5 次要比只出现过 1 次的相关性高。
    - 反向文档频率
    每个检索词在索引中出现的频率？频率越高，相关性越低。检索词出现在多数文档中会比出现在少数文档中的权重更低。
    - 字段长度准则
    字段的长度是多少？长度越长，相关性越低。 检索词出现在一个短的 title 要比同样的词出现在一个长的 content 字段权重更大。
    
+ 控制相关度:<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/controlling-relevance.html>

##### 理解评分标准
`GET /_search?explain`

+ 输出 explain 结果代价是十分昂贵的，它只能用作调试工具 。千万不要用于生产环境。
+ JSON 形式的 explain 描述是难以阅读的， 但是转成 YAML 会好很多，只需要在参数中加上 format=yaml 。

##### 理解文档是如何被匹配到的
当 explain 选项加到某一文档上时， explain api 会帮助你理解为何这个文档会被匹配，更重要的是，一个文档为何没有被匹配。
`GET /us/tweet/12/_explain`

#### Doc Values 介绍
在 Elasticsearch 中，doc values 就是一种列式存储结构，默认情况下每个字段的 doc values 都是激活的，doc values 是在索引时创建的，
当字段索引时，Elasticsearch 为了能够快速检索，会把字段的值加入倒排索引中，同时它也会存储该字段的 doc values。

+ Elasticsearch 中的 doc vaules 常被应用到以下场景：
  - 对一个字段进行排序
  - 对一个字段进行聚合
  - 某些过滤，比如地理位置过滤
  - 某些与字段相关的脚本计算

????

---

### 执行分布式检索
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/distributed-search.html>

+ 一个 CRUD 操作只对单个文档进行处理，文档的唯一性由 _index, _type, 和 routing values （通常默认是该文档的 _id ）的组合来确定。 
这表示我们确切的知道集群中哪个分片含有此文档。
+ 搜索需要一种更加复杂的执行模型因为我们不知道查询会命中哪些文档: 这些文档有可能在集群的任何分片上。 
一个搜索请求必须询问我们关注的索引（index or indices）的所有分片的某个副本来确定它们是否含有任何匹配的文档。
+ 但是找到所有的匹配文档仅仅完成事情的一半。 在 search 接口返回一个结果之前，多分片中的结果必须组合成单个排序列表。 
为此，搜索被执行成一个两阶段过程，我们称之为 query then fetch 。

#### 查询阶段
在初始 查询阶段 时， 查询会广播到索引中每一个分片拷贝（主分片或者副本分片）。 每个分片在本地执行搜索并构建一个匹配文档的 _优先队列_。

#### 取回阶段
协调节点首先决定哪些文档 确实 需要被取回。例如，如果我们的查询指定了 { "from": 90, "size": 10 } ，最初的90个结果会被丢弃，
只有从第91个开始的10个结果需要被取回。这些文档可能来自和最初搜索请求有关的一个、多个甚至全部分片。

##### 深分页（Deep Pagination）
+ 排序过程可能会变得非常沉重，使用大量的CPU、内存和带宽。因为这个原因，我们强烈建议你不要使用深分页。
+ 实际上， “深分页” 很少符合人的行为。当2到3页过去以后，人会停止翻页，并且改变搜索标准。
会不知疲倦地一页一页的获取网页直到你的服务崩溃的罪魁祸首一般是机器人或者web spider。    
+ 如果你 确实 需要从你的集群取回大量的文档，你可以通过用 scroll 查询禁用排序使这个取回行为更有效率!!!
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/scroll.html>


#### 搜索选项

##### 偏好
+ 偏好这个参数 preference 允许 用来控制由哪些分片或节点来处理搜索请求。
+ 所谓的 bouncing results 问题: 每次用户刷新页面，搜索结果表现是不同的顺序。 
让同一个用户始终使用同一个分片，这样可以避免这种问题， 可以设置 preference 参数为一个特定的任意值比如用户会话ID来解决。

##### 超时问题
参数 timeout 告诉 分片允许处理数据的最大时间。如果没有足够的时间处理所有数据，这个分片的结果可以是部分的，甚至是空数据。

##### 路由
在搜索的时候，不用搜索索引的所有分片，而是通过指定几个 routing 值来限定只搜索几个相关的分片
`GET /_search?routing=user_1,user2`

+  扩容设计 : <https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/scale.html>

##### 搜索类型
缺省的搜索类型是 query_then_fetch 。 在某些情况下，你可能想明确设置 search_type 为 dfs_query_then_fetch 来改善相关性精确度
搜索类型 dfs_query_then_fetch 有预查询阶段，这个阶段可以从所有相关分片获取词频来计算全局词频。

+  被破坏的相关度:<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/relevance-is-broken.html>    
 
#### 游标查询 Scroll 
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/scroll.html>
   
+ scroll 查询 可以用来对 Elasticsearch 有效地执行大批量的文档查询，而又不用付出深度分页那种代价。
+ 游标查询会取某个时间点的快照数据。 查询初始化之后索引上的任何变化会被它忽略。 
它通过保存旧的数据文件来实现这个特性，结果就像保留初始化时的索引 视图 一样。
+ 深度分页的代价根源是结果集全局排序，如果去掉全局排序的特性的话查询结果的成本就会很低。
+ 启用游标查询可以通过在查询的时候设置参数 scroll 的值为我们期望的游标查询的过期时间。 
这个过期时间的参数很重要，因为保持这个游标查询窗口需要消耗资源，所以我们期望如果不再需要维护这种资源就该早点儿释放掉。 
设置这个超时能够让 Elasticsearch 在稍后空闲的时候自动释放这部分资源。
+ 查询的返回结果包括一个字段 _scroll_id， 它是一个base64编码的长字符串 ((("scroll_id"))) 。 
现在我们能传递字段 _scroll_id 到 _search/scroll 查询接口获取下一批结果

<pre>
GET /old_index/_search?scroll=1m 
{
    "query": { "match_all": {}},
    "sort" : ["_doc"], 
    "size":  1000
}
</pre>

+ 尽管我们指定字段 size 的值为1000，我们有可能取到超过这个值数量的文档。 当查询的时候， 字段 size 作用于单个分片，
所以每个批次实际返回的文档数量最大为 size * number_of_primary_shards 。
????那岂不是不能严格进分页,只能自己在程序进行控制,后续的版本有改善优化????!

+ 注意游标查询每次返回一个新字段 _scroll_id。每次我们做下一次游标查询， 我们必须把前一次查询返回的字段 _scroll_id 传递进去。 
当没有更多的结果返回的时候，我们就处理完所有匹配的文档了。!!

---

### 索引管理
介绍管理索引和类型映射的 API 以及一些最重要的设置

#### 创建一个索引
建立索引的过程做更多的控制：我们想要确保这个索引有数量适中的主分片，并且在我们索引任何数据 之前 ，分析器和映射已经被建立好。

+ 禁止自动创建索引，你 可以通过在 config/elasticsearch.yml 的每个节点下添加下面的配置
`action.auto_create_index: false`

+ 用 索引模板(https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/index-templates.html) 来预配置开启自动创建索引。
这在索引日志数据的时候尤其有用：你将日志数据索引在一个以日期结尾命名的索引上，子夜时分(第二天)，一个预配置的新索引将会自动进行创建。

#### 删除一个索引
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/_deleting_an_index.html>

+ 避免意外的大量删除, 你可以在你的 elasticsearch.yml 做如下配置
`action.destructive_requires_name: true`,
可以通过 Cluster State API 动态的更新这个设置

#### 索引设置
通过修改配置来自定义索引行为，详细配置参照 索引模块:<https://www.elastic.co/guide/en/elasticsearch/reference/master/index-modules.html>

+ 两个 最重要的设置：
  - number_of_shards
  每个索引的主分片数，默认值是 5 。这个配置在索引创建后不能修改。
  - number_of_replicas
  每个主分片的副本数，默认值是 1 。对于活动的索引库，这个配置可以随时修改。

+ 可以用 update-index-settings API 动态修改副本数 : `PUT /my_temp_index/_settings`

#### 配置分析器
索引设置的analysis 部分， 用来配置已存在的分析器或针对你的索引创建新的自定义分析器。

<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/configuring-analyzers.html>

#### 自定义分析器
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/custom-analyzers.html>

#### 类型和映射
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/mapping.html>

+ 类型由 名称 —比如 user 或 blogpost —和 映射 组成。
+ Elasticsearch 类型是 以 Lucene 处理文档的这个方式为基础来实现的。一个索引可以有多个类型，这些类型的文档可以存储在相同的索引中。
+ Lucene 没有文档类型的概念，每个文档的类型名被存储在一个叫 _type 的元数据字段上。 
当我们要检索某个类型的文档时, Elasticsearch 通过在 _type 字段上使用过滤器限制只返回这个类型的文档。
+ Lucene 也没有映射的概念。 映射是 Elasticsearch 将复杂 JSON 文档 映射 成 Lucene 需要的扁平化数据的方式。
+ 每个 Lucene 索引中的所有字段都包含一个单一的、扁平的模式。一个特定字段可以映射成 string 类型也可以是 number 类型，但是不能两者兼具。
因为类型是 Elasticsearch 添加的 优于 Lucene 的额外机制（以元数据 _type 字段的形式），在 Elasticsearch 中的所有类型最终都共享相同的映射。

+ 对于整个索引，映射在本质上被 扁平化 成一个单一的、全局的模式。这就是为什么两个类型不能定义冲突的字段：当映射被扁平化时，Lucene 不知道如何去处理

##### 类型结论
+ 类型不适合 完全不同类型的数据 。如果两个类型的字段集是互不相同的，这就意味着索引中将有一半的数据是空的（字段将是 稀疏的 ），最终将导致性能问题。
在这种情况下，最好是使用两个单独的索引。

+ 同一个索引的不同类型的字段必须是相似的,否则放在同一个索引没有意义!!!

#### 根对象
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/root-object.html>

映射的最高一层被称为 根对象 

##### 属性
##### 元数据: _source 字段
##### 元数据: _all 字段
+ 不再需要 _all 字段，你可以通过下面的映射来禁用
`"_all": { "enabled": false }`
+ _all 字段仅仅是一个 经过分词的 string 字段。它使用默认分词器来分析它的值，不管这个值原本所在字段指定的分词器。就像所有 string 字段，你可以配置 _all 字段使用的分词器

##### 元数据：文档标识
+ 文档标识与四个元数据字段 相关：
	- _id
	文档的 ID 字符串
	- _type
	文档的类型名
	- _index
	文档所在的索引
	- _uid
	_type 和 _id 连接在一起构造成 type#id
	
默认情况下， _uid 字段是被存储（可取回）和索引（可搜索）的。 _type 字段被索引但是没有存储， _id 和 _index 字段则既没有被索引也没有被存储，这意味着它们并不是真实存在的。

#### 动态映射
+ 用 dynamic 配置来控制 ，可接受的选项如下：
	- true
	动态添加新的字段--缺省
	- false
	忽略新的字段
	- strict
	如果遇到新字段抛出异常
	
配置参数 dynamic 可以用在根 object 或任何 object 类型的字段上。你可以将 dynamic 的默认值设置为 strict , 而只在指定的内部对象中开启它.

+ 把 dynamic 设置为 false 一点儿也不会改变 _source 的字段内容。 _source 仍然包含被索引的整个JSON文档。只是新的字段不会被加到映射中也不可搜索。

#### 自定义动态映射
+ 日期检测可以通过在根对象上设置 date_detection 为 false 来关闭
+ 
Elasticsearch 判断字符串为日期的规则可以通过[dynamic_date_formats setting](https://www.elastic.co/guide/en/elasticsearch/reference/master/dynamic-field-mapping.html#date-detection)来设置。

##### 动态模板
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/custom-dynamic-mapping.html#dynamic-templates>

+ 使用 dynamic_templates ，你可以完全控制 新检测生成字段的映射。你甚至可以通过字段名称或数据类型来应用不同的映射。
+ 详见：<https://www.elastic.co/guide/en/elasticsearch/reference/master/dynamic-mapping.html>

#### 缺省映射
通常，一个索引中的所有类型共享相同的字段和设置。 _default_ 映射更加方便地指定通用设置，而不是每次创建新类型时都要重复设置。 _default_ 映射是新类型的模板。在设置 _default_ 映射之后创建的所有类型都将应用这些缺省的设置，除非类型在自己的映射中明确覆盖这些设置。

可以使用 _default_ 映射为所有的类型禁用 _all 字段， 而只在 blog 类型启用

#### 重新索引你的数据
尽管可以增加新的类型到索引中，或者增加新的字段到类型中，但是不能添加新的分析器或者对现有的字段做改动。 如果你那么做的话，结果就是那些已经被索引的数据就不正确， 搜索也不能正常工作。

+ 对现有数据的这类改变最简单的办法就是重新索引：用新的设置创建新的索引并把文档从旧的索引复制到新的索引。
+ 为了有效的重新索引所有在旧的索引中的文档，用 scroll 从旧的索引检索批量文档 ， 然后用 bulk API 把文档推送到新的索引中。
(从Elasticsearch v2.3.0开始， Reindex API 被引入。它能够对文档重建索引而不需要任何插件或外部工具。)

#### 索引别名和零停机
重建索引的问题是必须更新应用中的索引名称。 索引别名就是用来解决这个问题的！

+ 索引 别名 就像一个快捷方式或软连接，可以指向一个或多个索引，也可以给任何一个需要索引名的API来使用。
+ 怎样使用别名在零停机下从旧索引切换到新索引。
+ 即使你认为现在的索引设计已经很完美了，在生产环境中，还是有可能需要做一些修改的。<br/>
做好准备：在你的应用中使用别名而不是索引名。然后你就可以在任何时候重建索引。别名的开销很小，应该广泛使用。

---

### 分片内部原理

#### 使文本可被搜索
+ 当讨论倒排索引时，我们会谈到 文档 标引，因为历史原因，倒排索引被用来对整个非结构化文本文档进行标引。 Elasticsearch 中的 文档 是有字段和值的结构化 JSON 文档。事实上，在 JSON 文档中， 每个被索引的字段都有自己的倒排索引。

+ 这个倒排索引相比特定词项出现过的文档列表，会包含更多其它信息。它会保存每一个词项出现过的文档总数， 在对应的文档中一个具体词项出现的总次数，词项在文档中的顺序，每个文档的长度，所有文档的平均长度，等等。这些统计信息允许 Elasticsearch 决定哪些词比其它词更重要，哪些文档比其它文档更重要

##### 不变性
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/making-text-searchable.html#_不变性>

倒排索引被写入磁盘后是 不可改变 的:它永远不会修改。 不变性有重要的价值
	
不可以变不会有问题？？？

#### 动态更新索引
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/dynamic-indices.html>

+ 怎样在保留不变性的前提下实现倒排索引的更新？ 答案是: 用更多的索引。
	- 通过增加新的补充索引来反映新近的修改，而不是直接重写整个倒排索引。每一个倒排索引都会被轮流查询到--从最早的开始--查询完后再对结果进行合并。
	- Elasticsearch 基于 Lucene, 这个 java 库引入了 按段搜索 的概念。 每一 段 本身都是一个倒排索引， 但 索引 在 Lucene 中除表示所有 段 的集合外， 还增加了 提交点 的概念 

##### 索引与分片的比较
被混淆的概念是，一个 Lucene 索引 我们在 Elasticsearch 称作 分片 。 一个 Elasticsearch 索引 是分片的集合。 当 Elasticsearch 在索引中搜索的时候， 他发送查询到每一个属于索引的分片(Lucene 索引)，然后像 执行分布式检索 提到的那样，合并每个分片的结果到一个全局的结果集。

##### 删除和更新
段是不可改变的，所以既不能从把文档从旧的段中移除，也不能修改旧的段来进行反映文档的更新。 取而代之的是，每个提交点会包含一个 .del 文件，文件中会列出这些被删除文档的段信息。

+ 当一个文档被 “删除” 时，它实际上只是在 .del 文件中被 标记 删除。一个被标记删除的文档仍然可以被查询匹配到， 但它会在最终结果被返回前从结果集中移除。
+ 文档更新也是类似的操作方式：当一个文档被更新时，旧版本文档被标记删除，文档的新版本被索引到一个新的段中。 可能两个版本的文档都会被一个查询匹配到，但被删除的那个旧版本文档在结果集返回前就已经被移除。

+ 在 段合并 , 我们展示了一个被删除的文档是怎样被文件系统移除的。<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/merge-process.html>	

#### 近实时搜索
<https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/near-real-time.html>

+ 随着按段（per-segment）搜索的发展， 一个新的文档从索引到可被搜索的延迟显著降低了。
+ 磁盘在这里成为了瓶颈。 提交（Commiting）一个新的段到磁盘需要一个 fsync 来确保段被物理性地写入磁盘，这样在断电的时候就不会丢失数据。 但是 fsync 操作代价很大; 如果每次索引一个文档都去执行一次的话会造成很大的性能问题。
+ 我们需要的是一个更轻量的方式来使一个文档可被搜索，这意味着 fsync 要从整个过程中被移除。
+ 在Elasticsearch和磁盘之间是文件系统缓存。新段会被先写入到文件系统缓存--这一步代价会比较低，稍后再被刷新到磁盘--这一步代价比较高。不过只要文件已经在缓存中， 就可以像其它文件一样被打开和读取了。

+ 重点：新段会被先写入到文件系统缓存，refresh则是从这个文件缓存获取新段（写入和打开一个新段的轻量的过程叫做 refresh ）<br/>
默认一秒刷新，可以设置

##### refresh API
在 Elasticsearch 中，写入和打开一个新段的轻量的过程叫做 refresh 。 默认情况下每个分片会每秒自动刷新一次。这就是为什么我们说 Elasticsearch 是 近 实时搜索: 文档的变化并不是立即对搜索可见，但会在一秒之内变为可见。

<pre>
POST /_refresh 
POST /blogs/_refresh 

PUT /my_logs
{
  "settings": {
    "refresh_interval": "30s" 
  }
}
</pre>
		
#### 持久化变更
如果没有用 fsync 把数据从文件系统缓存刷（flush）到硬盘，我们不能保证数据在断电甚至是程序正常退出之后依然存在。为了保证 Elasticsearch 的可靠性，需要确保数据变化被持久化到磁盘。
		
+ 在 动态更新索引，我们说一次完整的提交会将段刷到磁盘，并写入一个包含所有段列表的提交点。Elasticsearch 在启动或重新打开一个索引的过程中使用这个提交点来判断哪些段隶属于当前分片。
+ 即使通过每秒刷新（refresh）实现了近实时搜索，我们仍然需要经常进行完整提交来确保能从失败中恢复。
+ Elasticsearch 增加了一个 translog ，或者叫事务日志，在每一次对 Elasticsearch 进行操作时均进行了日志记录。通过 translog
+ translog 提供所有还没有被刷到磁盘的操作的一个持久化纪录。当 Elasticsearch 启动的时候， 它会从磁盘中使用最后一个提交点去恢复已知的段，并且会重放 translog 中所有在最后一次提交后发生的变更操作。
+ translog 也被用来提供实时 CRUD 。当你试着通过ID查询、更新、删除一个文档，它会在尝试从相应的段中检索之前， 首先检查 translog 任何最近的变更。这意味着它总是能够实时地获取到文档的最新版本。

##### flush API
这个执行一个提交并且截断 translog 的行为在 Elasticsearch 被称作一次 flush 。 分片每30分钟被自动刷新（flush），或者在 translog 太大的时候也会刷新。

+ flush API 可以 被用来执行一个手工的刷新（flush）
`POST /blogs/_flush` ,	`POST /_flush?wait_for_ongoin`

##### Translog 有多安全
translog 的目的是保证操作不会丢失。

+ 在文件被fsync+到磁盘前，被写入的文件在重启之后就会丢失。默认 translog 是每 5 秒被 +fsync' 刷新到硬盘， 或者 是在写请求完成之后 执行(e.g. index, delete, update, bulk)。这个过程在主分片和复制分片都会发生。最终， 基本上，这意味着在整个请求被+fsync+到主分片和复制分片的translog之前，你的客户端不会得到一个 200 OK 响应。(在每次请求后都执行一个 fsync 会带来一些性能损失，尽管实践表明这种损失相对较小)

+ 对于一些大容量的偶尔丢失几秒数据问题也并不严重的集群，使用异步的 fsync 还是比较有益的。比如，写入的数据被缓存到内存中，并且每5秒执行一次 fsync 。
这个行为可以通过设置 durability 参数为 async 来启用(这个选项可以针对索引单独设置，并且可以动态进行修改。)
<br/>
如果你不确定这个行为的后果，最好是使用默认的参数`（ "index.translog.durability": "request" ）`来避免数据丢失。

#### 段合并
+ 由于自动刷新流程每秒会创建一个新的段 ，这样会导致短时间内的段数量暴增。而段数目太多会带来较大的麻烦。 每一个段都会消耗文件句柄、内存和cpu运行周期。更重要的是，每个搜索请求都必须轮流检查每个段；所以段越多，搜索也就越慢。
+ Elasticsearch通过在后台进行段合并来解决这个问题。小的段被合并到大的段，然后这些大的段再被合并到更大的段。
+ 段合并的时候会将那些旧的已删除文档 从文件系统中清除。 被删除的文档（或被更新文档的旧版本）不会被拷贝到新的大段中。
+ 一旦合并结束，老的段被删除.
+ 合并大的段需要消耗大量的I/O和CPU资源，如果任其发展会影响搜索性能。Elasticsearch在默认情况下会对合并流程进行资源限制，所以搜索仍然 有足够的资源很好地执行。
+ 合并调整的建议:  <https://elasticsearch.cn/book/elasticsearch_definitive_guide_2.x/indexing-performance.html#segments-and-merging>

##### optimize API
optimize API大可看做是 强制合并 API 。它会将一个分片强制合并到 max_num_segments 参数指定大小的段数目。 这样做的意图是减少段的数量（通常减少到一个），来提升搜索性能。

+ 在特定情况下，使用 optimize API 颇有益处。例如在日志这种用例下，每天、每周、每月的日志被存储在一个索引中。 老的索引实质上是只读的；它们也并不太可能会发生变化。

+ 请注意，使用 optimize API 触发段合并的操作一点也不会受到任何资源上的限制。这可能会消耗掉你节点上全部的I/O资源, 使其没有余裕来处理搜索请求，从而有可能使集群失去响应。 如果你想要对索引执行 `optimize`，你需要先使用分片分配（查看 迁移旧索引）把索引移到一个安全的节点，再执行。





	

  


