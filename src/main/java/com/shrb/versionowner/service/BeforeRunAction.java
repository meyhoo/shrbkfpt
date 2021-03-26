package com.shrb.versionowner.service;

import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.utils.MyFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class BeforeRunAction {
    private static final Logger log = LoggerFactory.getLogger(BeforeRunAction.class);

    @Autowired
    private Configuration configuration;

    public void prepareUserInfoFile() throws Exception {
        String path = configuration.getUserInfoFilePath();
        if (!MyFileUtils.judgeFileExists(path)) {
            List<String> lines = new ArrayList<>();
            String password = "123456";
            String passwordMd5 = DigestUtils.md5DigestAsHex(password.getBytes());
            lines.add("admin|" + passwordMd5 + "|1");
            if (log.isInfoEnabled()) {
                log.info("开始生成文件：{}", path);
            }
            MyFileUtils.writeLinesToFileFromHead(lines, path, "utf-8");
        }
    }

    public void prepareUnzipDevTemplate() throws Exception {
        //模板源文件zip
        String srcPath = configuration.getDevTemplatePath();
        //解压到目标路径
        String destPath = configuration.getDevTemplateUnzipBasePath();
        //解压后的一级目录
        String unzipDirPath = configuration.getDevTemplateUnzipDirPath();
        File file = new File(unzipDirPath);
        if (!file.exists()) {
            log.info("开始解压：{}，目标路径：{}", srcPath, destPath);
            MyFileUtils.decompressZip(srcPath, destPath);
        }
    }

    public void prepareUnzipAdminTemplate() throws Exception {
        //模板源文件zip
        String srcPath = configuration.getAdminTemplatePath();
        //解压到目标路径
        String destPath = configuration.getAdminTemplateUnzipBasePath();
        //解压后的一级目录
        String unzipDirPath = configuration.getAdminTemplateUnzipDirPath();
        File file = new File(unzipDirPath);
        if (!file.exists()) {
            log.info("开始解压：{}，目标路径：{}", srcPath, destPath);
            MyFileUtils.decompressZip(srcPath, destPath);
        }
    }

    public void prepareSystemUrlInfoFile() throws Exception {
        String path = configuration.getSystemUrlInfoFilePath();
        if (!MyFileUtils.judgeFileExists(path)) {
            MyFileUtils.createFile(path);
        }
    }

    public void prepareSqlTemplateDir() throws Exception {
        String basePath = configuration.getSqlTemplatesBasePath();
        MyFileUtils.createFile(basePath);
    }

    public void prepareSqlTemplateGroupDir() throws Exception {
        String basePath = configuration.getSqlTemplateGroupBasePath();
        MyFileUtils.createFile(basePath);
    }

    public void prepareDeveloperVersionDir() throws Exception {
        String basePath = configuration.getDeveloperVersionBasePath();
        MyFileUtils.createFile(basePath);
    }

    public void prepareAdministratorVersionDir() throws Exception {
        String basePath = configuration.getAdministratorVersionBasePath();
        MyFileUtils.createFile(basePath);
    }

}
