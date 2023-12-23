package com.luna.logserver.config;

import org.apache.kafka.common.serialization.Deserializer;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 * @author 文轩
 */
public class CommonKafkaDeserializer implements Deserializer<Object> {
    @Override
    public Object deserialize(String s, byte[] bytes) {
        try (ByteArrayInputStream ins = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(ins)) {
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
