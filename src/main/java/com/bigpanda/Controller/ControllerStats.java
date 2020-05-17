package com.bigpanda.Controller;

import com.bigpanda.property.ProcessingJsonProperties;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ControllerStats extends AbstractVerticle {

    private final ProcessingJsonProperties processingJsonProperties;


    @Autowired
    public ControllerStats(ProcessingJsonProperties processingJsonProperties) {
        this.processingJsonProperties = processingJsonProperties;
    }


    public void start() {
        HttpServer server = vertx.createHttpServer();

        server.requestHandler(request -> {

            // This handler gets called for each request that arrives on the server
            HttpServerResponse response = request.response();
            response.putHeader("content-type", "application/json");
            response.setChunked(true);
            response.write(getStats()).end();
        });

        server.listen(8080);
    }


    private String getStats() {
        JSONObject jsonEventTypesStats = new JSONObject(processingJsonProperties.getEventTypesStats());
        JSONObject jsonDataStats = new JSONObject(processingJsonProperties.getDataStats());
        JSONArray jsons = new JSONArray();
        jsons.put(jsonDataStats);
        jsons.put(jsonEventTypesStats);

        return jsons.toString();
    }


}
