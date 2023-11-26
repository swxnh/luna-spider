package com.luna.zhihusearch.controller;

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
public class ZhiHuColumnController {



    private final EsPaperService esPaperService;



    public ZhiHuColumnController(EsPaperService esPaperService) {
        this.esPaperService = esPaperService;
    }

//    /**
//     * 专栏列表
//     */
//    @RequestMapping("/list")
//    public ResponseResult<List<ColumnVo>> list(@RequestParam(defaultValue = "") Integer page,
//                                               @RequestParam(defaultValue = "") Integer size){
//        if (page == null || page < 1) {
//            page = 1;
//        }
//        if (size == null || size < 1 || size > 10) {
//            size = 10;
//        }
//
//        return ResponseResult.ok(columnService.list(page, size));
//    }

//    /**
//     * 专栏文章列表
//     */
//    @RequestMapping("/article/list")
//    public ResponseResult<List<Paper>> articleList(@RequestParam(defaultValue = "") Integer page,
//                                                   @RequestParam(defaultValue = "") Integer size){
//        if (page == null || page < 1) {
//            page = 1;
//        }
//        if (size == null || size < 1 || size > 10) {
//            size = 10;
//        }
//
//        return ResponseResult.ok(paperService.list(page, size));
//    }

    /**
     * 文章搜索
     */
    @RequestMapping("/article/search/content")
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
    @RequestMapping("/article/search/title")
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
    @RequestMapping("/article/search/excerpt")
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
    @RequestMapping("/article/search/all")
    public ResponseResult<Page<EsPaper>> articleSearchAll(@RequestParam(defaultValue = "") String keyword,
                                                          @RequestParam(defaultValue = "") Integer page,
                                                          @RequestParam(defaultValue = "") Integer size){
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1 || size > 10) {
            size = 10;
        }
        //判断是否是虚拟线程
        System.out.println(Thread.currentThread().isVirtual());

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
