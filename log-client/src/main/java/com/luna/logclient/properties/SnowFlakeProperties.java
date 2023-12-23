package com.luna.logclient.properties;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Date;

/**
 * @author 文轩
 */
@Data
@ConfigurationProperties(prefix = "snowflake")
public class SnowFlakeProperties {

    private Long datacenterId = 1L;

    private Long workerId = 1L;

    private Long startTimestamp = 1577808000000L;

    private Boolean isUseSystemClock = false;
}
