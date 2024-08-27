package com.camel.file_polling_microservice.components;

import com.camel.file_polling_microservice.config.KafkaConfigData;
import com.camel.file_polling_microservice.dto.NameAddress;
import com.camel.file_polling_microservice.kafka.admin.KafkaAdminClient;
import com.camel.file_polling_microservice.utility.RandomObjectsGenerator;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NewKafkaProducerRoute extends RouteBuilder {
    private final static Logger LOGGER = LoggerFactory.getLogger(NewKafkaProducerRoute.class);

    private final KafkaAdminClient kafkaAdminClient;
    private final RandomObjectsGenerator randomObjectsGenerator;
    private final KafkaConfigData kafkaConfigData;

    public NewKafkaProducerRoute(KafkaAdminClient kafkaAdminClient, RandomObjectsGenerator randomObjectsGenerator, KafkaConfigData kafkaConfigData) {
        this.kafkaAdminClient = kafkaAdminClient;
        this.randomObjectsGenerator = randomObjectsGenerator;
        this.kafkaConfigData = kafkaConfigData;
    }

    @Override
    public void configure() throws Exception {
        from("timer:kafka-create-topics?repeatCount=1")
                .routeId("create-kafka-topic")
                .bean(kafkaAdminClient, "createTopic")
                .to("controlbus:route?routeId=kafka-producer&action=start");

        from("timer:produceEvents?period=60000")
                .routeId("kafka-producer")
                .autoStartup(false)
                .bean(randomObjectsGenerator, "generateNameAddress")
                .to("direct:saveToDB")
                .to("direct:toNewKafka");

        from("direct:saveToDB")
                .to("jpa:"+ NameAddress.class.getName());

        from("direct:toNewKafka")
                .convertBodyTo(String.class)
                .to("kafka:"+kafkaConfigData.getTopicName());
    }
}
