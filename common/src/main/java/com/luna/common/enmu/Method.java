package com.luna.common.enmu;

public enum Method {


        NONE("无"),
        //搜索
        SEARCH("搜索"),

        //查询
        QUERY("查询"),
        ;

        private final String value;
        Method(String s) {
            this.value = s;
        }

        public String getValue() {
                return value;
        }
}
