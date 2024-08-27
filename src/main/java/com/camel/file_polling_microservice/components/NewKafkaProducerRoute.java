package com.camel.file_polling_microservice.components;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NewKafkaProducerRoute extends RouteBuilder {
    private final static Logger LOGGER = LoggerFactory.getLogger(NewKafkaProducerRoute.class);
    @Override
    public void configure() throws Exception {

    }
}
