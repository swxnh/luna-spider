package com.luna.zhihuspider.spider;


import com.luna.common.pojo.zhihuspider.Member;
import com.luna.zhihuspider.converter.ZhiHuConverter;
import com.luna.zhihuspider.mapper.MemberMapper;
import com.luna.zhihuspider.properties.ZhihuSpiderProperties;
import com.luna.zhihuspider.spider.pojo.zhihuuser.FollowerData;
import com.luna.zhihuspider.spider.pojo.zhihuuser.FollowersPage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 文轩
 */
@Component
@Slf4j
public class FollowersSpider extends BaseSpider<FollowersPage> {


    private final String startLogTemplate = "开始爬取知乎用户: url={}";

    private final String failLogTemplate = "爬取知乎用户失败: url={}, statusCode={}";

    private final String errorLogTemplate = "爬取知乎用户异常: url={}";

    private final Class<FollowersPage> clazz = FollowersPage.class;




    private final ZhiHuConverter zhiHuConverter;

    private final MemberMapper memberMapper;


    public FollowersSpider(ZhiHuConverter zhiHuConverter,
                           MemberMapper memberMapper,
                           ZhihuSpiderProperties zhihuSpiderProperties) {
        super(zhihuSpiderProperties);
        this.zhiHuConverter = zhiHuConverter;
        this.memberMapper = memberMapper;
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
        return "https://www.zhihu.com/api/v4/members/"+params+"/followees?include=data[*].answer_count,articles_count,gender,follower_count,is_followed,is_following,badge[?(type=best_answerer)].topics"+"&offset="+offset+"&limit="+limit;
    }

    @Override
    public void handlerPage(FollowersPage page, String params) {
        log.info("爬取关注者开始: urlToken={}", params);
        //获取关注者列表
        List<FollowerData> followerDataList = page.getData();
        for (FollowerData followerData : followerDataList) {
            //转换成Member
            Member member = zhiHuConverter.toMember(followerData);
            handlerData(member);
        }
    }

    private void handlerData(Member member) {
        int exist = memberMapper.existByZhihuMemberId(member.getZhihuMemberId());
        if (exist == 0) {
            log.info("插入用户， name={} urlToken={}", member.getName(), member.getUrlToken());
            memberMapper.insert(member);
        }
        else {
            log.info("更新用户，name={} urlToken={}", member.getName(), member.getUrlToken());
            memberMapper.updateByZhihuMemberId(member);
        }
    }

    @Override
    public String getStartLogTemplate() {
        return startLogTemplate;
    }

    @Override
    public String getFailLogTemplate() {
        return failLogTemplate;
    }

    @Override
    public String getErrorLogTemplate() {
        return errorLogTemplate;
    }

    @Override
    public Class<FollowersPage> getClazz() {
        return clazz;
    }
}
