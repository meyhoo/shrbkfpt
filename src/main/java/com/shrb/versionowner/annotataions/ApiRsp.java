package com.shrb.versionowner.annotataions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
/**
 * 处理响应类型为ApiResponse对象的接口
 */
public @interface ApiRsp {
}
