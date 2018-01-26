package com.kxw.storm.wordcount.bolts;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

/**
 * WordCounter，负责为单词计数。这个拓扑结束时（cleanup() 方法被调用时），我们将显示每个单词的数量。
 */
public class WordCounter implements IRichBolt {
    Integer id;
    String name;
    Map<String, Integer> counters;
    private OutputCollector collector;

    /**
     * 这个spout结束时（集群关闭的时候），我们会显示单词数量
     *
     * 这只是一个例子，但是通常情况下，当拓扑关闭时，你应当使用 cleanup() 方法关闭活动的连接和其它资源。
     */
    @Override
    public void cleanup() {
        System.out.println("-- 单词数 【" + name + "-" + id + "】 --");
        for (Map.Entry<String, Integer> entry : counters.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    /**
     * 为每个单词计数
     */
    @Override
    public void execute(Tuple input) {
        String str = input.getString(0);
        /**
         * 如果单词尚不存在于map，我们就创建一个，如果已在，我们就为它加1
         */
        if (!counters.containsKey(str)) {
            counters.put(str, 1);
        } else {
            Integer c = counters.get(str) + 1;
            counters.put(str, c);
        }
        //对元组作为应答
        collector.ack(input);
    }

    /**
     * 初始化
     */
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.counters = new HashMap<>();
        this.collector = collector;
        this.name = context.getThisComponentId();
        this.id = context.getThisTaskId();
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {}

    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
