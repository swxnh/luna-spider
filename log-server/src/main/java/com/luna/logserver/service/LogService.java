package com.luna.logserver.service;


import com.luna.common.pojo.log.ZhihuLog;
import com.luna.common.utils.ResponseResult;

import java.util.List;

public interface LogService {

    void saveLog(ZhihuLog log);

    void saveLog(List<ZhihuLog> logList);

    ResponseResult<?> queryList(ZhihuLog log, Integer curr, Integer size);

    ResponseResult<?> delete(List<Long> ids);

    List<ZhihuLog> export(ZhihuLog log);
}
