package com.luna.common.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.javapoet.MethodSpec;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResult<T> {

    private Integer code;
    private String msg;
    private T data;

//    /**
//     * 固定返回常量值
//     * 添加常量时请在常量初始化区的静态代码块内使用内部枚举类进行初始化
//      */
    public static final ResponseResult<Void> LOGIN_SUCCESS = new ResponseResult<>(ResponseCodeEnum.LOGIN_SUCCESS);
    public static final ResponseResult<Void> LOGIN_FAIL = new ResponseResult<>(ResponseCodeEnum.LOGIN_FAIL);
    public static final ResponseResult<Void> NOT_LOGIN = new ResponseResult<>(ResponseCodeEnum.NOT_LOGIN);
    public static final ResponseResult<Void> NO_AUTH = new ResponseResult<>(ResponseCodeEnum.NO_AUTH);
    public static final ResponseResult<Void> LOGOUT_SUCCESS = new ResponseResult<>(ResponseCodeEnum.LOGOUT_SUCCESS);
    public static final ResponseResult<?> DELETE_SUCCESS = new ResponseResult<>(ResponseCodeEnum.DELETE_SUCCESS);
    public static final ResponseResult<?> DELETE_FAIL = new ResponseResult<>(ResponseCodeEnum.DELETE_FAIL);

    public static <T> ResponseResult<T> message(Integer code,String msg,T t) {
        return new ResponseResult<>(code, msg,t);
    }

    public static <T> ResponseResult<T> ok(T t,String msg) {
        return new ResponseResult<>(200, msg,t);
    }

    public static <T> ResponseResult<T> ok(T t,Integer code) {
        return new ResponseResult<>(code, "success",t);
    }

    public static <T> ResponseResult<T> ok(T t,String msg,Integer code) {
        return new ResponseResult<>(code, msg,t);
    }

    public static <T> ResponseResult<T> ok() {
        return new ResponseResult<>(200, "success", null);
    }


    public static <T> ResponseResult<T> error(String msg,T t) {
        return new ResponseResult<>(400,msg, t);
    }

    public static <T> ResponseResult<T> error(String msg) {
        return new ResponseResult<>(400,msg, null);
    }

    public static <T> ResponseResult<T> error(Integer code,String msg) {
        return new ResponseResult<>(code,msg, null);
    }

    public static <T> ResponseResult<T> ok(T t) {
        return new ResponseResult<>(200, "success",t);
    }

//    public ResponseResult<T> ok(T t) {
//        return new ResponseResult<>(200, "success",t);
//    }
//
//    public ResponseResult<T> ok(String msg,T t) {
//        return new ResponseResult<>(200,msg, t);
//    }
//
//    public ResponseResult<T> ok() {
//        return new ResponseResult<>(400, "success", null);
//    }

//    public ResponseResult<T> errorStart(String msg,T t) {
//        return new ResponseResult<>(400,msg, t);
//    }

//    public ResponseResult<T> errorStart(String msg) {
//        return new ResponseResult<>(400,msg, null);
//    }

    public ResponseResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseResult(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public ResponseResult(ResponseResult<Void> responseResult, T data) {
        this.code = responseResult.getCode();
        this.msg = responseResult.getMsg();
        this.data = data;
    }

    private ResponseResult(ResponseCodeEnum responseCodeEnum) {
        this.code = responseCodeEnum.getCode();
        this.msg = responseCodeEnum.getMsg();
    }

    private ResponseResult(ResponseCodeEnum responseCodeEnum, T data) {
        this.code = responseCodeEnum.getCode();
        this.msg = responseCodeEnum.getMsg();
        this.data = data;
    }


    private enum ResponseCodeEnum {

        /**
         * 200 成功
         * 301 用户名或密码错误
         * 302 未登录，请登陆后再访问
         * 403 您没有相应权限
         * 504 添加失败
         * 604 删除失败
         * 704 修改失败
         * 804 发送失败
         * 907 输入不能为空
         * 600 系统异常，请联系管理员
         * 1001 参数错误
         * 4399 操作错误
         */
        LOGIN_FAIL(301, "用户名或密码错误"),
        NOT_LOGIN(302,"未登录，请登陆后再访问"),
        NO_AUTH(403,"您没有相应权限"),
        ADD_FAIL(504, "添加失败"),
        DELETE_FAIL(604,"删除失败"),
        SUCCESS(200),
        LOGIN_SUCCESS(200, "登陆成功"),
        LOGOUT_SUCCESS(200,"退出成功"),
        ADD_SUCCESS(200,"添加成功"),
        DELETE_SUCCESS(200,"删除成功"),
        UPDATE_SUCCESS(200,"修改成功"),
        UPDATE_FAIL(704,"修改失败"),
        OPERATION_SUCCESS(200,"操作成功"),
        OPERATION_ERROR(4399,"操作错误"),
        SEND_SUCCESS(200,"发送成功"),
        SEND_FAIL(804,"发送失败"),
        EMPTY_INPUT(907,"输入不能为空"),
        SYSTEM_ERROR(600,"系统异常，请联系管理员"),
        PARAM_ERROR(1001,"参数错误");

        private final Integer code;
        private String msg;

        ResponseCodeEnum(Integer code){
            this.code = code;
        }

        ResponseCodeEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

    }

}
