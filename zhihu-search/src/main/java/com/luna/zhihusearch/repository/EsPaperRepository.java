package com.luna.zhihusearch.repository;

import com.luna.common.pojo.zhihuspider.EsPaper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author 文轩
 */
public interface EsPaperRepository extends ElasticsearchRepository<EsPaper, Long> {

    /**
     * 搜索正文
     */
    Iterable<EsPaper> findByContent(String keyword);

    /**
     * 分页搜索正文
     */
    Page<EsPaper> findByContent(String keyword, Pageable pageable);


    Page<EsPaper> findByTitle(String keyword, Pageable pageable);


    Page<EsPaper> findByTitleOrContentOrName(String keyword, String keyword1, String keyword2, Pageable pageable);

    Page<EsPaper> findByExcerptTitleOrExcerpt(String keyword, String keyword1, Pageable pageable);

    EsPaper findTopByOrderByIdDesc();

    EsPaper findTopByOrderByUpdateTimeDesc();

    EsPaper findTopByOrderByCreateTimeDesc();


}
