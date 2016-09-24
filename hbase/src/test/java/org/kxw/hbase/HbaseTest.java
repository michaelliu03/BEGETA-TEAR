package org.kxw.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;


/**
 * <a href ='http://blog.csdn.net/gdmzlhj1/article/details/50783182'>@link</a>
 * 电信详单，查询某个号码，某个月的通话清单，包括通话号码，通话类型，通话时间等信息，如何设计？
 * 表名：t_cdr
 * rowkey设置：号码+时间
 * 一个列族：cf1
 * 字段：dest（对方号码），type（通话类型），time（通话时间）
 */
public class HbaseTest {
    @Test
    public void test1() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "node1,node2,node3");
        HBaseAdmin admin = new HBaseAdmin(conf);
        String table = "t_cdr";
        if (admin.isTableAvailable(table)) {
            admin.disableTable(table);
            admin.deleteTable(table);
        }
        HTableDescriptor t = new HTableDescriptor(table.getBytes());
        HColumnDescriptor cf1 = new HColumnDescriptor("cf1".getBytes());
        //cf1.setMaxVersions(8);
        //cf1.setMinVersions(0);
        t.addFamily(cf1);
        admin.createTable(t);

        admin.close();
    }

    //hbase(main):007:0> list
    //TABLE
    //t_cdr
    @Test
    public void test2() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "node1,node2,node3");
        HTable table = new HTable(conf, "t_cdr");
        String rowkey = "18933945820_" + System.currentTimeMillis();
        Put put = new Put(rowkey.getBytes());
        put.add("cf1".getBytes(), "dest".getBytes(), "123456789".getBytes());
        put.add("cf1".getBytes(), "type".getBytes(), "1".getBytes());
        put.add("cf1".getBytes(), "time".getBytes(), "2015-11-20 13:27:30".getBytes());
        table.put(put);
        table.close();
    }

    //hbase(main):013:0> scan 't_cdr'
    //ROW                                        COLUMN+CELL
    // 18933945820_1447997665063                 column=cf1:dest, timestamp=1447997661766, value=123456789
    // 18933945820_1447997665063                 column=cf1:time, timestamp=1447997661766, value=2015-11-20 13:27:30
    // 18933945820_1447997665063                 column=cf1:type, timestamp=1447997661766, value=1
    //
    @Test
    public void test3() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "node1,node2,node3");
        HTable table = new HTable(conf, "t_cdr");
        //Get get=new Get("18933945820_1447997665063".getBytes());
        //Result res=table.get(get);
        //Cell c1=res.getColumnLatestCell("cf1".getBytes(), "dest".getBytes());
        //System.out.println(new String(c1.getValue()));
        //Cell c2=res.getColumnLatestCell("cf1".getBytes(), "time".getBytes());
        //System.out.println(new String(c2.getValue()));
        //Cell c3=res.getColumnLatestCell("cf1".getBytes(), "type".getBytes());
        //System.out.println(new String(c3.getValue()));
        Scan scan = new Scan();
        scan.setStartRow("18933945820_1447997665000".getBytes());
        scan.setStopRow("18933945820_1447997665100".getBytes());
        scan.setMaxVersions();
        //指定最多返回的Cell数目。用于防止一行中有过多的数据，导致OutofMemory错误。

        ResultScanner rs = table.getScanner(scan);
        //row:18933945820_1447997665063, family:cf1, qualifier:dest, qualifiervalue:123456789, timestamp:1447997661766.
        //row:18933945820_1447997665063, family:cf1, qualifier:time, qualifiervalue:2015-11-20 13:27:30, timestamp:1447997661766.
        //row:18933945820_1447997665063, family:cf1, qualifier:type, qualifiervalue:1, timestamp:1447997661766.


        for (Result r : rs) {
            for (KeyValue kv : r.raw()) {
                System.out.println(String.format("row:%s, family:%s, qualifier:%s, qualifiervalue:%s, timestamp:%s.",
                        Bytes.toString(kv.getRow()),
                        Bytes.toString(kv.getFamily()),
                        Bytes.toString(kv.getQualifier()),
                        Bytes.toString(kv.getValue()),
                        kv.getTimestamp()));
            }


            rs.close();

        }

        table.close();
    }
}