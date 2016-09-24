package org.kxw.example.fksm.kafka.storm;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JsonBolt extends BaseRichBolt {
    private static final Logger LOG = LoggerFactory
            .getLogger(JsonBolt.class);

    private Fields fields;
    private OutputCollector collector;

    public JsonBolt() {
        this.fields = new Fields("hostIp", "instanceName", "className",
                "methodName", "createTime", "callTime", "errorCode");
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context,
                        OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple tuple) {
        String spanDataJson = tuple.getString(0);
        LOG.info("source data:{}", spanDataJson);

        //若读取的不是json会报错
        //Map<String, Object> map = (Map<String, Object>) JSONValue.parse(spanDataJson);
        Values values = new Values();
        for (int i = 0, size = this.fields.size(); i < size; i++) {
            //values.add(map.get(this.fields.get(i)));
            values.add(spanDataJson + "_" + i);
        }

        this.collector.emit(tuple, values);
        this.collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(this.fields);
    }
}