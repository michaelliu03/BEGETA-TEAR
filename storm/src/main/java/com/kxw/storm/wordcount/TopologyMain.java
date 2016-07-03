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
 */
public class TopologyMain {
    public static void main(String[] args) throws InterruptedException {
        //定义拓扑
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("word-reader", new WordReader());
        builder.setBolt("word-normalizer", new WordNormalizer()).shuffleGrouping("word-reader");
        builder.setBolt("word-counter", new WordCounter(), 2).fieldsGrouping("word-normalizer", new Fields("word"));

        //配置
        Config conf = new Config();
        //conf.put("wordsFile", args[0]);
        conf.put("wordsFile", "src/main/resources/words.txt");
        conf.setDebug(false);

        //运行拓扑
        conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("Getting-Started-Topologie", conf, builder.createTopology());
        Thread.sleep(1000);
        cluster.shutdown();
    }
}