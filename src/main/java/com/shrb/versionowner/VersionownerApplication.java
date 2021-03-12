package com.shrb.versionowner;

import com.shrb.versionowner.service.BeforeRunAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@ComponentScan(basePackages={
        "com.shrb.versionowner.controller",
        "com.shrb.versionowner.service",
        "com.shrb.versionowner.security.shiro",
        "com.shrb.versionowner.entity.configuration",
        "com.shrb.versionowner.aop"},lazyInit=true)
public class VersionownerApplication implements CommandLineRunner {

    @Autowired
    private BeforeRunAction beforeRunAction;

    public static void main(String[] args) {
        SpringApplication.run(VersionownerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        beforeRunAction.prepareUserInfoFile();
    }

}
