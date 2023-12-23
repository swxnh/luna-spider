package com.luna.logserver.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.converters.longconverter.LongStringConverter;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.luna.common.pojo.log.ZhihuLog;
import com.luna.common.utils.ResponseResult;
import com.luna.logserver.pojo.SystemLogExcel;
import com.luna.logserver.service.LogService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;


/**
 * @author 文轩
 */
@RestController
@RequestMapping("/log")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }


    @RequestMapping("/queryList")
    public ResponseResult<?> queryList(@RequestBody ZhihuLog log,
                                       @RequestParam(defaultValue = "1") Integer curr,
                                       @RequestParam(defaultValue = "10") Integer size) {
        return logService.queryList(log, curr, size);
    }


    @RequestMapping("/delete")
    public ResponseResult<?> delete(List<Long> ids) {
        return logService.delete(ids);
    }

    /**
     * 导出日志
     */
    @RequestMapping("/export")
    public List<ZhihuLog> export(@RequestBody ZhihuLog log) {
        return logService.export(log);
    }

    /**
     * 下载日志
     */
    @RequestMapping("/download")
    public void download(@RequestBody ZhihuLog log, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("log", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        List<ZhihuLog> export = logService.export(log);

        EasyExcel.write(response.getOutputStream(), SystemLogExcel.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .registerConverter(new LongStringConverter())
                .sheet("操作日志")
                .doWrite(export);

    }
}
