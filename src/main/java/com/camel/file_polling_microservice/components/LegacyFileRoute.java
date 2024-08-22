package com.camel.file_polling_microservice.components;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/*
An Exchange is the message container holding
the information during the entire routing of a
Message received by a Consumer . During processing
down the Processor chain, the Exchange provides access
to the current (not the original) request and response Message messages.
 */

@Component
public class LegacyFileRoute extends RouteBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(LegacyFileRoute.class);

    @Override
    public void configure() throws Exception {
        from("file:src/data/input?fileName=inputFile.csv") //<routeType>:{location}<optional>?{parameters}=<value>
                .routeId("legacy-file-move-route-id")
                .split(body().tokenize("\n"))
                // this is used to execute java code in camel DSL
                .process(exchange -> {
                    String fileData = exchange.getIn().getBody(String.class);
                    LOGGER.info("The file data read is <"+fileData+">.");
                })
                .to("file:src/data/output?fileName=outputFile.csv&fileExist=append&appendChars=\\n") //<routeType>:{location}<optional>?{parameters}=<value>
                .end(); //this is used to mark that the split has ended here
    }
}
