package com.luna.zhihusearch.service;


import com.luna.common.pojo.zhihuspider.EsPaper;
import org.springframework.data.domain.Page;

/**
 *
 * @author 文轩
 */
public interface EsPaperService {



    /**
     * 搜索正文
     */
    Page<EsPaper> searchContent(String keyword, Integer page, Integer size);



    /**
     * 搜索标题
     * @param keyword 关键字
     * @param page 页码
     * @param size 每页大小
     * @return Page<EsPaper>
     */
    Page<EsPaper> searchTitle(String keyword, Integer page, Integer size);

    /**
     * 搜索摘要
     * @param keyword 关键字
     * @param page 页码
     * @param size 每页大小
     * @return Page<EsPaper>
     */
    Page<EsPaper> searchExcerpt(String keyword, Integer page, Integer size);

    /**
     * @return
     */
    Page<EsPaper> searchAll(String keyword, Integer page, Integer size);


    /**
     * 查询数据总数
     */
    Long count();

    /**
     * 根据id查询数据
     */
    EsPaper selectById(Long id);

    /**
     * 删除索引
     */
    void deleteIndex();

    /**
     * 同步数据
     */
    void syncData();
}
