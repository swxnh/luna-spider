package com.luna.logserver.listen;


import com.luna.common.constants.KafkaConstants;
import com.luna.common.pojo.log.ZhihuLog;
import com.luna.logserver.service.LogService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 文轩
 */
@Component
public class LogListen {

    private final LogService logService;

    public LogListen(LogService logService) {
        this.logService = logService;
    }

    @KafkaListener(topics = KafkaConstants.LOG_TOPIC)
    public void saveLog(@Payload List<ZhihuLog> logList) {
        logService.saveLog(logList);

    }
}
