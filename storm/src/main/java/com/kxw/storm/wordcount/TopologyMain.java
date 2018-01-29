package com.kxw.storm.wordcount;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import com.kxw.storm.wordcount.bolts.WordCounter;
import com.kxw.storm.wordcount.bolts.WordNormalizer;
import com.kxw.storm.wordcount.spouts.WordReader;

/**
 * <a href='http://ifeve.com/getting-started-with-storm-2/'>@link</a>
 * mvn exec:java -Dexec.mainClass=”TopologyMain” -Dexec.args=”src/main/resources/words.txt"
 *
 * 创建一个简单的拓扑，数单词数量
 * 这是一个非常强大的拓扑，因为它能够扩展到几乎无限大的规模，而且只需要做一些小修改，就能用它构建一个统计系统。举个例子，我们可以修改一下工程用来找出 Twitter 上的热点话题。
 * 用一个 spout 读取文本，第一个 bolt 用来标准化单词，第二个 bolt 为单词计数
 *
 * 单词 is 和 great 分别在每个 WordCounter 各计数一次。怎么会这样？当你调用shuffleGrouping 时，就决定了 Storm 会以随机分配的方式向你的 bolt 实例发送消息。在这个例子中，理想的做法是相同的单词问题发送给同一个 WordCounter 实例。你把shuffleGrouping(“word-normalizer”) 换成 fieldsGrouping(“word-normalizer”, new Fields(“word”)) 就能达到目的。
 */
public class TopologyMain {
    public static void main(String[] args) throws InterruptedException {
        //定义拓扑
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("word-reader", new WordReader());
        builder.setBolt("word-normalizer", new WordNormalizer()).shuffleGrouping("word-reader");
        //builder.setBolt("word-normalizer", new WordNormalizer()).customGrouping("word-reader", new ModuleGrouping());
        //builder.setBolt("word-counter", new WordCounter()).shuffleGrouping("word-normalizer");
        builder.setBolt("word-counter", new WordCounter(), 2).fieldsGrouping("word-normalizer", new Fields("word"));




        //配置
        Config conf = new Config();
        //conf.put("wordsFile", args[0]);
        conf.put("wordsFile", "storm/src/main/resources/words.txt");
        conf.setDebug(true);

        //本地模式 运行拓扑
        //这个设置一个spout task上面最多有多少个没有处理的tuple（没有ack/failed）回复， 我们推荐你设置这个配置，以防止tuple队列爆掉。
        conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("Getting-Started-Topologie", conf, builder.createTopology());
        Thread.sleep(2000);

        //clean up 运行真不稳定

        cluster.shutdown();

        /**
         *
         -- 单词数 【word-counter-2】 --
         but: 1
         storm: 3
         great: 2
         an: 1
         really: 1

         -- 单词数 【word-counter-3】 --
         very: 1
         test: 1
         application: 1
         powerful: 1
         are: 1
         is: 2
         simple: 1

         */

       /* try {
            StormSubmitter.submitTopology("Count-Word-Topology-With_Refresh-Cache", conf, builder.createTopology());
        } catch (AlreadyAliveException e) {
            e.printStackTrace();
        } catch (InvalidTopologyException e) {
            e.printStackTrace();
        } catch (AuthorizationException e) {
            e.printStackTrace();
        }*/

    }
}