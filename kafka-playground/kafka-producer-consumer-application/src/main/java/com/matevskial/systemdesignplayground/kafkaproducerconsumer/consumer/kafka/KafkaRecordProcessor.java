package com.matevskial.systemdesignplayground.kafkaproducerconsumer.consumer.kafka;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class KafkaRecordProcessor {

    private final Consumer<String, String> consumer;

    @PostConstruct
    void postConstruct() {
        setUp();
    }

    private void setUp() {
        Thread kafkaConsumerThread = new Thread(this::processRecords, "kafka-consumer-main");
        kafkaConsumerThread.start();
    }

    private void processRecords() {
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                process(record);
            }
        }
    }

    private void process(ConsumerRecord<String, String> record) {
        System.out.println("CONSUMER DATA: " + record.value());
    }
}
