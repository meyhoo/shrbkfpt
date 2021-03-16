package com.shrb.versionowner.aop;

import com.alibaba.fastjson.JSONObject;
import com.shrb.versionowner.entity.api.AbstractResponse;
import com.shrb.versionowner.entity.api.ApiExtendResponse;
import com.shrb.versionowner.enums.ErrorCodeEnums;
import com.shrb.versionowner.utils.WebHttpUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class ProcessApiResponseAspect {
    private static final Logger log = LoggerFactory.getLogger(ProcessApiResponseAspect.class);

    @Pointcut("@annotation(com.shrb.versionowner.annotataions.ApiRsp)")
    public void apiResponsePointcut(){

    }

    @Around("apiResponsePointcut()")
    public Object aroundApiRsp(ProceedingJoinPoint pjp) {
        AbstractResponse apiResponse;
        String name = pjp.getSignature().getName();
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            JSONObject params = WebHttpUtils.getHttpRequestJson(request);
            String requestUrl = request.getMethod() + " " + request.getRequestURL();
            log.info("[服务器收到请求：{}] ==>[请求报文：{}] ==>[{}方法开始执行]", requestUrl, params.toJSONString(), name);
            apiResponse = (AbstractResponse)pjp.proceed();
            if(StringUtils.isEmpty(apiResponse.getErrorCode())) {
                apiResponse.setErrorCode(ErrorCodeEnums.success.getCode());
            }
            if(StringUtils.isEmpty(apiResponse.getErrorMsg())) {
                apiResponse.setErrorMsg(ErrorCodeEnums.success.getMsg());
            }
            log.info("[{}方法执行结束] ==>[返回响应报文：{}]", name, apiResponse.toString());
        } catch (Throwable throwable) {
            apiResponse = new ApiExtendResponse();
            apiResponse.setErrorCode(ErrorCodeEnums.failure.getCode());
            apiResponse.setErrorMsg(ErrorCodeEnums.failure.getMsg());
            log.error("[{}方法出现异常...] ==>[返回响应报文：{}]", name, apiResponse.toString(), throwable);
            return apiResponse;
        }
        return apiResponse;
    }


}
