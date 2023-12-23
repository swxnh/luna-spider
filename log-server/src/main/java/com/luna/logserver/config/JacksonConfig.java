package com.luna.logserver.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * jackson配置
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder
                    .simpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .timeZone("GMT+8")
                    .serializerByType(Long.class, ToStringSerializer.instance)
                    .serializerByType(Long.TYPE, ToStringSerializer.instance);
    }


}
