package com.luna.zhihusearch.service;


import com.luna.common.vo.zhihusearch.AssociationalWordVO;

import java.util.List;

/**
 * 关联词服务
 * @Author: 文轩
 */
public interface AssociationalWordServer {

    /**
     * 获取关联词
     */
    List<AssociationalWordVO> getAssociationalWord(String keyword);

//    int getRelativeLength(String word);
//
//    List<String> split(String word);

    /**
     * 初始化
     */
    void init();
}
