package com.luna.zhihuspider.service;

import com.luna.common.pojo.zhihuspider.EsPaper;
import com.luna.common.utils.ResponseResult;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * 专栏服务接口
 * @author 文轩
 */
public interface ColumnService {
    ResponseResult<List<EsPaper>> findBeforeCreateTime(Long createTime, Integer size);

    ResponseResult<List<EsPaper>> findBeforeUpdateTime(Long updateTime, Integer size);
}
