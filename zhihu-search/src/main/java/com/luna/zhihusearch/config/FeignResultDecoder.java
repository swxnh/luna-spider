package com.luna.zhihusearch.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.luna.common.utils.ResponseResult;
import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author 文轩
 */

public class FeignResultDecoder implements Decoder {


    private final ObjectMapper objectMapper;

    public FeignResultDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        if (response.body() == null) {
            throw new DecodeException(response.status(), "没有返回有效的数据", response.request());
        }
        String bodyStr = Util.toString(response.body().asReader(Util.UTF_8));
        //对结果进行转换,使用jackjson转换
        ResponseResult<?> result;
        try {
            JavaType javaType = TypeFactory.defaultInstance().constructType(type);
            result = objectMapper.readValue(bodyStr, javaType);
        } catch (Exception e) {
            //如果转换失败，则直接抛出异常 IllegalArgumentException
            throw new IllegalArgumentException("接口返回数据转换失败", e);
        }


        //如果返回错误，且为内部错误，则直接抛出异常
        if (result.getCode() != 200) {
                throw new DecodeException(response.status(), "接口返回错误：" + result.getMsg(), response.request());
        }
        return result.getData();
    }

}
