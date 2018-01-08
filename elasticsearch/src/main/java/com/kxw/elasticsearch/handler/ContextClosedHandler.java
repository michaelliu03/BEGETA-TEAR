package com.kxw.elasticsearch.handler;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * Created by kingsonwu on 18/1/8.
 */
@Component
public class ContextClosedHandler implements ApplicationListener<ContextClosedEvent> {

    @Autowired
    private Client client;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        client.close();
    }
}