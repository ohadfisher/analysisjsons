package com.bigpanda;


import com.bigpanda.client.Client;
import com.bigpanda.Controller.ControllerStats;
import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class TransformJsonsApplication {

    @Autowired
    private Client client;
    @Autowired
    private ControllerStats controllerStats;


    public static void main(String[] args) {
        SpringApplication.run(TransformJsonsApplication.class,args);
    }

    @PostConstruct
    public void deployVerticle() {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(client);
        vertx.deployVerticle(controllerStats);
    }
}
