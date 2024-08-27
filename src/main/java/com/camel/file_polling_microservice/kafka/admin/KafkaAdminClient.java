package com.camel.file_polling_microservice.kafka.admin;

import com.camel.file_polling_microservice.config.KafkaConfigData;
import com.camel.file_polling_microservice.config.RetryConfigData;
import com.camel.file_polling_microservice.kafka.exception.KafkaAdminException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class KafkaAdminClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaAdminClient.class);

    private final KafkaConfigData kafkaConfigData;
    private final RetryConfigData retryConfigData;
    private final RetryTemplate retryTemplate;
    private final AdminClient adminClient;

    public KafkaAdminClient(KafkaConfigData kafkaConfigData, RetryConfigData retryConfigData,
                            RetryTemplate retryTemplate, AdminClient adminClient) {
        this.kafkaConfigData = kafkaConfigData;
        this.retryConfigData = retryConfigData;
        this.retryTemplate = retryTemplate;
        this.adminClient = adminClient;
    }

    public void createTopic() {
        CreateTopicsResult createTopicsResult = null;

        try {
            createTopicsResult = retryTemplate.execute(this::doCreateTopic);
            LOGGER.info("Create topic results {}", createTopicsResult.values());
        }catch (Throwable e) {
            LOGGER.error("Exception caught while creating the topic");
            throw new KafkaAdminException("Exception caught while creating the topic", e);
        }
        checkTopicsCreated();
    }

    private CreateTopicsResult doCreateTopic(RetryContext retryContext) {
        List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();
        LOGGER.info("Creating topics: {}, attempt: {}", topicNames, retryContext.getRetryCount());
        List<NewTopic> topics = topicNames.stream().map(topic -> new NewTopic(
                topic.trim(),
                kafkaConfigData.getNumOfPartitions(),
                kafkaConfigData.getReplicationFactor()
        )).toList();
        return adminClient.createTopics(topics);
    }
    public void checkTopicsCreated() {
//        Executors.newSingleThreadExecutor().submit(() -> {
        Collection<TopicListing> topicListings = getTopics();
        int retryCount = 1;
        int maxRetry = retryConfigData.getMaxAttempts();
        int multiplier = retryConfigData.getMultiplier().intValue();
        long sleepTimeMs = retryConfigData.getSleepTimeMs();
        for (String topicName: kafkaConfigData.getTopicNamesToCreate()) {
            while (!isTopicCreated(topicListings, topicName)) {
                checkMaxRetry(retryCount++, maxRetry);
                sleep(sleepTimeMs);
                sleepTimeMs *= multiplier;
                topicListings = getTopics();
            }
        }
//        });
    }

    private boolean isTopicCreated(Collection<TopicListing> topicListings, String topicName) {
        if (topicListings == null){
            return false;
        }
        return topicListings.stream().anyMatch(topicListing -> topicListing.name().equals(topicName));
    }

    private void checkMaxRetry(int retryCount, int maxRetry) {
        if (retryCount > maxRetry) {
            throw new KafkaAdminException("Reached Max Attempts while reading topics");
        }
    }

    private void sleep(long sleepTimeMs) {
        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException e) {
            throw new KafkaAdminException("Interrupted while sleeping for checking topics");
        }
    }

    private Collection<TopicListing> getTopics() {
        Collection<TopicListing> topicListings;

        try {
            topicListings = retryTemplate.execute(this::doGetTopics);
        } catch (Throwable e) {
            throw new KafkaAdminException("Exception while getting topics", e);
        }

        return topicListings;
    }

    private Collection<TopicListing> doGetTopics(RetryContext retryContext) throws ExecutionException,
            InterruptedException {
        LOGGER.info("Reading Kafka Topic(s) {}, attempt {}",
                kafkaConfigData.getTopicNamesToCreate(), retryContext.getRetryCount());
        Collection<TopicListing> topics = adminClient.listTopics().listings().get();
        if (topics != null) {
            topics.forEach(topicListing -> LOGGER.info("Topic with name: {}, is ready", topicListing.name()));
        }
        return topics;
    }
}
