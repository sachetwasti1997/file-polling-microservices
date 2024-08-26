package com.camel.file_polling_microservice.components;

import com.camel.file_polling_microservice.dto.NameAddress;
import com.camel.file_polling_microservice.kafka.KafkaAdminClient;
import com.camel.file_polling_microservice.processor.InboundMessageProcessor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class BatchJpaProcessingRoute extends RouteBuilder {

    private final KafkaAdminClient kafkaAdminClient;

    public BatchJpaProcessingRoute(KafkaAdminClient kafkaAdminClient) {
        this.kafkaAdminClient = kafkaAdminClient;
    }

    @Override
    public void configure() throws Exception {
        from("timer:readDB?period=10000")
                .routeId("read-db-id")
                .to("jpa:"+ NameAddress.class.getName()+"?namedQuery=fetchAllRows")
                .split(body())
                .process(new InboundMessageProcessor())
                .log(LoggingLevel.INFO, "Transformed Body: ${body}")
                .convertBodyTo(String.class)
                .to("file:src/data/output?fileName=outputFile.csv&fileExist=append&appendChars=\\n")
                .toD("jpa:"+NameAddress.class.getName()
                        +"?nativeQuery=DELETE FROM name_address WHERE id=${header.consumedId}&useExecuteUpdate=true")
                .end();
    }
}
