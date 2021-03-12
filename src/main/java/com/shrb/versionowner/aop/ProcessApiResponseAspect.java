package com.shrb.versionowner.aop;

import com.shrb.versionowner.entity.api.ApiResponse;
import com.shrb.versionowner.enums.ErrorCodeEnums;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ProcessApiResponseAspect {
    private static final Logger log = LoggerFactory.getLogger(ProcessApiResponseAspect.class);

    @Pointcut("@annotation(com.shrb.versionowner.annotataions.ApiRsp)")
    public void apiResponsePointcut(){

    }

    @Around("apiResponsePointcut()")
    public Object aroundApiRsp(ProceedingJoinPoint pjp) {
        ApiResponse apiResponse;
        String name = pjp.getSignature().getName();
        log.info("{}方法开始执行", name);
        try {
            apiResponse = (ApiResponse)pjp.proceed();
        } catch (Throwable throwable) {
            apiResponse = new ApiResponse();
            apiResponse.setErrorCode(ErrorCodeEnums.failure.getCode());
            apiResponse.setErrorMsg(ErrorCodeEnums.failure.getMsg());
            log.error("{}方法出现异常...", name, throwable);
        }
        return apiResponse;
    }


}
