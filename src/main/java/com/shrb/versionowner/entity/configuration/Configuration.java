package com.shrb.versionowner.entity.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

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

    @Value("${configuration.filepath.administrator.version.bak.basePath}")
    private String administratorVersionBakBasePath;

    @Value("${configuration.filepath.dev.origin.template.basePath}")
    private String devTemplatePath;

    @Value("${configuration.filepath.admin.origin.template.basePath}")
    private String adminTemplatePath;

    @Value("${configuration.filepath.devtemplate.unzip.basePath}")
    private String devTemplateUnzipBasePath;

    @Value("${configuration.filepath.devtemplate.unzip.dirPath}")
    private String devTemplateUnzipDirPath;

    @Value("${configuration.filepath.admintemplate.unzip.basePath}")
    private String adminTemplateUnzipBasePath;

    @Value("${configuration.filepath.admintemplate.unzip.dirPath}")
    private String adminTemplateUnzipDirPath;

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

    public String getAdministratorVersionBakBasePath() {
        return administratorVersionBakBasePath;
    }

    public String getDevTemplatePath() throws FileNotFoundException {
        File path = new File(ResourceUtils.getURL("classpath:").getPath());
        return path.getAbsolutePath() + devTemplatePath;
    }

    public String getAdminTemplatePath() throws FileNotFoundException {
        File path = new File(ResourceUtils.getURL("classpath:").getPath());
        return path.getAbsolutePath() + adminTemplatePath;
    }

    public String getDevTemplateUnzipBasePath() {
        return devTemplateUnzipBasePath;
    }

    public String getDevTemplateUnzipDirPath() {
        return devTemplateUnzipDirPath;
    }

    public String getAdminTemplateUnzipBasePath() {
        return adminTemplateUnzipBasePath;
    }

    public String getAdminTemplateUnzipDirPath() {
        return adminTemplateUnzipDirPath;
    }
}
