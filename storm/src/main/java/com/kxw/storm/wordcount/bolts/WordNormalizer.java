package com.kxw.storm.wordcount.bolts;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * bolt最重要的方法是void execute(Tuple input)，每次接收到元组时都会被调用一次，还会再发布若干个元组。
 * 只要必要，bolt 或 spout 会发布若干元组。当调用 nextTuple 或 execute 方法时，它们可能会发布0个、1个或许多个元组。
 */
public class WordNormalizer implements IRichBolt {
    private OutputCollector collector;

    public void cleanup() {}

    /**
     * *bolt*从单词文件接收到文本行，并标准化它。
     * 文本行会全部转化成小写，并切分它，从中得到所有单词。
     *
     * 在一次 execute 调用中发布多个元组。如果这个方法在一次调用中接收到句子 “This is the Storm book”，它将会发布五个元组。
     */
    public void execute(Tuple input) {
        String sentence = input.getString(0);
        String[] words = sentence.split(" ");
        for (String word : words) {
            word = word.trim();
            if (!word.isEmpty()) {
                word = word.toLowerCase();
                //发布这个单词
               /* List<Tuple> a = new ArrayList();
                a.add(input);
                collector.emit(a, new Values(word));*/
                collector.emit(new Values(word));
            }
        }
        //对元组做出应答
        collector.ack(input);
    }

    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    /**
     * 这个*bolt*只会发布“word”域
     */
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));
    }

    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}