package com.luna.logserver.mapper;

import com.luna.common.pojo.log.ZhihuLog;

import java.util.List;

/**
* @author 文轩
* @description 针对表【t_zhihu_log】的数据库操作Mapper
* @createDate 2023-12-10 21:50:09
* @Entity com.luna.logserver.pojo.ZhihuLog
*/
public interface ZhihuLogMapper {


    void saveLog(ZhihuLog log);

    List<ZhihuLog> queryList(ZhihuLog log);

    void delete(List<Long> ids);

    int saveLogList(List<ZhihuLog> logList);
}




