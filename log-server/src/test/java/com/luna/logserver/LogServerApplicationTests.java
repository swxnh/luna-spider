package com.luna.logserver;

import com.luna.common.constants.KafkaConstants;
import com.luna.common.pojo.log.ZhihuLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class LogServerApplicationTests {

    @Autowired
    private KafkaTemplate<String, ZhihuLog> kafkaTemplate;


    @Test
    void contextLoads() throws InterruptedException {

        ZhihuLog zhihuLog = new ZhihuLog();
        zhihuLog.setId(2L);
        zhihuLog.setTime(100);
        zhihuLog.setModule("测试");
        zhihuLog.setMethod("测试");
        zhihuLog.setIpAddr("192.168.1.1");
        List<Object> argsJson = new ArrayList<>();
        argsJson.add("测试");
        zhihuLog.setArgsJson(argsJson);
        zhihuLog.setUserId(0L);
        kafkaTemplate.send(KafkaConstants.LOG_TOPIC, zhihuLog);
    }

}
