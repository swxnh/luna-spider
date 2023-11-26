package com.luna.zhihuspider.spider;

import com.luna.common.pojo.zhihuspider.Column;
import com.luna.zhihuspider.converter.ZhiHuConverter;
import com.luna.zhihuspider.mapper.ColumnMapper;
import com.luna.zhihuspider.properties.ZhihuSpiderProperties;
import com.luna.zhihuspider.spider.pojo.columns.ColumnBO;
import com.luna.zhihuspider.spider.pojo.columns.ColumnInfo;
import com.luna.zhihuspider.spider.pojo.columns.ColumnsPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 专栏爬虫
 * @author 文轩
 */
@Component
@Slf4j
public class ColumnsSpider extends BaseSpider<ColumnsPage> {

    private final Class<ColumnsPage> clazz = ColumnsPage.class;



    private final ZhiHuConverter zhiHuConverter;

    private final ColumnMapper columnMapper;

    public ColumnsSpider(ZhiHuConverter zhiHuConverter,
                         ColumnMapper columnMapper,
                         ZhihuSpiderProperties zhihuSpiderProperties) {
        super(zhihuSpiderProperties);
        this.zhiHuConverter = zhiHuConverter;
        this.columnMapper = columnMapper;
    }


    /**
     * 获取爬虫开始日志模板
     *
     * @return 日志模板
     */
    @Override
    public String getStartLogTemplate() {
        String startLogTemplate = "开始爬取专栏: url={}";
        return startLogTemplate;
    }

    /**
     * 获取爬虫失败日志模板
     *
     * @return 日志模板
     */
    @Override
    public String getFailLogTemplate() {
        String failLogTemplate = "爬取专栏失败: url={}, statusCode={}";
        return failLogTemplate;
    }

    /**
     * 获取爬虫错误日志模板
     *
     * @return 日志模板
     */
    @Override
    public String getErrorLogTemplate() {
        String errorLogTemplate = "爬取专栏异常: url={}";
        return errorLogTemplate;
    }

    /**
     * 获取爬虫返回的数据类型
     *
     * @return 爬虫返回的数据类型
     */
    @Override
    public Class<ColumnsPage> getClazz() {
        return clazz;
    }

    /**
     * 组装爬虫前缀
     *
     * @param params
     * @param offset
     * @param limit
     */
    @Override
    public String getUrl(String params, int offset, int limit) {
        return "https://www.zhihu.com/api/v4/members/" + params + "/column-contributions?include=data[*].column.intro,followers,articles_count,voteup_count,items_count&offset=" + offset + "&limit=" + limit;
    }

    @Override
    public void handlerPage(ColumnsPage page, String params) {
        //提取取data列表中的columns作为list
        List<ColumnInfo> data = page.getData();
        List<ColumnBO> columnBOList = new ArrayList<>(data.size());
        for (ColumnInfo columnInfo : data) {
            columnBOList.add(columnInfo.getColumn());
        }
        List<Column> columnsList = zhiHuConverter.toColumn(columnBOList);
        handlerList(columnsList);

    }

    private void handlerList(List<Column> columnsList) {
        for (Column column : columnsList) {
            handlerData(column);
        }
    }

    private void handlerData(Column column) {
        int exist = columnMapper.existByZhihuColumnId(column.getZhihuColumnId());
        if (exist == 0) {
            log.info("插入专栏， name={} zhihuColumnId={}", column.getTitle(), column.getZhihuColumnId());
            columnMapper.insert(column);
        } else {
            log.info("更新专栏，name={} zhihuColumnId={}", column.getTitle(), column.getZhihuColumnId());
            columnMapper.updateByZhihuColumnId(column);
        }
    }
}
