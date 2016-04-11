package com.kxw.kafka;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * Test the Kafka Producer
 * @author jcsong2
 * <a href='http://www.linuxidc.com/Linux/2013-11/92754.htm'>@link</a>
 *
 */
public class ProducerTest {
/*    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("zk.connect", "10.199.145.87:2181");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("metadata.broker.list", "10.199.145.87:9092");
        ProducerConfig config = new ProducerConfig(props);
        Producer<String, String> producer = new Producer<>(config);
        for (int i = 0; i < 10; i++)
            producer.send(new KeyedMessage<>("test", "test" + i));
    }*/
}
