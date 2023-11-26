package com.luna.zhihusearch.service.impl;

import com.luna.common.vo.zhihusearch.AssociationalWordVO;
import com.luna.zhihusearch.mapper.AssociationalWordMapper;
import com.luna.zhihusearch.pojo.Trie;
import com.luna.zhihusearch.service.AssociationalWordServer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 文轩
 * 联想词的字典树实现
 */
@Service
@Slf4j
public class AssociationalWordServerTreeImpl implements AssociationalWordServer {

    private final Trie trie;

    private final AssociationalWordMapper associationalWordMapper;

    private final StringRedisTemplate redisTemplate;




    public AssociationalWordServerTreeImpl(AssociationalWordMapper associationalWordMapper,
                                           StringRedisTemplate redisTemplate) {
        this.associationalWordMapper = associationalWordMapper;
        this.redisTemplate = redisTemplate;
        trie = new Trie();
    }


    /**
     * 获取关联词
     */
    public List<AssociationalWordVO> getAssociationalWord(String keyword){
        List<String> wordsByPrefixOrderByWeight = trie.getWordsByPrefixOrderByWeight(keyword, 10);
        //转换
        List<AssociationalWordVO> associationalWordVOList = new ArrayList<>(10);
        for (String word : wordsByPrefixOrderByWeight) {
            AssociationalWordVO associationalWordVO = new AssociationalWordVO();
            associationalWordVO.setValue(word);
            associationalWordVOList.add(associationalWordVO);
        }
        return associationalWordVOList;

    }

    /**
     * 初始化
     */
    @Override
    @PostConstruct
    public void init() {

        log.info("初始化关联词");
        int limit = 1000;
        int offset = 0;
        List<String> associationalWordList = associationalWordMapper.selectPage(limit, offset);
        while (!associationalWordList.isEmpty()) {
            for (String word : associationalWordList) {
                List<String> split = split(word);
                trie.addAll(split);
            }
            offset += limit;
            associationalWordList = associationalWordMapper.selectPage(limit, offset);
        }
        log.info("初始化关联词完成");
    }

    /**
     * 字符串切分
     */
    private List<String> split(String word) {
        if (word == null || word.isEmpty()) {
            return new ArrayList<>();
        }
        //使用空格切分
        String[] split = word.split(" ");
        List<String> result = new ArrayList<>(split.length);
        //使用标点符号切分
        for (String string : split) {
            result.addAll(Arrays.stream(string.split("[\\pP\\p{Punct}]")).toList());
        }
        return result;
    }
}
