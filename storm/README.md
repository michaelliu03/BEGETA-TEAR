+ 流式大数据处理的三种框架：Storm，Spark和Samza:<http://www.csdn.net/article/2015-03-09/2824135>
+ Storm与Spark：谁才是我们的实时处理利器:<http://developer.51cto.com/art/201412/460116.htm>

+ Storm入门: <https://www.w3cschool.cn/storm/>
---

## Storm
+ 暴风雨，暴风雪
+ spout 美[spaʊt] 龙卷，读取原始数据, 为bolt提供数据
+ bolt 美[boʊlt] 雷电，从spout或其它bolt接收数据，并处理数据，处理结果可作为其它bolt的数据源或最终结果
+ nimbus 美[ˈnɪmbəs] 雨云，主节点的守护进程，负责为工作节点分发任务。
+ topology [tə'pɑ:lədʒɪ] 拓扑结构，Storm的一个任务单元
+ define field(s) 定义域，由spout或bolt提供，被bolt接收
+ Worker：Topology跨一个或多个Worker节点的进程执行。

+ 在Storm集群中，有两类节点：主节点master node和工作节点worker nodes。主节点运行着一个叫做Nimbus的守护进程。这个守护进程负责在集群中分发代码，为工作节点分配任务，并监控故障。Supervisor守护进程作为拓扑的一部分运行在工作节点上。一个Storm拓扑结构在不同的机器上运行着众多的工作节点。
+ 因为Storm在Zookeeper或本地磁盘上维持所有的集群状态，守护进程可以是无状态的而且失效或重启时不会影响整个系统的健康
+ 在系统底层，Storm使用了zeromq(0mq, zeromq(http://www.zeromq.org))。这是一种先进的，可嵌入的网络通讯库，它提供的绝妙功能使Storm成为可能。下面列出一些zeromq的特性。


+ 在 spout 和 bolts 之间通过 shuffleGrouping 方法连接。这种分组方式决定了 Storm 会以随机分配方式从源节点向目标节点发送消息。

#### 操作模式
1. 本地模式: 在本地模式下，Storm 拓扑结构运行在本地计算机的单一 JVM 进程上。这个模式用于开发、测试以及调试，因为这是观察所有组件如何协同工作的最简单方法。在这种模式下，我们可以调整参数，观察我们的拓扑结构如何在不同的 Storm 配置环境下运行。
    - 在本地模式下，跟在集群环境运行很像。不过很有必要确认一下所有组件都是线程安全的，因为当把它们部署到远程模式时它们可能会运行在不同的 JVM 进程甚至不同的物理机上，这个时候它们之间没有直接的通讯或共享内存。
2. 远程模式: 在远程模式下，我们向 Storm 集群提交拓扑，它通常由许多运行在不同机器上的流程组成。远程模式不会出现调试信息， 因此它也称作生产模式。不过在单一开发机上建立一个 Storm 集群是一个好主意，可以在部署到生产环境之前，用来确认拓扑在集群环境下没有任何问题。
    

+ 可以在主类中创建拓扑和一个本地集群对象，以便于在本地测试和调试。LocalCluster 可以通过 Config 对象，让你尝试不同的集群配置。比如，当使用不同数量的工作进程测试你的拓扑时，如果不小心使用了某个全局变量或类变量，你就能够发现错误。
+ 所有拓扑节点的各个进程必须能够独立运行，而不依赖共享数据（也就是没有全局变量或类变量），因为当拓扑运行在真实的集群环境时，这些进程可能会运行在不同的机器上。
+ /Users/kingsonwu/Personal/github/BEGETA-TEAR/storm/src/main/java/com/kxw/storm/wordcount/TopologyMain.java
+ 调用shuffleGrouping 时，就决定了 Storm 会以随机分配的方式向你的 bolt 实例发送消息。在这个例子中，理想的做法是相同的单词问题发送给同一个 WordCounter 实例。你把shuffleGrouping(“word-normalizer”) 换成 fieldsGrouping(“word-normalizer”, new Fields(“word”)) 就能达到目的。


### Storm 拓扑
+ 数据流组
    - 设计一个拓扑时，你要做的最重要的事情之一就是定义如何在各组件之间交换数据（数据流是如何被 bolts 消费的）。一个据数流组指定了每个 bolt 会消费哪些数据流，以及如何消费它们。
    1. 随机数据流组
        + 随机流组是最常用的数据流组。它只有一个参数（数据源组件），并且数据源会向随机选择的 bolt 发送元组，保证每个消费者收到近似数量的元组。
    2. 域数据流组
        + 域数据流组允许你基于元组的一个或多个域控制如何把元组发送给 bolts。 它保证拥有相同域组合的值集发送给同一个 bolt。 
    3. 全部数据流组
        + 全部数据流组，为每个接收数据的实例复制一份元组副本。这种分组方式用于向 bolts 发送信号。比如，你要刷新缓存，你可以向所有的 bolts 发送一个刷新缓存信号。
    4. 自定义数据流组
        + 通过实现 backtype.storm.grouping.CustormStreamGrouping 接口创建自定义数据流组，让你自己决定哪些 bolt 接收哪些元组。(ModuleGrouping)
    5. 直接数据流组
        + 这是一个特殊的数据流组，数据源可以用它决定哪个组件接收元组。与前面的例子类似，数据源将根据单词首字母决定由哪个 bolt 接收元组。要使用直接数据流组，
            1. 在 WordNormalizer bolt 中，使用 emitDirect 方法代替 emit。
            2. 在 prepare 方法中计算任务数: `this.numCounterTasks = context.getComponentTasks("word-counter");`
            3. 在拓扑定义中指定数据流将被直接分组: builder.setBolt("word-counter", new WordCounter(),2).directGrouping("word-normalizer");                                             
    6. 全局数据流组
        + 全局数据流组把所有数据源创建的元组发送给单一目标实例（即拥有最低 ID 的任务）。
        
#### LocalCluster VS StormSubmitter
1. 用 LocalCluster 在你的本地机器上运行了一个拓扑。Storm 的基础工具，使你能够在自己的计算机上方便的运行和调试不同的拓扑。                                             
2. 把自己的拓扑提交给运行中的 Storm 集群.把 LocalCluster 换成 StormSubmitter 并实现 submitTopology 方法， 它负责把拓扑发送给集群。
    - 当你使用 StormSubmitter 时，你就不能像使用 LocalCluster 时一样通过代码控制集群了。
    接下来，把源码压缩成一个 jar 包，运行 Storm 客户端命令，把拓扑提交给集群。
    - 使用 storm jar 命令提交拓扑: `storm jar allmycode.jar org.me.MyTopology arg1 arg2 arg3。`                                         
    - 想停止或杀死它:storm kill Count-Word-Topology-With-Refresh-Cache  
    - 拓扑名称必须保证惟一性。
        
#### DRPC 拓扑(TODO 另外查资料)
+ 有一种特殊的拓扑类型叫做分布式远程过程调用（DRPC），它利用 Storm 的分布式特性执行远程过程调用（RPC）
+ Storm 提供了一些用来实现 DRPC 的工具。第一个是 DRPC 服务器，它就像是客户端和 Storm 拓扑之间的连接器，作为拓扑的 spout 的数据源。它接收一个待执行的函数和函数参数，然后对于函数操作的每一个数据块，这个服务器都会通过拓扑分配一个请求 ID 用来识别 RPC 请求。拓扑执行最后的 bolt 时，它必须分配 RPC 请求 ID 和结果，使 DRPC 服务器把结果返回正确的客户端。
+ 使用 DRPCClient 类连接远程 DRPC 服务器。DRPC 服务器暴露了 Thrift API，因此可以跨语言编程；并且不论是在本地还是在远程运行DRPC服务器，它们的 API 都是相同的。 对于采用 Storm 配置的 DRPC 配置参数的 Storm 集群，调用构建器对象的createRemoteTopology 向 Storm 集群提交一个拓扑，而不是调用 createLocalTopology。        
        

### Storm Spouts

#### 可靠的消息 VS 不可靠的消息
+ 对于 Storm 来说，根据每个拓扑的需要担保消息的可靠性是开发者的责任。这就涉及到消息可靠性和资源消耗之间的权衡。高可靠性的拓扑必须管理丢失的消息，必然消耗更多资源；可靠性较低的拓扑可能会丢失一些消息，占用的资源也相应更少。不论选择什么样的可靠性策略，Storm 都提供了不同的工具来实现它。
+ 要在 spout 中管理可靠性，你可以在分发时包含一个元组的消息 ID（collector.emit(new Values(…),tupleId)）。在一个元组被正确的处理时调用 ack** 方法，而在失败时调用 fail** 方法。当一个元组被所有的靶 bolt 和锚 bolt 处理过，即可判定元组处理成功
+ 发生下列情况之一时为元组处理失败：
    - 提供数据的 spout 调用 collector.fail(tuple)
    - 处理时间超过配置的超时时间
(说得太烂,再看TODO)

#### 获取数据
1. 直接连接
    - 在一个直接连接的架构中，spout 直接与一个消息分发器连接。
2. 消息队列
    - 通过一个队列系统接收来自消息分发器的消息，并把消息转发给 spout。更进一步的做法是，把队列系统作为 spout 和数据源之间的中间件
    - 不推荐在 spout 创建太多线程，因为每个 spout 都运行在不同的线程。一个更好的替代方案是增加拓扑并行性，也就是通过 Storm 集群在分布式环境创建更多线程。
3. DRPC
    - DRPCSpout从DRPC 服务器接收一个函数调用，并执行它。对于最常见的情况，使用 backtype.storm.drpc.DRPCSpout 就足够了，不过仍然有可能利用 Storm 包内的DRPC类创建自己的实现。
    
### Storm Bolts

#### Bolt 生命周期
+ Bolt 是这样一种组件，它把元组作为输入，然后产生新的元组作为输出。实现一个 bolt 时，通常需要实现 IRichBolt 接口。Bolts 对象由客户端机器创建，序列化为拓扑，并提交给集群中的主机。然后集群启动工人进程反序列化 bolt，调用 prepare****，最后开始处理元组。
+ 要创建一个 bolt 对象，它通过构造器参数初始化成员属性，bolt 被提交到集群时，这些属性值会随着一起序列化。
+ Bolts拥有如下方法:

<pre>
declareOutputFields(OutputFieldsDeclarer declarer)
    为bolt声明输出模式
prepare(java.util.Map stormConf, TopologyContext context, OutputCollector collector)
    仅在bolt开始处理元组之前调用
execute(Tuple input)
    处理输入的单个元组
cleanup()
    在bolt即将关闭时调用  
</pre>

+ 在许多情况下，你想确保消息在整个拓扑范围内都被处理过了

#### 可靠的 bolts 和不可靠的 bolts
+ Storm 保证通过 spout 发送的每条消息会得到所有 bolt 的全面处理。基于设计上的考虑，这意味着你要自己决定你的 bolts 是否保证这一点。
+ 拓扑是一个树型结构，消息（元组）穿过其中一条或多条分支。树上的每个节点都会调用 ack(tuple) 或 fail(tuple)，Storm 因此知道一条消息是否失败了，并通知那个/那些制造了这些消息的 spout(s)。
+ 既然一个 Storm 拓扑运行在高度并行化的环境里，跟踪始发 spout 实例的最好方法就是在消息元组内包含一个始发 spout 引用。这一技巧称做锚定(译者注：原文为Anchoring)。
+ 锚定发生在调用 collector.emit() 时(collector.emit(tuple, new Values(word));)。正如前面提到的，Storm 可以沿着元组追踪到始发spout。collector.ack(tuple) 和 collector.fail(tuple)会告知 spout 每条消息都发生了什么。
当树上的每条消息都已被处理了，Storm 就认为来自 spout 的元组被全面的处理了。如果一个元组没有在设置的超时时间内完成对消息树的处理，就认为这个元组处理失败。默认超时时间为30秒。
+ 可以通过修改Config.TOPOLOGY_MESSAGE_TIMEOUT修改拓扑的超时时间。 当然了spout需要考虑消息的失败情况，并相应的重试或丢弃消息。
+ !!!!! 你处理的每条消息要么是确认的（译者注：collector.ack()）要么是失败的（译者注：collector.fail()）。Storm 使用内存跟踪每个元组，所以如果你不调用这两个方法，该任务最终将耗尽内存。   
 
#### 多数据流
+ 一个 bolt 可以使用 emit(streamId, tuple) 把元组分发到多个流，其中参数 streamId 是一个用来标识流的字符串。然后，你可以在 TopologyBuilder 决定由哪个流订阅它。
 
#### 多锚定
+ 为了用 bolt 连接或聚合数据流，你需要借助内存缓冲元组。为了在这一场景下确保消息完成，你不得不把流锚定到多个元组上。可以向 emit 方法传入一个元组列表来达成目的。
```java
List anchors = new ArrayList();
anchors.add(tuple1);
anchors.add(tuple2);
collector.emit(anchors, values);
``` 
通过这种方式，bolt 在任意时刻调用 ack 或 fail 方法，都会通知消息树，而且由于流锚定了多个元组，所有相关的 spout 都会收到通知。

#### 使用 IBasicBolt 自动确认
+ 在许多情况下都需要消息确认。简单起见，Storm 提供了另一个用来实现bolt 的接口，IBasicBolt。对于该接口的实现类的对象，会在执行 execute 方法之后自动调用 ack 方法。
分发消息的 BasicOutputCollector 自动锚定到作为参数传入的元组。


### 使用非 JVM 语言开发
+ 多语言协议是 Storm 实现的一种特殊的协议，它使用标准输入输出作为 spout 和 bolt 进程间的通讯通道。消息以 JSON 格式或纯文本格式在通道中传递。
+ public class NumberGeneratorSpout extends ShellSpout implements IRichSpout (执行php脚本文件)
+ 所有的脚本文件保存在你的工程目录下的一个名为multilang/resources 的子目录中。这个子目录被包含在发送给工人进程的 jar 文件中。

### Storm 事务性拓扑
+ 使用 Storm 编程，可以通过调用 ack 和 fail 方法来确保一条消息的处理成功或失败。不过当元组被重发时，会发生什么呢？你又该如何砍不会重复计算？
+  使用事务性拓扑时，数据源要能够重发批次，有时候甚至要重复多次。因此确认你的数据源——你连接到的那个 spout ——具备这个能力。 
+ TransactionalTopologyBuilder


### Storm ack机制
+ 首先开启storm tracker机制的前提是，
    1. 在spout emit tuple的时候，要加上第3个参数messageid 
    2. 在配置中acker数目至少为1 
    3. 在bolt emit的时候，要加上第二个参数anchor tuple，以保持tracker链路
    
+ 关闭Ack机制有两种形式
    1. spout发送数据不带上msgid(msgid是emit第二个参数 , emit(tuple,msgid))(在tuple层面去掉可靠性。 你可以在发射tuple的时候不指定messageid来达到不跟粽某个特定的spout tuple的目的。)
    2. 设置Ack等于0(Config.TOPOLOGY_ACKERS 设置成 0. 在这种情况下， storm会在spout发射一个tuple之后马上调用spout的ack方法。也就是说这个tuple树不会被跟踪。)
    3. 如果你对于一个tuple树里面的某一部分到底成不成功不是很关心，那么可以在发射这些tuple的时候unanchor它们。 这样这些tuple就不在tuple树里面， 也就不会被跟踪了
    
  
  
      