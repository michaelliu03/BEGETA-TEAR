package org.kxw.example.fksm.hello;

import org.apache.storm.spout.Scheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.List;

public class MessageScheme implements Scheme {
    private static final Logger logger = LoggerFactory.getLogger(MessageScheme.class);


   /* public List<Object> deserialize(byte[] ser) {
        try {
            String msg = new String(ser, "UTF-8");
            logger.info("get one message is {}", msg);
            return new Values(msg);
        } catch (UnsupportedEncodingException ignored) {
            return null;
        }
    }*/


    @Override
    public List<Object> deserialize(ByteBuffer byteBuffer) {
        try {
            String msg = new String(byteBuffer.array(), "UTF-8");
            logger.info("get one message is {}", msg);
            return new Values(msg);
        } catch (UnsupportedEncodingException ignored) {
            return null;
        }
    }


    @Override
    public Fields getOutputFields() {
        return new Fields("msg");
    }
}