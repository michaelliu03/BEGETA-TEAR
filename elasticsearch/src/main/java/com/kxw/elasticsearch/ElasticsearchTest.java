package com.kxw.elasticsearch;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by kingsonwu on 16/7/17.
 * <a href='http://www.ibm.com/developerworks/cn/java/j-use-elasticsearch-java-apps/'>@link</a>
 */
public class ElasticsearchTest {

    public static void main(String[] args) throws UnknownHostException {
        Client client = TransportClient.builder().build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

        SearchResponse searchResponse = client.prepareSearch("music").setTypes("lyrics").execute().actionGet();
    }
}
