package com.matevskial.systemdesignplayground.kafkaproducerconsumer.config;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
class KafkaProducerConfig {

    @Bean
    public Producer<String, String> kafkaProducer() {
        String topicName = "test-topic";

        Properties props = new Properties();

        props.put("bootstrap.servers", "localhost:9092");

        // all is slowest, means that each broker in cluster needs to ack that the record replicated
        props.put("acks", "all");

        props.put("retries", 3);

        // this configuration sets the maximum block time for the async methods such as producer.send(...)
        // the default is 60 seconds
        props.put("max.block.ms", 5000);

        // so we can optimize the process of producing records
        props.put("batch.size", 16384);

//        props.put("linger.ms", 1);

        //The buffer.memory controls the total amount of memory available to the producer for buffering.
//        props.put("buffer.memory", 3355);

        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        return producer;
    }
}
