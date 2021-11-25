package com.shrb.versionowner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

//aaa
@SpringBootApplication
@EnableAspectJAutoProxy
@ComponentScan(basePackages={
        "com.shrb.versionowner.controller",
        "com.shrb.versionowner.service",
        "com.shrb.versionowner.security.shiro",
        "com.shrb.versionowner.entity.configuration",
        "com.shrb.versionowner.aop"},lazyInit=true)
public class VersionownerApplication {

    public static void main(String[] args) {
        SpringApplication.run(VersionownerApplication.class, args);
    }

}
