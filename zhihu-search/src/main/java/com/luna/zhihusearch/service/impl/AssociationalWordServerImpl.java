package com.luna.zhihusearch.service.impl;

import com.luna.common.vo.zhihusearch.AssociationalWordVO;
import com.luna.zhihusearch.mapper.AssociationalWordMapper;
import com.luna.zhihusearch.service.AssociationalWordServer;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author 文轩
 */
//@Service
public class AssociationalWordServerImpl implements AssociationalWordServer {

    private static List<String> AssociationalWordList;

    private final StringRedisTemplate stringRedisTemplate;

    private final AssociationalWordMapper associationalWordMapper;

    public AssociationalWordServerImpl(StringRedisTemplate stringRedisTemplate,
                                       AssociationalWordMapper associationalWordMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.associationalWordMapper = associationalWordMapper;
    }


    @PostConstruct
    public void init() {
        //初始化关联词
        AssociationalWordList = associationalWordMapper.selectAllAssociationalWord();
    }


    /**
     * 获取关联词
     *
     * @param keyword
     */
    @Override
    public List<AssociationalWordVO> getAssociationalWord(String keyword) {

        if (keyword == null || keyword.length() == 0) {
            return null;
        }
       //是否包含英文
        if (keyword.matches(".*[a-zA-Z]+.*")) {
            //获取相对长度
            int relativeLength = getRelativeLength(keyword);
            if (relativeLength > 1) {
                return getMultipleAssociationalWord(keyword);
            }
        }
        if (keyword.length() > 2) {
            return getMultipleAssociationalWord(keyword);
        }

        //随机取10个
        List<String> stringList = stringRedisTemplate.opsForZSet().randomMembers("word::"+keyword, 10);

        List<AssociationalWordVO> result = new ArrayList<>(10);

        for (String string : stringList) {
            long id = Long.parseLong(string);
            String associationalWord = AssociationalWordList.get(Math.toIntExact(id) - 1);
            AssociationalWordVO associationalWordObj = new AssociationalWordVO();
            associationalWordObj.setValue(associationalWord);
            associationalWordObj.setId(id);
            result.add(associationalWordObj);
        }

        return result;

    }

    private List<AssociationalWordVO> getMultipleAssociationalWord(String keyword) {
        //切割
        List<String> split = split(keyword);
        if (split.isEmpty()) {
            return new ArrayList<>();
        }
        //加上前缀
        for (int i = 0; i < split.size(); i++) {
            split.set(i, "word::" + split.get(i));
        }

        //获取关联词,取并集
        List<ZSetOperations.TypedTuple<String>> typedTupleSet = new ArrayList<>();
        //并行流处理
        split.parallelStream().forEach(s -> {
            List<ZSetOperations.TypedTuple<String>> rangeWithScores = stringRedisTemplate.opsForZSet().randomMembersWithScore(s, 100);
            if (rangeWithScores != null) {
                typedTupleSet.addAll(rangeWithScores);
            }
        });
        if (typedTupleSet.isEmpty()) {
            return new ArrayList<>();
        }
        List<AssociationalWordVO> associationalWordList = new ArrayList<>(typedTupleSet.size());
        //如果小于等于10,直接返回
        if (typedTupleSet.size() <= 10) {
            //id去重
            Set<Long> ids = new HashSet<>(typedTupleSet.size());
            for (ZSetOperations.TypedTuple<String> stringTypedTuple : typedTupleSet) {
                long id = Long.parseLong(stringTypedTuple.getValue());
                if (ids.contains(id)) {
                    continue;
                }
                ids.add(id);
                String associationalWord = AssociationalWordList.get(Math.toIntExact(id) - 1);
                AssociationalWordVO associationalWordObj = new AssociationalWordVO();
                associationalWordObj.setValue(associationalWord);
                associationalWordObj.setId(id);
                associationalWordList.add(associationalWordObj);
            }
            return associationalWordList;
        }
        //如果大于10,则进行排序
        Map<Long,AssociationalWordVO> map = new HashMap<>(typedTupleSet.size());
        for (ZSetOperations.TypedTuple<String> stringTypedTuple : typedTupleSet) {
            String value = stringTypedTuple.getValue();
            if (value == null) {
                continue;
            }
            long id = Long.parseLong(value);
            if (map.containsKey(id)) {
                //重复命中,分数相加,并加1
                AssociationalWordVO associationalWordVO = map.get(id);
                associationalWordVO.setScore(associationalWordVO.getScore() + stringTypedTuple.getScore() + 1);
                continue;
            }
            //获取关联词
            String associationalWord = AssociationalWordList.get(Math.toIntExact(id) - 1);
            //获取分数
            double score = stringTypedTuple.getScore();
            //是否包含关键词
            if (associationalWord.contains(keyword)) {
                //如果分数小于1,使分数+10
                if (score < 10) {
                    score += 10;
                }else {
                    //如果分数大于10,则乘以1.5
                    score *= 1.5;
                }
            }
            AssociationalWordVO associationalWordObj = new AssociationalWordVO();
            associationalWordObj.setValue(associationalWord);
            associationalWordObj.setScore(score);
            associationalWordObj.setId(id);
            associationalWordList.add(associationalWordObj);
            map.put(id,associationalWordObj);
        }
        //排序
        Collections.sort(associationalWordList);
        //取前10个
        return associationalWordList.subList(0, 10);

    }


