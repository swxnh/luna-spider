package com.luna.logclient.aop;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.luna.common.anno.ZhihuSystemLog;
import com.luna.common.constants.KafkaConstants;
import com.luna.common.enmu.Method;
import com.luna.common.enmu.Module;
import com.luna.common.pojo.log.ZhihuLog;
import com.luna.logclient.kafka.MyKafkaTemplate;
import com.luna.logclient.properties.SnowFlakeProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

/**
 * @author 文轩
 */
@Aspect
@Component
public class LogAOP {


    private final MyKafkaTemplate<String,ZhihuLog> kafkaTemplate;

    private final Snowflake snowflake;


    public LogAOP(MyKafkaTemplate<String,ZhihuLog> kafkaTemplate,
                  Snowflake snowflake) {
        this.kafkaTemplate = kafkaTemplate;
        this.snowflake = snowflake;

    }

    @Pointcut(value = "@annotation(com.luna.common.anno.ZhihuSystemLog)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object res = joinPoint.proceed();
        long end = System.currentTimeMillis();
        saveLog(joinPoint, start,end);
        return res;
    }

    private void saveLog(ProceedingJoinPoint joinPoint, long start, long end) {
        //获取类上的注解
        ZhihuSystemLog classLog = joinPoint.getTarget().getClass().getAnnotation(ZhihuSystemLog.class);
        //获取方法上的注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        ZhihuSystemLog methodLog = signature.getMethod().getAnnotation(ZhihuSystemLog.class);
        String module;
        String method = Method.NONE.getValue();

        //如果类上的注解为空，就用方法上的注解
        if (classLog != null) {
            Module modelEnum = classLog.model();
            module = modelEnum == Module.NONE ? classLog.value() : modelEnum.getValue();
        }else {
            module = methodLog.model().getValue();
        }


        if (methodLog != null) {
            Method methodEnum = methodLog.method();
            method = methodEnum == Method.NONE ? methodLog.value() : methodEnum.getValue();
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        //获取参数
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        //获取入参名称和值
        Map<String,Object> params = new HashMap<>(args.length);
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof HttpServletRequest || args[i] instanceof HttpServletResponse) {
                continue;
            }
            params.put(parameterNames[i],args[i]);
        }


        String ip = getIpAddr(request);

        ZhihuLog log = new ZhihuLog();
        //TODO 获取用户信息
        log.setUserId(0L);



        //雪花算法生成id
        long id = snowflake.nextId();
        log.setId(id);
        log.setCreateTime(new Date());
        log.setIpAddr(ip);
        log.setMethod(method);
        log.setModule(module);
        log.setArgsJson(params);
        int time = (int) (end - start);
        log.setTime(time);
        log.setStartTime(new Date(start));
        log.setEndTime(new Date(end));
        log.setUseFunction(joinPoint.getTarget().getClass().getName()+"."+signature.getName());
        kafkaTemplate.send(KafkaConstants.LOG_TOPIC, log);
    }


    public String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if(ip ==null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip ==null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip ==null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if(ip !=null && ip.length() >15) {
            if(ip.indexOf(",")>0) {
                ip = ip.substring(0,ip.indexOf(","));
            }
        }
        return ip;
    }
}
