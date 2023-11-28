package com.luna.zhihusearch.service;

import com.luna.common.pojo.zhihuspider.EsPaper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

/**
 * @author 文轩
 * 知乎专栏服务rpc接口
 */
@FeignClient(name = "zhihu-spider", path = "/zhihu/spider/column")
public interface ZhihuSpiderColumnService {

    /**
     * 查询最新添加的数据
     * @param createTime
     * @return
     */
    @GetMapping("/createTime/before/{createTime}")
    List<EsPaper> findBeforeCreateTime(@PathVariable Long createTime);

    /**
     * 查询最新更新的数据
     * @param updateTime
     * @return
     */
    @GetMapping("/updateTime/before/{updateTime}")
    List<EsPaper> findBeforeUpdateTime(@PathVariable Long updateTime);


}
