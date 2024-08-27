package com.camel.file_polling_microservice.kafka.exception;

public class KafkaAdminException extends RuntimeException{
    public KafkaAdminException() {
    }

    public KafkaAdminException(String message) {
        super(message);
    }

    public KafkaAdminException(String message, Throwable cause) {
        super(message, cause);
    }
}
