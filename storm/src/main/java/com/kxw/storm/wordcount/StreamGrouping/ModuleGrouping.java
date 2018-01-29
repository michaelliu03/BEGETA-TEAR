package com.kxw.storm.wordcount.StreamGrouping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import backtype.storm.generated.GlobalStreamId;
import backtype.storm.grouping.CustomStreamGrouping;
import backtype.storm.task.WorkerTopologyContext;

/**
 * 通过实现 backtype.storm.grouping.CustormStreamGrouping 接口创建自定义数据流组，让你自己决定哪些 bolt 接收哪些元组。
 * 让我们修改单词计数器示例，使首字母相同的单词由同一个 bolt 接收。
 * 这是一个 CustomStreamGrouping 的简单实现，在这里我们采用单词首字母字符的整数值与任务数的余数，决定接收元组的 bolt。
 */
public class ModuleGrouping implements CustomStreamGrouping, Serializable {

    int numTasks = 0;

    @Override
    public void prepare(WorkerTopologyContext context, GlobalStreamId stream, List<Integer> targetTasks) {
        numTasks = targetTasks.size();
    }

    @Override
    public List<Integer> chooseTasks(int taskId, List<Object> values) {
        List<Integer> boltIds = new ArrayList<>();
        if (values.size() > 0) {
            String str = values.get(0).toString();
            if (str.isEmpty()) {
                boltIds.add(0);
            } else {
                boltIds.add(str.charAt(0) % numTasks);
            }
        }
        return boltIds;
    }
}