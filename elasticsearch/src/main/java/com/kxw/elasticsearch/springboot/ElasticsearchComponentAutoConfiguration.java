package com.kxw.elasticsearch.springboot;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by kingsonwu on 18/1/5.
 */
@Configuration
@ComponentScan("com.kxw.elasticsearch")
public class ElasticsearchComponentAutoConfiguration {

    /**
     * https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/transport-client.html
     * @return
     * @throws UnknownHostException
     */
    @Bean
    Client elasticsearchClient() throws UnknownHostException {
            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
                //.addTransportAddress(new TransportAddress(InetAddress.getByName("host1"), 9300))
                .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));

        /*Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            client.close();
        }));*/

        //使用ContextClosedHandler

        return client;
    }

}
