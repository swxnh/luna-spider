package com.luna.zhihuspider.service.impl;

import com.luna.common.pojo.zhihuspider.EsPaper;
import com.luna.common.utils.ResponseResult;
import com.luna.zhihuspider.mapper.PaperMapper;
import com.luna.zhihuspider.service.ColumnService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 * @author 文轩
 */
@Service
public class ColumnServiceImpl implements ColumnService {

    private final PaperMapper paperMapper;

    public ColumnServiceImpl(PaperMapper paperMapper) {
        this.paperMapper = paperMapper;
    }


    @Override
    public ResponseResult<List<EsPaper>> findBeforeCreateTime(Long createTime) {
        List<EsPaper> esPaperList = paperMapper.selectBeforeCreateTime(new Date(createTime));
        return ResponseResult.ok(esPaperList);
    }

    @Override
    public ResponseResult<List<EsPaper>> findBeforeUpdateTime(Long updateTime) {
        List<EsPaper> esPaperList = paperMapper.selectBeforeUpdateTime(new Date(updateTime));
        return ResponseResult.ok(esPaperList);
    }
}
