package com.luna.logclient;


import com.luna.logclient.aop.LogAOP;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.ComponentScan;



/**
 * @author 文轩
 */
@AutoConfiguration
@ConditionalOnMissingBean(LogAOP.class)
@ComponentScan("com.luna.logclient")
public class LogClientAutoConfig {


}
