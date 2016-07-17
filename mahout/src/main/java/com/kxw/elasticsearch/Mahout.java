package com.kxw.elasticsearch;

import java.io.File;
import java.util.List;

import org.apache.mahout.cf.taste.impl.model.file.*;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 * <a href='http://blog.csdn.net/sky_money/article/details/7824507'>@link</a>
 * Mahout的主页Apache Mahout，正如其主要介绍的，这是一个Scalable Machine Learning库，而且基于Map/Reduce，可运行在Hadoop集群上。
 * 事实上它提供的库就有两种，一种是单机版的，独立运行在PC上的，还有一个就是分布式版的，运行在Hadoop上.
 * 至于 data.csv里的数据，就是模拟的用户打分数据
 */
public class Mahout {
    private Mahout() {
    }

    public static void main(String[] args) throws Exception {
        String path = Thread.currentThread().getContextClassLoader().getResource("data.csv").getPath();

        DataModel model = new FileDataModel(new File(path));
        //用户相似度，使用基于皮尔逊相关系数计算相似度
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        //选择邻居用户，使用NearestNUserNeighborhood实现UserNeighborhood接口，选择邻近的2个用户
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, model);
        Recommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
        //给用户1推荐2个物品
        List<RecommendedItem> recommendations = recommender.recommend(1, 2);
        for (RecommendedItem recommendation : recommendations) {
            System.out.println(recommendation);
        }
        System.out.println(14.5/4);
    }
}
