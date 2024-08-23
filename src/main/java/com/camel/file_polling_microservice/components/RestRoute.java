package com.camel.file_polling_microservice.components;

import com.camel.file_polling_microservice.dto.NameAddress;
import com.camel.file_polling_microservice.processor.InboundMessageProcessor;
import lombok.Builder;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class RestRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        restConfiguration()
                .component("jetty")
                .host("localhost")
                .port(8080)
                .bindingMode(RestBindingMode.json)
                .enableCORS(true);

        rest("masterclass")
                .produces("application/json")
                .post("nameAddress").type(NameAddress.class)
                .routeId("new-rest-route-id")
                .to("direct:endpoint");

        from("direct:endpoint")
                .log(LoggingLevel.INFO, "${body}")
//                .process(new InboundMessageProcessor())
//                .log(LoggingLevel.INFO, "Transformed body: ${body}")
//                .convertBodyTo(String.class)
//                .to("file:src/data/output?fileName=outputFile.csv&fileExist=append&appendChars=\\n");
                .to("jpa:"+NameAddress.class.getName());

    }
}
