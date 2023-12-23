package com.luna.zhihuspider.spider;

import com.alibaba.fastjson2.JSON;
import com.luna.zhihuspider.properties.ZhihuSpiderProperties;
import com.luna.zhihuspider.spider.pojo.BasePage;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 文轩
 */
@Slf4j
public abstract class BaseSpider<T extends BasePage<?>> implements Spider<T> {

    private final ZhihuSpiderProperties zhihuSpiderProperties;

    protected BaseSpider(ZhihuSpiderProperties zhihuSpiderProperties) {
        this.zhihuSpiderProperties = zhihuSpiderProperties;
    }


    /**
     * 爬取一页数据
     *
     * @param params 爬虫参数
     * @param offset 偏移量
     * @return 爬虫返回的数据
     */
    public T spiderPage(String params, int offset, int limit){
        String url = getUrl(params, offset, limit);
        log.info(getStartLogTemplate(),url);
        //请求
        Connection.Response response = null;
        Map<String, String> headers = getHeaders();
        try {
            response = Jsoup
                    .connect(url)
                    .headers(headers)
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) ")
                    .maxBodySize(0)
                    .execute();
        }catch (IOException e) {
            log.error(getErrorLogTemplate(), url, e);
            return null;
        }
        if (response.statusCode() != 200) {
            log.error(getFailLogTemplate(), url, response.statusCode());
            return null;
        }

        //获取set-cookie
        String cookie = response.header("set-cookie");
        //设置cookie
        if (!"".equals(cookie)) {
            setCookie(headers, cookie);
        }

        //暂停1-5秒
        try {
            Thread.sleep((long) (Math.random() * 4000 + 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //解析并返回结果

        String result = response.body();

        return JSON.parseObject(result, getClazz());
    }


    /**
     * 爬取所有数据
     *
     * @param params 爬虫参数
     */
    public void spiderAll(String params){
        int offset = 0;
        int limit = 10;
        T page = spiderPage(params, offset, limit);
        if (page == null) {
            return;
        }
        handlerPage(page,params);
        while (!page.getPaging().getIs_end()) {
            offset += limit;
            page = spiderPage(params, offset, limit);
            if (page == null) {
                return;
            }
            handlerPage(page,params);
        }
    }

    @Override
    public void setCookie(Map<String, String> headers, String cookie) {

        String cookies = headers.get("Cookie");

        if (cookies == null) {
            cookies = "";
        }
        //转换为map
        Map<String, String> cookieMap = new HashMap<>();
        String[] split = cookies.split(";");
        for (String s : split) {
            //第一个=号之前的是key，之后的是value
            int index = s.indexOf("=");
            String key = s.substring(0, index);
            String value = s.substring(index + 1);
            cookieMap.put(key.trim(), value.trim());

        }
        for (String s : cookie.split(";")) {
            //第一个=号之前的是key，之后的是value
            int index = s.indexOf("=");
            String key = s.substring(0, index);
            String value = s.substring(index + 1);
            cookieMap.put(key.trim(), value.trim());
        }
//        cookieMap.remove("Path");
        //拼接
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : cookieMap.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(";").append(" ");
        }
        //去掉最后一个;
        sb.deleteCharAt(sb.length() - 2);
        String newCookie = sb.toString();
        log.info("设置cookie: {}", newCookie);
        headers.put("Cookie", newCookie);
    }

    /**
     * 获取请求头
     * 随机获取一个请求头
     * @return 请求头
     */
    public Map<String, String> getHeaders() {
        List<Map<String, String>> headersList = zhihuSpiderProperties.getHeadersList();
        int index = (int) (Math.random() * headersList.size());
        return headersList.get(index);
    }
}
