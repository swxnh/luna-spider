package com.luna.zhihuspider.controller;

import com.luna.common.anno.ZhihuSystemLog;
import com.luna.common.enmu.Method;
import com.luna.common.pojo.zhihuspider.EsPaper;
import com.luna.common.utils.ResponseResult;
import com.luna.zhihuspider.service.ColumnService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.luna.common.enmu.Method.QUERY;
import static com.luna.common.enmu.Module.ARTICLE;

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
    ResponseResult<List<EsPaper>> findBeforeCreateTime(@PathVariable Long createTime,
                                                       @RequestParam(required = false,defaultValue = "100") Integer size) {
        return columnService.findBeforeCreateTime(createTime,size);


    }

    /**
     * 查询最新更新的数据
     *
     * @param updateTime
     * @return
     */
    @GetMapping("/updateTime/before/{updateTime}")
    ResponseResult<List<EsPaper>> findBeforeUpdateTime(@PathVariable Long updateTime,
                                                       @RequestParam(required = false,defaultValue = "100") Integer size) {

        return columnService.findBeforeUpdateTime(updateTime,size);

    }
}
