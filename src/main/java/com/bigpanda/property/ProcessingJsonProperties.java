package com.bigpanda.property;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;


@Configuration
public class ProcessingJsonProperties {

    // collect stats
    private Map<String, Integer> eventTypes = new ConcurrentHashMap<>();
    private Map<String, Integer> dataMap = new ConcurrentHashMap<>();
    private String event_type;


    public Map<String, Integer> getEventTypesStats() {
        return eventTypes;
    }

    public Map<String, Integer> getDataStats() {
        return dataMap;
    }

    @Bean
    String getCommand(@Value("${command}") String command) {
        return command;
    }


    @Bean
    public Consumer<String> processingJsons(@Qualifier("isJSONValid")
                                                    Predicate<String> isJSONValid,
                                            @Qualifier("getJsonArray")
                                                    Function<String, JSONArray> getJsonArray,
                                            @Qualifier("classify")
                                                    Consumer<JSONArray> classify) {
        return jsons -> Stream.of(jsons.split("\n", -1))
                .filter(isJSONValid())
                .map(getJsonArray)
                .forEach(classify::accept);
    }


    @Bean
    public Predicate<String> isJSONValid() {
        return jsonAsString -> {
            try {
                // TODO: Delete at the end
                System.out.println(jsonAsString);
                new JSONObject(jsonAsString);
            } catch (JSONException ex) {
                try {
                    new JSONArray(jsonAsString);
                } catch (JSONException ex1) {
                    return false;
                }
            }
            return true;
        };
    }

    @Bean
    public Function<String, JSONArray> getJsonArray() {
        return jsonAsString -> {
            try {
                JSONObject jsonObject = new JSONObject(jsonAsString);
                JSONArray jsonArray = new JSONArray();
                return jsonArray.put(jsonObject);
            } catch (JSONException ex) {
                {
                    return new JSONArray(jsonAsString);
                }

            }
        };
    }


    @Bean
    public Consumer<JSONArray> classify(@Qualifier("updateEventTypeCounterByType")
                                                Consumer<String> updateEventTypeCounterByType,
                                        @Qualifier("updateDataWordsCounter")
                                                Consumer<String> updateDataWordsCounter) {
        return jsonArray -> jsonArray
                .forEach(jsonObjectAsObject -> {
                            JSONObject jsonObject = (JSONObject) jsonObjectAsObject;
                            jsonObject.toMap()
                                    .forEach((key, value) ->
                                    {
                                        event_type = "event_type";
                                        if (key.equals(event_type)) {
                                            updateEventTypeCounterByType.accept(((String) value));
                                        }
                                        if (key.equals("data")) {
                                            updateDataWordsCounter.accept(((String) value));
                                        }
                                    });
                        }
                );
    }


    @Bean
    public Consumer<String> updateEventTypeCounterByType() {
        return eventTypeName -> {
            if (eventTypes.containsKey(eventTypeName)) {
                eventTypes.put(eventTypeName, eventTypes.get(eventTypeName) + 1);
            } else {
                eventTypes.put(eventTypeName, 1);
            }
        };
    }

    @Bean
    public Consumer<String> updateDataWordsCounter() {
        return data -> {
            Stream.of(data.split(" ", -1))
                    .forEach(wordAsKey -> {
                        if (dataMap.containsKey(wordAsKey)) {
                            dataMap.put(wordAsKey, dataMap.get(wordAsKey) + 1);
                        } else {
                            dataMap.put(wordAsKey, 1);
                        }
                    });
        };
    }

}




