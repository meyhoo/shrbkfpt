package com.shrb.versionowner.service;

import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.utils.MyFileUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.tomcat.util.security.MD5Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

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

    public void prepareSqlTemplateDir() throws Exception {
        String basePath = configuration.getSqlTemplatesBasePath();
        MyFileUtils.createFile(basePath);
    }

}
