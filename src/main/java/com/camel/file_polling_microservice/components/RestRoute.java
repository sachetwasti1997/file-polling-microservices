package com.camel.file_polling_microservice.components;

import com.camel.file_polling_microservice.dto.NameAddress;
import com.camel.file_polling_microservice.processor.InboundMessageProcessor;
import lombok.Builder;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class RestRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        onException(Exception.class)
                .routeId("exception-handler")
                .log("Exception Occurred: ${body}");

        restConfiguration()
                .component("jetty")
                .bindingMode(RestBindingMode.json)
                .enableCORS(true);

        rest("masterclass")
                .produces("application/json")
                .post("nameAddress").type(NameAddress.class)
                .routeId("new-rest-route-id")
                .to("direct:endpoint");

        from("direct:endpoint")
                .log(LoggingLevel.INFO, "${body}")
                .to("direct:toDB")
                .to("direct:toKafka")

                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.OK_200))
                .transform().simple("Message Processed: ${body}");

        from("direct:toDB")
                .routeId("save-to-db")
                .to("jpa:"+NameAddress.class.getName());

        from("direct:toKafka")
                .convertBodyTo(String.class)
                .to("kafka:camel-topic");

    }
}
