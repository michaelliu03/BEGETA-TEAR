package org.kxw.example.fksm.kafka.producer;

import java.util.concurrent.ExecutionException;


/**
 * <a href='http://www.yanjiankang.cn/kafka-install-config-storm-spout/'>@link</a>
 */
public class SimpleCounter {
    private static DemoProducer producer;
    public static void main(String[] args) throws InterruptedException, ExecutionException {


        args = new String[5];
        args[0] = "localhost:9092";
        args[1] = "test";//需要现在kafka创建这一个topic
        //bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic test --from-beginnin (可查看消费)
        args[2] = "async";
        args[3] = "50";
        args[4] = "10";


        if (args.length == 0) {
            System.out.println("SimpleCounterOldProducer {broker-list} {topic} {type old/new} {type sync/async} {delay (ms)} {count}");

            /**
             * localhost:9092 kafkatest async 50 10
             * 其中参数【1】为kafka地址，参数【2】为topic name， 参数【3】为同步produce还是异步produce，【4】为发送间隔，【5】为发送消息数目。
             */

            return;
        }
        /* get arguments */
        String brokerList = args[0];
        String topic = args[1];
        String sync = args[2];
        int delay = Integer.parseInt(args[3]);
        int count = Integer.parseInt(args[4]);
        producer = new DemoProducer(topic);

        /* start a producer */
        producer.configure(brokerList, sync);
        producer.start();
        long startTime = System.currentTimeMillis();
        System.out.println("Starting...");
        producer.produce("Starting...");

        /* produce the numbers */
        for (int i=0; i < count; i++ ) {
            producer.produce(Integer.toString(i));
            Thread.sleep(delay);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("... and we are done. This took " + (endTime - startTime) + " ms.");
        producer.produce("... and we are done. This took " + (endTime - startTime) + " ms.");

        /* close shop and leave */
        producer.close();
        System.exit(0);
    }
}