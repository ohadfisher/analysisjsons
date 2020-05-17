package com.bigpanda.property;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Consumer;

import static org.junit.Assert.*;

public class ProcessingJsonPropertiesTest {


    private ProcessingJsonProperties properties;
    private Consumer<String> processingJsons;

    private  String validJsonAsString;
    private String  not_valid_JsonAsString;

    @Before
    public void setUp() throws Exception {
        properties = new ProcessingJsonProperties();
        processingJsons = properties.processingJsons(
                properties.isJSONValid(),properties.getJsonArray()
                ,properties.classify(properties.updateEventTypeCounterByType(),properties.updateDataWordsCounter()));
         validJsonAsString = "{ \"event_type\": \"baz\", \"data\": \"ipsum\", \"timestamp\": 1589710952 }";
         not_valid_JsonAsString = "{ \"�v���f\u0016O\u0005�";

    }

    @Test
    public void oneJsonNotValid() {
        String input =not_valid_JsonAsString;
        processingJsons.accept(input);
        Assert.assertTrue(properties.getEventTypesStats().isEmpty());
        Assert.assertTrue(properties.getDataStats().isEmpty());
    }
    @Test
    public void oneJsonValid() {
        String input =validJsonAsString;
        processingJsons.accept(input);
        Assert.assertFalse(properties.getEventTypesStats().isEmpty());
        Assert.assertFalse(properties.getDataStats().isEmpty());
        Assert.assertEquals(1,getNumberOfEventTypeByName("baz").intValue());
        Assert.assertEquals(1,getDataStatsByName("ipsum").intValue());

    }

    @Test
    public void oneJsonValidAndOneNot_insertToMaps() {
        String input = validJsonAsString+"\n"+not_valid_JsonAsString;
        processingJsons.accept(input);
        Assert.assertFalse(properties.getEventTypesStats().isEmpty());
        Assert.assertFalse(properties.getDataStats().isEmpty());
        Assert.assertEquals(1,getNumberOfEventTypeByName("baz").intValue());
        Assert.assertEquals(1,getDataStatsByName("ipsum").intValue());

    }
    @Test
    public void insertToMapsCorrect() {
        String input = validJsonAsString+"\n"+not_valid_JsonAsString;
        processingJsons.accept(input);
        Assert.assertEquals(1,getNumberOfEventTypeByName("baz").intValue());
        Assert.assertEquals(1,getDataStatsByName("ipsum").intValue());

    }
  @Test
    public void insertToMapsCorrect_doubleInput() {
        String input = validJsonAsString+"\n"+not_valid_JsonAsString;
        processingJsons.accept(input+"\n"+input);
        Assert.assertEquals(2,getNumberOfEventTypeByName("baz").intValue());
        Assert.assertEquals(2,getDataStatsByName("ipsum").intValue());

    }

    @Test
    public void oneJsonValidAndOneNot_2() {
        String input = validJsonAsString+"\n"+not_valid_JsonAsString;
        processingJsons.accept(input);
        Assert.assertNull(getNumberOfEventTypeByName("b"));
    }

    private Integer getNumberOfEventTypeByName(String key){
        return properties.getEventTypesStats().get(key);
    }
    private Integer getDataStatsByName(String key){
        return properties.getDataStats().get(key);
    }
}