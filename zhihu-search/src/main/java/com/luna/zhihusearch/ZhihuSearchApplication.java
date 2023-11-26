package com.luna.zhihusearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 文轩
 */
@SpringBootApplication
@EnableCaching
@EnableFeignClients(basePackages = "com.luna.zhihusearch.service")
public class ZhihuSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhihuSearchApplication.class, args);
    }

}
