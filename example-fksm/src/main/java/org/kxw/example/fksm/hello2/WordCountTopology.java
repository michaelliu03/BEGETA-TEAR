package org.kxw.example.fksm.hello2;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;
import org.kxw.example.fksm.hello.MessageScheme;

import java.util.HashMap;
import java.util.Map;

/**
 * <a href = 'http://www.howardliu.cn/a-few-notes-about-storm/'>@link</a>
 */
public class WordCountTopology {
    private static final String KAFKA_SPOUT_ID = "kafkaSpout";
    private static final String SENTENCE_BOLT_ID = "sentenceBolt";
    private static final String SPLIT_BOLT_ID = "sentenceSplitBolt";
    private static final String WORD_COUNT_BOLT_ID = "sentenceWordCountBolt";
    private static final String REPORT_BOLT_ID = "reportBolt";
    private static final String KAFKA_BOLT_ID = "kafkabolt";
    private static final String CONSUME_TOPIC = "sentenceTopic";
    private static final String PRODUCT_TOPIC = "wordCountTopic";
    private static final String ZK_ROOT = "/topology/root";
    private static final String ZK_ID = "wordCount";
    private static final String DEFAULT_TOPOLOGY_NAME = "sentenceWordCountKafka";

    public static void main(String[] args) throws Exception {
        // 配置Zookeeper地址
        BrokerHosts brokerHosts = new ZkHosts("zk1:2181,zk2:2281,zk3:2381");
        // 配置Kafka订阅的Topic，以及zookeeper中数据节点目录和名字
        SpoutConfig spoutConfig = new SpoutConfig(brokerHosts, CONSUME_TOPIC, ZK_ROOT, ZK_ID);
        spoutConfig.scheme = new SchemeAsMultiScheme(new MessageScheme());

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(KAFKA_SPOUT_ID, new KafkaSpout(spoutConfig));
        builder.setBolt(SENTENCE_BOLT_ID, new SentenceBolt()).shuffleGrouping(KAFKA_SPOUT_ID);
        builder.setBolt(SPLIT_BOLT_ID, new SplitSentenceBolt()).shuffleGrouping(KAFKA_SPOUT_ID);
        builder.setBolt(WORD_COUNT_BOLT_ID, new WordCountBolt()).fieldsGrouping(SPLIT_BOLT_ID, new Fields("word"));
        builder.setBolt(REPORT_BOLT_ID, new ReportBolt()).shuffleGrouping(WORD_COUNT_BOLT_ID);
        builder.setBolt(KAFKA_BOLT_ID, new KafkaBolt<String, Long>()).shuffleGrouping(REPORT_BOLT_ID);

        Config config = new Config();
        Map<String, String> map = new HashMap<>();
        map.put("metadata.broker.list", "dev2_55.wfj-search:9092");// 配置Kafka broker地址
        map.put("serializer.class", "kafka.serializer.StringEncoder");// serializer.class为消息的序列化类
        config.put("kafka.broker.properties", map);// 配置KafkaBolt中的kafka.broker.properties
        config.put("topic", PRODUCT_TOPIC);// 配置KafkaBolt生成的topic

        if (args.length == 0) {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(DEFAULT_TOPOLOGY_NAME, config, builder.createTopology());
            Utils.sleep(100000);
            cluster.killTopology(DEFAULT_TOPOLOGY_NAME);
            cluster.shutdown();
        } else {
            config.setNumWorkers(1);
            StormSubmitter.submitTopology(args[0], config, builder.createTopology());
        }
    }
}