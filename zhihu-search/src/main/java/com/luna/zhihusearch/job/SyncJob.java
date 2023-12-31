package com.luna.zhihusearch.job;

import com.luna.zhihusearch.service.EsPaperService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 数据同步任务
 * @author 文轩
 */
@Component
public class SyncJob {

    private final EsPaperService esPaperService;

    public SyncJob(EsPaperService esPaperService) {
        this.esPaperService = esPaperService;
    }

    /**
     * 同步数据
     * 每隔20分钟同步一次
     */
    @Scheduled(cron = "0 0/20 * * * ?")
    public void syncData() {
        esPaperService.syncData();
    }
}
