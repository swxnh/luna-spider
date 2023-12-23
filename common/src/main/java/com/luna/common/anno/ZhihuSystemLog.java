package com.luna.common.anno;


import com.luna.common.enmu.Method;
import com.luna.common.enmu.Module;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 文轩
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ZhihuSystemLog {
    String value() default "";

    Method method() default Method.NONE;

    Module model() default Module.NONE;
}
