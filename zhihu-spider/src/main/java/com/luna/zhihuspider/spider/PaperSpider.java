package com.luna.zhihuspider.spider;

import com.luna.common.pojo.zhihuspider.Paper;
import com.luna.zhihuspider.converter.ZhiHuConverter;
import com.luna.zhihuspider.mapper.PaperMapper;
import com.luna.zhihuspider.properties.ZhihuSpiderProperties;
import com.luna.zhihuspider.spider.pojo.papers.PapersPage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 文轩
 * @version 1.0
 */
@Component("paperSpider")
@Slf4j
public class PaperSpider extends BaseSpider<PapersPage> {

    private final static String URL = "https://www.zhihu.com/api/v4/columns/";

    private final Class<PapersPage> clazz = PapersPage.class;


    private final PaperMapper paperMapper;

    private final ZhiHuConverter zhiHuConverter;

    public PaperSpider(PaperMapper paperMapper,
                       ZhiHuConverter zhiHuConverter,
                       ZhihuSpiderProperties zhihuSpiderProperties) {
        super(zhihuSpiderProperties);
        this.paperMapper = paperMapper;
        this.zhiHuConverter = zhiHuConverter;
    }

    /**
     * 组装爬虫前缀
     *
     * @param params 爬虫参数
     * @param offset
     * @param limit
     */
    @Override
    public String getUrl(String params, int offset, int limit) {
        return URL + params + "/items" + "?limit=" + limit + "&offset=" + offset;
    }

    @Override
    public void handlerPage(PapersPage page, String params) {
        List<Paper> paperList = zhiHuConverter.toPaper(page.getData());
        for (Paper paper : paperList) {
            paper.setZhihuColumnId(params);
            handler(paper);
        }
    }


    private void handler(Paper paper) {
        //判断是否存在
        boolean exists = paperMapper.existsWithZhuhuPaperId(paper.getZhihuPaperId());
        if (!exists) {
            //不存在，插入
            log.info("插入文章: {}", paper.getZhihuPaperId());
            paperMapper.insert(paper);
        } else {
            //存在，更新
            log.info("更新文章: {}", paper.getZhihuPaperId());
            paperMapper.updateByZhuhuPaperId(paper);
        }
    }

    @Override
    public String getStartLogTemplate() {
        return "开始爬取专栏文章: url={}";
    }

    @Override
    public String getFailLogTemplate() {
        return "爬取专栏文章失败: url={}, statusCode={}";
    }

    @Override
    public String getErrorLogTemplate() {
        return "爬取专栏文章异常: url={}";
    }

    @Override
    public Class<PapersPage> getClazz() {
        return clazz;
    }
}
