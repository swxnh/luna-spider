package com.luna.zhihuspider.controller;

import com.luna.common.pojo.zhihuspider.EsPaper;
import com.luna.common.utils.ResponseResult;
import com.luna.zhihuspider.service.ColumnService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 专栏控制器
 * @author 文轩
 */
@RestController
@RequestMapping("/column")
public class ColumnController {

    private final ColumnService columnService;

    public ColumnController(ColumnService columnService) {
        this.columnService = columnService;
    }


    /**
     * 查询最新添加的数据
     * @param createTime
     * @return
     */
    @GetMapping("/createTime/before/{createTime}")
    ResponseResult<List<EsPaper>> findBeforeCreateTime(@PathVariable Long createTime) {
        return columnService.findBeforeCreateTime(createTime);


    }

    /**
     * 查询最新更新的数据
     *
     * @param updateTime
     * @return
     */
    @GetMapping("/updateTime/before/{updateTime}")
    ResponseResult<List<EsPaper>> findBeforeUpdateTime(@PathVariable Long updateTime) {

        return columnService.findBeforeUpdateTime(updateTime);

    }
}
