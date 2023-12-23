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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author 文轩
 */
@Slf4j
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

        TypeFactory typeFactory = objectMapper.getTypeFactory();
        //对结果进行转换,使用jackjson转换
        ResponseResult<?> result;
        try {
            result = objectMapper.readValue(response.body().asInputStream(),
                    typeFactory.constructParametricType(ResponseResult.class, typeFactory.constructType(type)));
        } catch (Exception e) {
            //如果转换失败，则直接抛出异常 IllegalArgumentException
            throw new IllegalArgumentException("接口返回数据转换失败", e);
        }

        if (type instanceof ResponseResult) {
            return result;
        }

        //如果返回错误，且为内部错误，则直接抛出异常
        if (result.getCode() != 200) {
                throw new DecodeException(response.status(), "接口返回错误：" + result.getMsg(), response.request());
        }
        return result.getData();
    }


}
