package com.kxw.storm.wordcount.spouts;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

/**
 * WordReader负责从文件按行读取文本，并把文本行提供给第一个 bolt。
 * 一个 spout 发布一个定义域列表。这个架构允许你使用不同的 bolts 从同一个spout 流读取数据，它们的输出也可作为其它 bolts 的定义域，以此类推。
 *
 * 第一个被调用的 spout 方法都是 public void open(Map conf, TopologyContext context, SpoutOutputCollector collector)。它接收如下参数：配置对象，在定义topology 对象是创建；TopologyContext 对象，包含所有拓扑数据；还有SpoutOutputCollector 对象，它能让我们发布交给 bolts 处理的数据。
 * 接下来我们要实现 public void nextTuple()，我们要通过它向 bolts 发布待处理的数据。在这个例子里，这个方法要读取文件并逐行发布数据。
 * Values 是一个 ArrarList 实现，它的元素就是传入构造器的参数。
 * 元组(tuple)是一个具名值列表，它可以是任意 java 对象（只要它是可序列化的）。默认情况，Storm 会序列化字符串、字节数组、ArrayList、HashMap 和 HashSet 等类型。
 */
public class WordReader implements IRichSpout {
    private SpoutOutputCollector collector;
    private FileReader fileReader;
    private boolean completed = false;
    private TopologyContext context;

    public boolean isDistributed() {
        return false;
    }

    @Override
    public void ack(Object msgId) {
        System.out.println("OK:" + msgId);
    }

    @Override
    public void close() {

    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public void fail(Object msgId) {
        System.out.println("FAIL:" + msgId);
    }

    /**
     * 这个方法做的惟一一件事情就是分发文件中的文本行
     * nextTuple() 会在同一个循环内被 ack() 和 fail() 周期性的调用。没有任务时它必须释放对线程的控制，其它方法才有机会得以执行。因此 nextTuple 的第一行就要检查是否已处理完成。
     * 如果完成了，为了降低处理器负载，会在返回前休眠一毫秒。(????直接return不行?)如果任务完成了，文件中的每一行都已被读出并分发了。
     */
    @Override
    public void nextTuple() {
        /**
         * 这个方法会不断的被调用，直到整个文件都读完了，我们将等待并返回。
         */
        if (completed) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //什么也不做
            }
            return;
        }
        String str;
        //创建reader
        BufferedReader reader = new BufferedReader(fileReader);
        try {
            //读所有文本行
            while ((str = reader.readLine()) != null) {
                /**
                 * 按行发布一个新值
                 */
                this.collector.emit(new Values(str), str);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading tuple", e);
        } finally {
            completed = true;
        }
    }

    /**
     * 我们将创建一个文件并维持一个collector对象
     */
    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        try {
            this.context = context;
            this.fileReader = new FileReader(conf.get("wordsFile").toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error reading file [" + conf.get("wordsFile") + "]");
        }
        this.collector = collector;
    }

    /**
     * 声明输入域"word"
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("line"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}