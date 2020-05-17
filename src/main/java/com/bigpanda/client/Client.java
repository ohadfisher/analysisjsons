package com.bigpanda.client;

import io.vertx.core.AbstractVerticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.wisdom.framework.vertx.AsyncInputStream;

import java.util.function.Consumer;


@Component
public class Client extends AbstractVerticle {

    private final String command;
    private final Consumer<String> processingJsons;

    @Autowired
    public Client(@Qualifier("getCommand") String command,
                  @Qualifier("processingJsons") Consumer<String> processingJsons) {
        this.command = command;
        this.processingJsons = processingJsons;
    }


    @Override
    public void start() throws Exception {

        Runtime r = Runtime.getRuntime();
        Process p = r.exec(command);
        AsyncInputStream asyncInputStream = new AsyncInputStream(vertx, vertx.nettyEventLoopGroup(), p.getInputStream());
        asyncInputStream.handler(buffer ->
                        processingJsons.accept(buffer.getString(0, buffer.length() - 1))
        );
    }

}
