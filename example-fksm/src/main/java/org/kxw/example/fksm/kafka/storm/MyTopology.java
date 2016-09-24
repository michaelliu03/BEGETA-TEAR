package org.kxw.example.fksm.kafka.storm;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.*;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.Utils;

import java.util.Arrays;


/**
 * <a href='http://blog.csdn.net/wangyangzhizhou/article/details/52440862'>@link</a>
 */
public class MyTopology {

    private static final String TOPOLOGY_NAME = "SPAN-DATA-TOPOLOGY";
    private static final String KAFKA_SPOUT_ID = "kafka-stream";
    private static final String JsonProject_BOLT_ID = "jsonProject-bolt";
    private static final String CONSOLE_BOLT_ID = "console-bolt";

    public static void main(String[] args) throws Exception {
        String zks = "localhost:2181";
        String topic = "test";
        String zkRoot = "/kafka-storm";
        BrokerHosts brokerHosts = new ZkHosts(zks);
        SpoutConfig spoutConf = new SpoutConfig(brokerHosts, topic, zkRoot,
                KAFKA_SPOUT_ID);
        spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
        spoutConf.zkServers = Arrays.asList(new String[]{"localhost"});
        spoutConf.zkPort = 2181;
        JsonBolt jsonBolt = new JsonBolt();

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(KAFKA_SPOUT_ID, new KafkaSpout(spoutConf));
        builder.setBolt(JsonProject_BOLT_ID, jsonBolt).shuffleGrouping(
                KAFKA_SPOUT_ID);
        builder.setBolt(CONSOLE_BOLT_ID, new ConsoleBolt()).shuffleGrouping(
                JsonProject_BOLT_ID);

        Config config = new Config();
        config.setNumWorkers(1);
        if (args.length == 0) {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(TOPOLOGY_NAME, config,
                    builder.createTopology());
            Utils.sleep(1000000);
            cluster.killTopology(TOPOLOGY_NAME);
            cluster.shutdown();
        } else {
            StormSubmitter.submitTopology(args[0], config,
                    builder.createTopology());
        }
    }
}
