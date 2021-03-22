package com.shrb.versionowner.entity.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Configuration {
    @Value("${configuration.filepath.userinfo}")
    private String userInfoFilePath;

    @Value("${configuration.filepath.systemUrlInfo}")
    private String systemUrlInfoFilePath;

    @Value("${shiro.session.timeout}")
    private Long shiroSessionTimeout;

    @Value("${configuration.filepath.sqlTemplates.basePath}")
    private String sqlTemplatesBasePath;

    @Value("${configuration.filepath.sqlTemplateGroup.basePath}")
    private String sqlTemplateGroupBasePath;

    @Value("${configuration.filepath.developer.version.basePath}")
    private String developerVersionBasePath;

    @Value("${configuration.filepath.administrator.version.basePath}")
    private String administratorVersionBasePath;

    public String getUserInfoFilePath() {
        return userInfoFilePath;
    }

    public Long getShiroSessionTimeout() {
        return shiroSessionTimeout;
    }

    public String getSqlTemplatesBasePath() {
        return sqlTemplatesBasePath;
    }

    public String getSqlTemplateGroupBasePath() {
        return sqlTemplateGroupBasePath;
    }

    public String getSystemUrlInfoFilePath() {
        return systemUrlInfoFilePath;
    }

    public String getDeveloperVersionBasePath() {
        return developerVersionBasePath;
    }

    public String getAdministratorVersionBasePath() {
        return administratorVersionBasePath;
    }
}
