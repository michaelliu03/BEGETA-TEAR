/*
package com.kxw.elasticsearch.service;

import java.util.ArrayList;
import java.util.List;

import com.kxw.elasticsearch.entity.DataBean;
import com.kxw.elasticsearch.dao.DataBeanRepository;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;

*/
/**
 * Created by kingsonwu on 18/1/5.
 * https://mp.weixin.qq.com/s/IS3LH90lziCx9phxnkthFw
 *//*

public class ElasticsearchSpringDataService {

    @Autowired
    DataBeanRepository repository;

    */
/**
     * 采用BoolQueryBuilder构建查询条件，也即可基于DSL模块查询数据，还可以采用Criteria查询。
     *//*

    public List<DataBean> query(String name, String num, String type) {

        //采用过滤器的形式，提高查询效率

        BoolQueryBuilder builder = QueryBuilders.boolQuery();

        builder.must(QueryBuilders.termQuery("name", name)).must(
            QueryBuilders.termQuery("num", num));

        Iterable<DataBean> lists = repository.search(builder);

        List<DataBean> datas = new ArrayList<>();

        for (DataBean dataBean : lists) {

            datas.add(dataBean);

            */
/*logger.info(
                "---------------------->>>Request result = 【"
                    + dataBean +
                    "】"
            );*//*


        }

        return datas;

    }
}
*/
