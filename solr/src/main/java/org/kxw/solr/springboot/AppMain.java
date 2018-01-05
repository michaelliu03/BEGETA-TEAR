package org.kxw.solr.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by kingsonwu on 18/1/4.
 * Spring-Boot 集成Solr客户端:<a href='https://www.jianshu.com/p/e21fe5f3bd8c'>@link</a>
 */
@SpringBootApplication
@EnableAutoConfiguration
public class AppMain {

    public static void main(String[] args) {
        SpringApplication.run(AppMain.class, args);
    }
}

