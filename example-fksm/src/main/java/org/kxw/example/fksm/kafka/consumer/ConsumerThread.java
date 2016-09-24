package org.kxw.example.fksm.kafka.consumer;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

public class ConsumerThread implements Runnable {
    private KafkaStream m_stream;
    private int m_threadNumber;

    public ConsumerThread(KafkaStream a_stream, int a_threadNumber) {
        m_threadNumber = a_threadNumber;
        m_stream = a_stream;
    }

    public void run() {
        ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
    //此处循环会不断从topic中读取消息，阻塞函数，除非手动shutdown；
        while (it.hasNext())
            System.out.println("Thread " + m_threadNumber + ": " + new String(it.next().message()));
        System.out.println("Shutting down Thread: " + m_threadNumber);
    }
}

