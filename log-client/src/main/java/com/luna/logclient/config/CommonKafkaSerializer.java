package com.luna.logclient.config;

import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * @author 文轩
 */
public class CommonKafkaSerializer implements Serializer<Object> {

    @Override
    public byte[] serialize(String s, Object o) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(o);
            oos.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
