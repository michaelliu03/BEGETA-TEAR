package org.kxw.example.fksm.kafka.consumer;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <a href='http://www.yanjiankang.cn/kafka-install-config-storm-spout/'>@link</a>
 */
public class ConsumerGroupExample {
    private final ConsumerConnector consumer;
    private final String topic;
    private ExecutorService executor;

    public ConsumerGroupExample(String a_zookeeper, String a_groupId, String a_topic) {
        // 创建consumer对象， a_zookeeper是zookeeper地址，
        // a_groupId是消费者读取时的group id，可以随便设
        // a_topic是读取的topic名称
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(
                createConsumerConfig(a_zookeeper, a_groupId));
        this.topic = a_topic;
    }

    // 关闭consumer接口
    public void shutdown() {
        if (consumer != null) consumer.shutdown();
        if (executor != null) executor.shutdown();
        try {
            if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                System.out.println("Timed out waiting for consumer threads to shut down, exiting uncleanly");
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted during shutdown, exiting uncleanly");
        }
    }

    // 启动消费者， a_numThreads指定读取线程数，因为topic可以有多个分区，可以多线程读取
    public void run(int a_numThreads) {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(a_numThreads));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);

        // now launch all the threads
        executor = Executors.newFixedThreadPool(a_numThreads);

        // now create an object to consume the messages
        int threadNumber = 0;
        for (final KafkaStream stream : streams) {
            // ConsumerThread真正读取消息是在 ConsumerThread中
            executor.submit(new ConsumerThread(stream, threadNumber));
            threadNumber++;
        }
    }

    // 初始化consumer 的相关属性
    private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId) {
        Properties props = new Properties();
        props.put("zookeeper.connect", a_zookeeper);
        props.put("group.id", a_groupId);
        props.put("zookeeper.session.timeout.ms", "400");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");

        return new ConsumerConfig(props);
    }

    public static void main(String[] args) {

        // 运行之后,生产者端输入数据即可接收
        args = new String[4];
        args[0] = "localhost:2181";
        args[1] = "simpleKafkaConsumerTest";
        args[2] = "test";
        args[3] = "2";

        String zooKeeper = args[0];
        String groupId = args[1];
        String topic = args[2];
        int threads = Integer.parseInt(args[3]);

        ConsumerGroupExample example = new ConsumerGroupExample(zooKeeper, groupId, topic);
        example.run(threads);

        // sleep时间用于consumer将offset信息更新到zookeeper中，因为offset信息不是同步更新的
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ie) {

        }
        // example.shutdown();
    }
}
