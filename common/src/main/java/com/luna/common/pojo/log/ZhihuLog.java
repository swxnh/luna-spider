package com.luna.common.pojo.log;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author 文轩
 * @TableName t_zhihu_log
 */
@Data
public class ZhihuLog implements Serializable {
    /**
     * 
     */
    private Long id;

    /**
     * 模块
     */
    private String module;

    /**
     * 方法
     */
    private String method;

    /**
     * 调用的方法全限定名
     */
    private String useFunction;

    /**
     * 方法参数(jsonb)
     */
    private Object argsJson;

    /**
     * 执行时间 单位毫秒
     */
    private Integer time;

    /**
     * 用户id 默认为0即匿名用户
     */
    private Long userId;

    /**
     * ip地址
     */
    private String ipAddr;

    /**
     * 方法开始时间
     */
    private Date startTime;

    /**
     * 方法结束时间
     */
    private Date endTime;

    /**
     * 入库时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}