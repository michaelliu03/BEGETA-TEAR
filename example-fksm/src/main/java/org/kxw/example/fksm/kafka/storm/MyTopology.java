package org.kxw.example.fksm.kafka.storm;

import java.util.Arrays;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.Utils;


/**
 * <a href='http://blog.csdn.net/wangyangzhizhou/article/details/52440862'>@link</a>
 * 创建拓扑MyTopology，先配置好KafkaSpout的配置SpoutConfig，其中zk的地址端口和根节点，将id为KAFKA_SPOUT_ID的spout通过shuffleGrouping关联到jsonBolt对象。
 *
 * 本地测试时直接不带运行参数运行即可，放到集群是需带拓扑名称作为参数。(本地模式)
 * 另外需要注意的是：KafkaSpout默认从上次运行停止时的位置开始继续消费，即不会从头开始消费一遍，因为KafkaSpout默认每2秒钟会提交一次kafka的offset位置到zk上，如果要每次运行都从头开始消费可以通过配置实现。
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
