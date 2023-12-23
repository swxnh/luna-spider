package com.luna.zhihusearch.controller;

import com.luna.common.anno.ZhihuSystemLog;
import com.luna.common.enmu.Method;
import com.luna.common.enmu.Module;
import com.luna.common.pojo.zhihuspider.EsPaper;
import com.luna.zhihusearch.service.EsPaperService;
import com.luna.common.utils.ResponseResult;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 专栏控制器
 * @author 文轩
 */
@RestController
@RequestMapping("/column")
@CrossOrigin(origins = "*")
@ZhihuSystemLog(model = Module.ARTICLE)
public class ZhiHuColumnController {



    private final EsPaperService esPaperService;



    public ZhiHuColumnController(EsPaperService esPaperService) {
        this.esPaperService = esPaperService;
    }


    /**
     * 文章搜索
     */
    @RequestMapping("/article/content")
    public ResponseResult<Page<EsPaper>> articleSearch(@RequestParam(defaultValue = "") String keyword,
                                                       @RequestParam(defaultValue = "") Integer page,
                                                       @RequestParam(defaultValue = "") Integer size){
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1 || size > 10) {
            size = 10;
        }

        return ResponseResult.ok(esPaperService.searchContent(keyword, page, size));
    }

    /**
     * 文章标题搜索
     */
    @RequestMapping("/article/title")
    public ResponseResult<Page<EsPaper>> articleSearchTitle(@RequestParam(defaultValue = "") String keyword,
                                                            @RequestParam(defaultValue = "") Integer page,
                                                            @RequestParam(defaultValue = "") Integer size){
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1 || size > 10) {
            size = 10;
        }

        return ResponseResult.ok(esPaperService.searchTitle(keyword, page, size));
    }


    /**
     * 摘要搜索(包括标题和正文)
     */
    @RequestMapping("/article/excerpt")
    public ResponseResult<Page<EsPaper>> articleSearchExcerpt(@RequestParam(defaultValue = "") String keyword,
                                                              @RequestParam(defaultValue = "") Integer page,
                                                              @RequestParam(defaultValue = "") Integer size){
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1 || size > 10) {
            size = 10;
        }

        return ResponseResult.ok(esPaperService.searchExcerpt(keyword, page, size));
    }

    /**
     * 全文搜索
     */
    @ZhihuSystemLog(method = Method.SEARCH)
    @RequestMapping("/article/all")
    public ResponseResult<Page<EsPaper>> articleSearchAll(@RequestParam(defaultValue = "") String keyword,
                                                          @RequestParam(defaultValue = "") Integer page,
                                                          @RequestParam(defaultValue = "") Integer size){
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1 || size > 10) {
            size = 10;
        }

        return ResponseResult.ok(esPaperService.searchAll(keyword, page, size));
    }

    /**
     * 数据总量
     */
    @RequestMapping("/article/count")
    public ResponseResult<Long> articleCount(){
        return ResponseResult.ok(esPaperService.count());
    }


}
