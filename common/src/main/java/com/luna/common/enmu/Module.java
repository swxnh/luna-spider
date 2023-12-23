package com.luna.common.enmu;

/**
 * @author 文轩
 */

public enum Module {

    NONE("无"),
    //关联词
    ASSOCIATIONAL_WORD("关联词"),

    //专栏文章
    ARTICLE("专栏文章"),
    ;


    private final String value;
    Module(String s) {
        this.value = s;
    }

    public String getValue() {
            return value;
    }
}
