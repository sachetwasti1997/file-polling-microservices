package com.camel.file_polling_microservice.components;

import com.camel.file_polling_microservice.dto.NameAddress;
import com.camel.file_polling_microservice.processor.InboundMessageProcessor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.beanio.BeanIODataFormat;
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
    private final BeanIODataFormat beanIODataFormat = new BeanIODataFormat("InboundMessageBeanIOMapping.xml",
            "inputMessageStream");

    @Override
    public void configure() throws Exception {
        from("file:src/data/input?fileName=inputFile.csv") //<routeType>:{location}<optional>?{parameters}=<value>
                .routeId("legacy-file-move-route-id")
                .split(body().tokenize("\n", 1, true))
                .unmarshal(beanIODataFormat)
                // this is used to execute java code in camel DSL
                .process(new InboundMessageProcessor())
                .log(LoggingLevel.INFO, "Transformed Object: ${body}")
                .convertBodyTo(String.class)
                .to("file:src/data/output?fileName=outputFile.csv&fileExist=append&appendChars=\\n") //<routeType>:{location}<optional>?{parameters}=<value>
                .end(); //this is used to mark that the split has ended here
    }
}
