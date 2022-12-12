package com.imooc.api.Aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author Leo
 * @version 1.0
 * @description: TODO
 * @date 2022-12-12 17:25
 */
@Component
@Slf4j
@Aspect
public class ServiceLogAspect {


    @Around("execution(* com.imooc.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();
        //执行方法
        Object proceed = joinPoint.proceed();
        //获得方法名
        String method = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName();

        long stopTime = System.currentTimeMillis();

        long runTime = startTime - stopTime;

        if (runTime >= 3000){
            log.info(method + "执行时间太长了 执行时间:" + runTime);
        }else if ( runTime >= 2000){
            log.info(method + "执行时间还是有的长 执行时间:" + runTime);
        }else if (runTime > 1000){
            log.info(method + "还有提升的空间 执行时间:" + runTime);
        }else {
            log.info(method +  "该方法执行了 执行时间:" + runTime);
        }


        return proceed;
    }
}
