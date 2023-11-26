package com.luna.common.vo.zhihusearch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 文轩
 */
@Data
public class AssociationalWordVO implements Serializable, Comparable<AssociationalWordVO> {

    /**
     * id
     */
    private Long id;

    /**
     * 联想词
     */
    private String value;

    /**
     * 分数
     */
    @JsonIgnore
    private Double score;

    /**
     * 重写compareTo方法，用于排序
     */
    @Override
    public int compareTo(AssociationalWordVO o) {
        return o.getScore().compareTo(this.score);
    }
}
