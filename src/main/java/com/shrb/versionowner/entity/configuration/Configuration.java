package com.shrb.versionowner.entity.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Configuration {
    @Value("${configuration.filepath.userinfo}")
    private String userInfoFilePath;

    @Value("${shiro.session.timeout}")
    private Long shiroSessionTimeout;

    public String getUserInfoFilePath() {
        return userInfoFilePath;
    }

    public Long getShiroSessionTimeout() {
        return shiroSessionTimeout;
    }

}
