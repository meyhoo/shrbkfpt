package com.shrb.versionowner.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ApplicationLaunchAspect {
    private static final Logger log = LoggerFactory.getLogger(ApplicationLaunchAspect.class);

    @Pointcut("execution(* com.shrb.versionowner.service.BeforeRunAction.prepare*(..))")
    public void prepareFunctionLogPointcut(){
    }

    @Before(value = "prepareFunctionLogPointcut()")
    public void beforePrepareFunction(JoinPoint jp) {
        String name = jp.getSignature().getName();
        if (log.isInfoEnabled()) {
            log.info("{}方法开始执行...", name);
        }

    }

    @After(value = "prepareFunctionLogPointcut()")
    public void afterPrepareFunction(JoinPoint jp) {
        String name = jp.getSignature().getName();
        if (log.isInfoEnabled()) {
            log.info("{}方法执行结束...", name);
        }
    }

    @AfterThrowing(value = "prepareFunctionLogPointcut()", throwing = "e")
    public void afterThrowing(JoinPoint jp, Exception e) {
        String name = jp.getSignature().getName();
        String exceptionMsg = e.getMessage();
        if (log.isInfoEnabled()) {
            log.info("{}方法抛异常了，异常是：{}", name, exceptionMsg);
        }
    }

}