    /**
     * 获取相对长度
     *
     * @param word
     */
    public int getRelativeLength(String word) {
        //连续的字母算一个长度,一个汉字算一个长度
        int length = 0;
        char[] charArray = word.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            //判断是否是字母
            if (Character.isLowerCase(c) || Character.isUpperCase(c)) {
                //判断是否是最后一个字母
                if (i == charArray.length - 1) {
                    length++;
                } else {
                    //判断下一个是否是字母
                    if (!Character.isLetter(charArray[i + 1])) {
                        length++;
                    }
                }
            } else {
                length++;
            }
        }
        return length;
    }


    /**
     * 切割
     *
     * @param word
     */
    public List<String> split(String word) {
        List<String> split = new ArrayList<>();
        //使用空格切割
        String[] splitSpace = word.split(" ");
        for (String space : splitSpace) {
            //使用标点符号切割
            String[] splitPunctuation = space.split("[\\pP\\p{Punct}]");
            //连续的字母或连续的数字或2个汉字算一个词,不足两个的算一个词
            for (String punctuation : splitPunctuation) {
                char[] charArray = punctuation.toCharArray();
                StringBuilder sbLetter = new StringBuilder();
                StringBuilder sbNumber = new StringBuilder();
                StringBuilder sbChinese = new StringBuilder();
                for (int i = 0; i < charArray.length; i++) {
                    char c = charArray[i];
                    //判断是否是字母
                    if (Character.isLowerCase(c) || Character.isUpperCase(c)) {
                        sbLetter.append(c);
                        //判断是否是最后一个字母
                        if (i == charArray.length - 1) {
                            split.add(sbLetter.toString());
                        } else {
                            //判断下一个是否是字母
                            if (!Character.isUpperCase(charArray[i + 1]) && !Character.isLowerCase(charArray[i + 1])) {
                                split.add(sbLetter.toString());
                                sbLetter = new StringBuilder();
                            }
                        }
                    } else if (Character.isDigit(c)) {
                        sbNumber.append(c);
                        //判断是否是最后一个数字
                        if (i == charArray.length - 1) {
                            split.add(sbNumber.toString());
                        } else {
                            //判断下一个是否是数字
                            if (!Character.isDigit(charArray[i + 1])) {
                                split.add(sbNumber.toString());
                                sbNumber = new StringBuilder();
                            }
                        }
                    } else {
                        sbChinese.append(c);
                        //判断是否是最后一个字
                        if (i == charArray.length - 1) {
                            split.add(sbChinese.toString());
                        } else if (Character.isUpperCase(charArray[i + 1]) || Character.isLowerCase(charArray[i + 1]) || Character.isDigit(charArray[i + 1])) {
                            //如果下一个是字母或数字,则终止
                            split.add(sbChinese.toString());
                            sbChinese = new StringBuilder();
                        } else if (sbChinese.length() > 1) {
                            //如果下一个是汉字,则判断长度是否大于1
                            split.add(sbChinese.toString());
                            sbChinese = new StringBuilder();
                        }


                    }
                }
            }
        }
        return split;
    }

//    public static void main(String[] args) {
//        AssociationalWordServerImpl associationalWordServer = new AssociationalWordServerImpl(null, null);
//        List<String> split = associationalWordServer.split("反抗现代java");
//        System.out.println(split);
//    }

}
