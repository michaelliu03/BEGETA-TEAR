package org.kxw.example.fksm.hello2;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SentenceBolt extends BaseBasicBolt {
    private static final Logger logger = LoggerFactory.getLogger(SentenceBolt.class);

    @Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        String msg = tuple.getStringByField("msg");
        logger.info("get one message is {}", msg);
        basicOutputCollector.emit(new Values(msg));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("sentence"));
    }
}