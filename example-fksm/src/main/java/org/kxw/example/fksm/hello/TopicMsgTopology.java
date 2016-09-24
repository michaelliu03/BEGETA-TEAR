
package org.kxw.example.fksm.hello;

import org.apache.kafka.common.utils.Utils;
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

import java.util.Properties;

/**
 * <a href = 'http://www.howardliu.cn/a-few-notes-about-storm/'>@link</a>
 * Bolt KafkaBolt用来实现数据写入kafka的目的
 */
public class TopicMsgTopology {
    public static void main(String[] args) throws Exception {
        // 配置Zookeeper地址
        BrokerHosts brokerHosts = new ZkHosts("localhost:2181");
        // 配置Kafka订阅的Topic，以及zookeeper中数据节点目录和名字
        SpoutConfig spoutConfig = new SpoutConfig(brokerHosts, "test", "/topology/root", "topicMsgTopology");
        // 配置KafkaBolt中的kafka.broker.properties
        Config conf = new Config();
        Properties props = new Properties();
        // 配置Kafka broker地址
        props.put("metadata.broker.list", "localhost:9092");
        //props.put("bootstrap.servers", "localhost:9092");
        // serializer.class为消息的序列化类
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        conf.put("kafka.broker.properties", props);
        //conf.put("bolt.specfied.properties",props);
        // 配置KafkaBolt生成的topic
        conf.put("topic", "TestKafkaBoltTopic");
        spoutConfig.scheme = new SchemeAsMultiScheme(new MessageScheme());
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("msgKafkaSpout", new KafkaSpout(spoutConfig));
        builder.setBolt("msgSentenceBolt", new TopicMsgBolt()).shuffleGrouping("msgKafkaSpout");
        builder.setBolt("msgKafkaBolt", new KafkaBolt<String, Integer>().withProducerProperties(props)).shuffleGrouping("msgSentenceBolt");
        if (args.length == 0) {
            String topologyName = "kafkaTopicTopology";
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(topologyName, conf, builder.createTopology());
            Utils.sleep(100000);
            cluster.killTopology(topologyName);
            cluster.shutdown();
        } else {
            conf.setNumWorkers(1);
            StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
        }
    }
}
