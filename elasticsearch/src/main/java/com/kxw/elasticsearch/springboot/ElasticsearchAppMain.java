package com.kxw.elasticsearch.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class ElasticsearchAppMain {

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchAppMain.class, args);
    }
}

