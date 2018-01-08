package com.kxw.elasticsearch.service;

import java.io.IOException;
import java.util.Date;

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by kingsonwu on 18/1/8.
 * http://blog.csdn.net/ljc2008110/article/details/48652937
 *
 * Elasticsearch 常用的java操作 : http://study121007.iteye.com/blog/2296556
 *
 * Java API docs: https://www.elastic.co/guide/index.html
 */
@Service
public class ElasticsearchCRUDExampleService {

    @Autowired
    private Client client;

    /**
     * 定义索引字段属性
     * Mapping,就是对索引库中索引的字段名及其数据类型进行定义，类似于关系数据库中表建立时要定义字段名及其数据类型那样，
     * ]不过es的mapping比数据库灵活很多，它可以动态添加字段。一般不需要要指定mapping都可以，
     * 因为es会自动根据数据格式定义它的类型，如果你需要对某些字段添加特殊属性（如：定义使用其它分词器、是否分词、是否存储等），就必须手动添加mapping。
     * 有两种添加mapping的方法，一种是定义在配置文件中，一种是运行时手动提交mapping，两种选一种就行了。
     */
    public void putMapping() throws IOException {

        //先创建空索引库
        //client.admin().indices().prepareCreate("product_index").execute().actionGet();

        /** 添加product_index索引库的mapping的json格式请求。其中product_index为索引类型，properties下面的为索引里面的字段，
         *  type为数据类型，store为是否存储，"index":"not_analyzed"为不对该字段进行分词。
         */
        XContentBuilder mapping = jsonBuilder()
            .startObject()
            .startObject("properties")
            .startObject("title").field("type", "text").field("store", "true").endObject()
            .startObject("description").field("type", "text").field("index", "false").endObject()
            .startObject("price").field("type", "double").endObject()
            .startObject("onSale").field("type", "boolean").endObject()
            .startObject("type").field("type", "integer").endObject()
            .startObject("createDate").field("type", "date").endObject()
            .endObject()
            .endObject();
        PutMappingRequest mappingRequest = Requests.putMappingRequest("product_index").type("product_type").source(
            mapping);
        // 同一个索引里可以有不同的索引类型

        client.admin().indices().putMapping(mappingRequest).actionGet();

    }

    public void savaIndex() throws IOException {
        /** 其中product_index为索引库名，一个es集群中可以有多个索引库。product_type为索引类型，是用来区分同索引库下不同类型的数据的，一个索引库下可以有多个索引类型。
         */
        XContentBuilder doc = jsonBuilder()
            .startObject()
            .field("title", "this is a title!")
            .field("description", "descript what?")
            .field("price", 100)
            .field("onSale", true)
            .field("type", 1)
            .field("createDate", new Date())
            .endObject();
        client.prepareIndex("product_index", "product_type").setSource(doc).execute().actionGet();

        /*String json = "";
        client.prepareIndex("product_index", "product_type").setSource(json).execute().actionGet();*/
    }

    /**
     * 删除api允许从特定索引通过id删除json文档。有两种方法，一是通过id删除，二是通过一个Query查询条件删除，符合这些条件的数据都会被删除。
     */
    public void deleteIndex(){

        // 1.通过id删除
        DeleteResponse response = client.prepareDelete("twitter", "tweet", "1")
            .execute()
            .actionGet();
        // 2. 通过Query删除
        /*QueryBuilder query = QueryBuilders.fieldQuery("title", "query");
        client.prepareDeleteByQuery("product_index").setQuery(query).execute().actionGet();
        */

        /**
         * 设置线程
         当删除api在同一个节点上执行时（在一个分片中执行一个api会分配到同一个服务器上），删除api允许执行前设置线程模式（operationThreaded选项），
         operationThreaded这个选项是使这个操作在另外一个线程中执行，或在一个正在请求的线程（假设这个api仍是异步的）中执行。
         默认的话operationThreaded会设置成true，这意味着这个操作将在一个不同的线程中执行。
         *
         */

    }

    /**
     * elasticsearch支持批量添加或删除索引文档，java api里面就是通过构造BulkRequestBuilder，
     * 然后把批量的index/delete请求添加到BulkRequestBuilder里面，执行BulkRequestBuilder。
     */
    public void batchSaveIndex() throws IOException {
        BulkRequestBuilder bulkRequest = client.prepareBulk();

        bulkRequest.add(client.prepareIndex("twitter", "tweet", "1")
                .setSource(jsonBuilder()
                        .startObject()
                        .field("user", "kimchy")
                        .field("postDate", new Date())
                        .field("message", "trying out Elastic Search")
                        .endObject()
                )
        );

        bulkRequest.add(client.prepareIndex("twitter", "tweet", "2")
                .setSource(jsonBuilder()
                        .startObject()
                        .field("user", "kimchy")
                        .field("postDate", new Date())
                        .field("message", "another post")
                        .endObject()
                )
        );

        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            //处理错误
        }
    }

    public String query() {
        /**
         * elasticsearch的查询是通过执行json格式的查询条件，在java api中就是构造QueryBuilder对象，
         * elasticsearch完全支持queryDSL风格的查询方式，QueryBuilder的构建类是QueryBuilders，filter的构建类是FilterBuilders。
         */

       /* SearchResponse response = client.prepareSearch("index1", "index2")
            .setTypes("type1", "type2")
            .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
            .setQuery(QueryBuilders.termQuery("multi", "test"))                 // Query
            .setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
            .setFrom(0).setSize(60).setExplain(true)
            .get();*/

        // MatchAll on the whole cluster with all default options
        //SearchResponse response = client.prepareSearch().get();

        SearchResponse response = client.prepareSearch("product_index")
            .setTypes("product_type")
            //.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
            //.setQuery(QueryBuilders.termQuery("multi", "test"))                 // Query
            //.setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
            .setFrom(0).setSize(60).setExplain(false)
            .get();


        return response.toString();
    }
}
