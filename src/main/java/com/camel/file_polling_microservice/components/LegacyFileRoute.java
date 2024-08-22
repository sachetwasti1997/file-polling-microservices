package com.camel.file_polling_microservice.components;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LegacyFileRoute extends RouteBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(LegacyFileRoute.class);

    @Override
    public void configure() throws Exception {
        from("file:src/data/input?fileName=inputFile.txt") //<routeType>:{location}<optional>?{parameters}=<value>
                .routeId("legacy-file-move-route-id")
                // this is used to execute java code in camel DSL
                .process(exchange -> {
                    String fileData = exchange.getIn().getBody(String.class);
                    LOGGER.info("The file data read is <"+fileData+">.");
                })
                .to("file:src/data/output?fileName=outputFile.txt"); //<routeType>:{location}<optional>?{parameters}=<value>
    }
}
