package com.luna.zhihusearch.service.impl;

import com.luna.common.pojo.zhihuspider.EsPaper;
import com.luna.zhihusearch.repository.EsPaperRepository;
import com.luna.zhihusearch.service.EsPaperService;
import com.luna.zhihusearch.service.ZhihuSpiderColumnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author 文轩
 */
@Service
@Slf4j
public class EsPaperServiceImpl implements EsPaperService {


    private final EsPaperRepository esPaperRepository;

    private final ZhihuSpiderColumnService zhihuSpiderColumnService;


    public EsPaperServiceImpl(EsPaperRepository esPaperRepository,
                              ZhihuSpiderColumnService zhihuSpiderColumnService) {
        this.esPaperRepository = esPaperRepository;
        this.zhihuSpiderColumnService = zhihuSpiderColumnService;
    }


    /**
     * 搜索正文
     */
    @Override
    @Cacheable(value = "searchContent", key = "#keyword+'&'+#page+'&'+#size")
    public Page<EsPaper> searchContent(String keyword, Integer page, Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        Pageable pageable = PageRequest.of(page-1, size, sort);
        Page<EsPaper> byContent = esPaperRepository.findByContent(keyword,  pageable);


        //高亮
        byContent.forEach(esPaper ->
            esPaper.setContent(esPaper.getContent().replace(keyword, "<span style='color:red'>" + keyword + "</span>"))
        );
        return byContent;
    }



    /**
     * 搜索标题
     *
     * @param keyword 关键字
     * @param page    页码
     * @param size    每页大小
     * @return Page<EsPaper>
     */
    @Override
    @Cacheable(value = "searchTitle", key = "#keyword+'&'+#page+'&'+#size")
    public Page<EsPaper> searchTitle(String keyword, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<EsPaper> byTitle = esPaperRepository.findByTitle(keyword, pageable);

        //高亮
        byTitle.forEach(esPaper ->
                esPaper.setTitle(esPaper.getTitle().replace(keyword, "<span style='color:red'>" + keyword + "</span>"))
        );
        return byTitle;
    }

    /**
     * 搜索摘要
     *
     * @param keyword 关键字
     * @param page    页码
     * @param size    每页大小
     * @return Page<EsPaper>
     */
    @Override
    public Page<EsPaper> searchExcerpt(String keyword, Integer page, Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page-1, size, sort);
        Page<EsPaper> byTitleAndContent = esPaperRepository.findByExcerptTitleOrExcerpt(keyword, keyword, pageable);

        //高亮
        byTitleAndContent.forEach(esPaper -> {
            esPaper.setTitle(esPaper.getExcerptTitle().replace(keyword, "<span style='color:red'>" + keyword + "</span>"));
            esPaper.setContent(esPaper.getExcerpt().replace(keyword, "<span style='color:red'>" + keyword + "</span>"));
        });
        return byTitleAndContent;
    }

    /**
     * @param keyword
     * @param page
     * @param size
     * @return
     */
    @Override
    @Cacheable(value = "searchAll", key = "#keyword+'&'+#page+'&'+#size")
    public Page<EsPaper> searchAll(String keyword, Integer page, Integer size) {
//        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page-1, size);
        Page<EsPaper> byTitleAndContentAndName = esPaperRepository.findByTitleOrContentOrName(keyword, keyword, keyword, pageable);

        //高亮
        byTitleAndContentAndName.forEach(esPaper -> {
            String title = esPaper.getTitle();
            if (title != null) {
                esPaper.setTitle(title.replaceAll(keyword, "<span style='color:red'>" + keyword + "</span>"));
            }
            String content = esPaper.getContent();
            if (content != null) {
                esPaper.setContent(content.replaceAll(keyword, "<span style='color:red'>" + keyword + "</span>"));
            }

            String name = esPaper.getName();
            if (name != null) {
                esPaper.setName(name.replaceAll(keyword, "<span style='color:red'>" + keyword + "</span>"));
            }
        });




        return byTitleAndContentAndName;
    }

    /**
     * 查询数据总数
     */
    @Override
    public Long count() {
        return esPaperRepository.count();
    }

    /**
     * 根据id查询数据
     *
     * @param id
     */
    @Override
    public EsPaper selectById(Long id) {
        return esPaperRepository.findById(id).orElse(null);
    }

    /**
     * 删除索引
     */
    @Override
    public void deleteIndex() {
        esPaperRepository.deleteAll();
    }

    /**
     * 同步数据
     */
    @Override
    public void syncData() {
        syncNewData();
        syncUpdateData();
    }

    /**
     * 同步最新添加的数据
     */
    public void syncNewData() {
        log.info("开始同步最新添加的数据");
        EsPaper esPaper;
        //获取es最后创建的数据
        esPaper = esPaperRepository.findTopByOrderByCreateTimeDesc();
        long lastCreateTimeLong = esPaper.getCreateTime().getTime();
        Date lastCreateTime = new Date(lastCreateTimeLong);
        log.info("es最后创建的数据时间：{}", lastCreateTime);
        //查询大于最后创建时间的数据
        List<EsPaper> lastCreateEsPaperList = zhihuSpiderColumnService.findBeforeCreateTime(lastCreateTimeLong);
        log.info("查询到数据：{}条", lastCreateEsPaperList.size());
        if (lastCreateEsPaperList.isEmpty()) {
            log.info("没有数据需要同步");
        }else {
            //存入es
            esPaperRepository.saveAll(lastCreateEsPaperList);
            log.info("存入es成功");
        }
    }

    /**
     * 同步最新更新的数据
     */
    public void syncUpdateData() {
        log.info("开始同步最新更新的数据");
        EsPaper esPaper;
        //获取es最后更新的数据
        esPaper = esPaperRepository.findTopByOrderByUpdateTimeDesc();
        long lastUpdateTimeLong = esPaper.getUpdateTime().getTime();
        Date lastUpdateTime = new Date(lastUpdateTimeLong);
        log.info("es最后更新的数据时间：{}", lastUpdateTime);
        //查询大于最后更新时间的数据
        List<EsPaper> lastUpdateEsPaperList = zhihuSpiderColumnService.findBeforeUpdateTime(lastUpdateTimeLong);
        log.info("查询到数据：{}条", lastUpdateEsPaperList.size());
        if (lastUpdateEsPaperList.isEmpty()) {
            log.info("没有数据需要同步");
            return;
        } else {
            //存入es
            esPaperRepository.saveAll(lastUpdateEsPaperList);
        }
    }


}
