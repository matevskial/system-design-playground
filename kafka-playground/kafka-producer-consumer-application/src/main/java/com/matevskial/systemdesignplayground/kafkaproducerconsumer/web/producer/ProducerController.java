package com.matevskial.systemdesignplayground.kafkaproducerconsumer.web.producer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/producer")
@RequiredArgsConstructor
public class ProducerController {

    private final Producer<String, String> producer;

    /**
     * We don't wait for the future to complete.
     * This kind of usage might be suitable for metrics where we don't need to quarantee that every
     * record was sent but instead work with best-effort
     */
    @PostMapping("/produce")
    public void produce() throws Exception {
        producer.send(new ProducerRecord<>("test-topic", "123"));
    }
}
