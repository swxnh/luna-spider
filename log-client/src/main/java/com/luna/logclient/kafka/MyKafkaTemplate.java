package com.luna.logclient.kafka;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.stereotype.Component;

/**
 * @author 文轩
 * 自定义kafkaTemplate
 */
public class MyKafkaTemplate<K,V> {


    private final KafkaProducer<K,V> kafkaProducer;

    public MyKafkaTemplate(KafkaProducer<K, V> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    /**
     * send方法
     */
    public void send(String topic, V value) {
        kafkaProducer.send(new org.apache.kafka.clients.producer.ProducerRecord<>(topic, value));
    }
}
