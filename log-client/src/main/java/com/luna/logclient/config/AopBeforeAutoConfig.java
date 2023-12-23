package com.luna.logclient.config;

import cn.hutool.core.lang.Snowflake;
import com.luna.logclient.kafka.MyKafkaTemplate;
import com.luna.logclient.properties.SnowFlakeProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author 文轩
 */
@Configuration
@EnableConfigurationProperties(SnowFlakeProperties.class)
public class AopBeforeAutoConfig {

    private final SnowFlakeProperties snowFlakeProperties;

    public AopBeforeAutoConfig(SnowFlakeProperties snowFlakeProperties) {
        this.snowFlakeProperties = snowFlakeProperties;
    }


    public KafkaProducer<?, ?> kafkaProducer() {
        //读取配置文件,由于该模块会被其他模块引用，所以应该保证该配置文件在任何模块都能读取到
        Properties properties = new Properties();
        try (InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("log-client.properties")){
            properties.load(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException("读取配置文件失败");
        }

        //指定key使用的序列化类
        Serializer<String> keySerializer = new StringSerializer();
        //指定value使用的序列化类
        Serializer<Object> valueSerializer = new CommonKafkaSerializer();
        //创建Kafka生产者
        return new KafkaProducer<>(properties, keySerializer, valueSerializer);
    }

    @Bean
    public MyKafkaTemplate<?, ?> myKafkaTemplate() {
        return new MyKafkaTemplate<>(kafkaProducer());
    }


    @Bean
    @ConditionalOnMissingBean(Snowflake.class)
    public Snowflake snowflake() {
        return new Snowflake(
                new Date(snowFlakeProperties.getStartTimestamp()),
                snowFlakeProperties.getWorkerId(),
                snowFlakeProperties.getDatacenterId(),
                snowFlakeProperties.getIsUseSystemClock()
        );

    }
}
