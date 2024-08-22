package com.camel.file_polling_microservice.components;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class LegacyFileRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file:src/data/input?fileName=inputFile.txt") //<routeType>:{location}<optional>?{parameters}=<value>
                .routeId("legacy-file-move-route-id")
                .to("file:src/data/output?fileName=outputFile.txt"); //<routeType>:{location}<optional>?{parameters}=<value>
    }
}
