package com.luna.logserver.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.luna.common.pojo.log.ZhihuLog;
import com.luna.common.utils.ResponseResult;
import com.luna.logserver.mapper.ZhihuLogMapper;
import com.luna.logserver.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 文轩
 */
@Service
public class LogServiceImpl implements LogService {
    private final ZhihuLogMapper zhihuLogMapper;

    @Autowired
    public LogServiceImpl(ZhihuLogMapper zhihuLogMapper) {
        this.zhihuLogMapper = zhihuLogMapper;
    }

    @Override
    public void saveLog(ZhihuLog log) {
        zhihuLogMapper.saveLog(log);
    }

    @Override
    public void saveLog(List<ZhihuLog> logList) {
        zhihuLogMapper.saveLogList(logList);
    }

    @Override
    public ResponseResult<?> queryList(ZhihuLog log, Integer curr, Integer size) {
        PageHelper.startPage(curr, size);
        List<ZhihuLog> logList = zhihuLogMapper.queryList(log);
        PageInfo<ZhihuLog> pageInfo = new PageInfo<>(logList);
        return ResponseResult.ok(pageInfo);


    }

    @Override
    public ResponseResult<?> delete(List<Long> ids) {
        zhihuLogMapper.delete(ids);
        return ResponseResult.DELETE_SUCCESS;
    }

    /**
     * 导出日志
     * @param log
     * @return
     */
    @Override
    public List<ZhihuLog> export(ZhihuLog log) {
        return zhihuLogMapper.queryList(log);
    }
}
