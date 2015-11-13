package com.hadoop.test;

/**
 * 简单的单词计数器
 */

/**
 *{<a href='http://blog.csdn.net/neofung/article/details/8171312'>@link</a>}
 * @author Neo neosfung_gmail_com
 * @version 1.0 2012-11-11
 */

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

public class WordCount {

    public static class Map extends MapReduceBase implements
            Mapper<LongWritable, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private final Text word = new Text();

        /**
         * map函数继承自MapReduceBase, 并且实现了Mapper接口, 此接口是一个泛型类型,它有4种形式的参数,
         * 分别用来指定map的输入key类型, 输入value值类型, 输出key值类型和输出value值类型.
         * 在本例中，输入使用的是TextInputFormat, 它的输出key值是LongWritable类型, 输出value值是Text类型.
         * 根据本例, 需要输出的是<Text, IntWritable>的形式, 所以输出的key值类型是Text,
         * 输出的value类型是IntWritable.
         */
        @Override
        public void map(LongWritable key, Text value,
                        OutputCollector<Text, IntWritable> output, Reporter reporter)
                throws IOException {
            String line = value.toString();
            StringTokenizer tokenizer = new StringTokenizer(line);
            while (tokenizer.hasMoreTokens()) {
                word.set(tokenizer.nextToken());
                output.collect(word, one);
            }

        }
    }

    public static class Reduce extends MapReduceBase implements
            Reducer<Text, IntWritable, Text, IntWritable> {

        /**
         * reduce函数的输入以map的输出作对应, 因此reduce的输入类型是<Text, IntWritable>.
         */
        @Override
        public void reduce(Text key, Iterator<IntWritable> values,
                           OutputCollector<Text, IntWritable> output, Reporter reporter)
                throws IOException {
            int sum = 0;
            while (values.hasNext()) {
                sum += values.next().get();

            }
            output.collect(key, new IntWritable(sum));

        }
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        JobConf conf = new JobConf(WordCount.class);
        conf.setJobName("wordcount");

        // 设定输出的key和value类型
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);

        // 设定各个作业的类
        conf.setMapperClass(Map.class);
        conf.setCombinerClass(Reduce.class);
        conf.setReducerClass(Reduce.class);

        // 设定输入输出的格式
        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        //FileInputFormat.setInputPaths(conf, new Path(args[0]));
        //FileOutputFormat.setOutputPath(conf, new Path(args[1]));

        FileInputFormat.setInputPaths(conf, new Path("./kxw.txt"));
        FileOutputFormat.setOutputPath(conf, new Path("d:/output"));

        JobClient.runJob(conf);
    }
}
