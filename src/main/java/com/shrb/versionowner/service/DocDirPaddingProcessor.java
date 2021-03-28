package com.shrb.versionowner.service;

import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.utils.MyFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 补齐 /DOC/版本信息文件 目录
 * 补齐 /DOC/版本回退手册.txt
 */
@Component
public class DocDirPaddingProcessor implements IMergeVersionProcessor {

    @Autowired
    private Configuration configuration;

    @Override
    public void invoke(String versionId, List<String> committerNameList) throws Exception {
        String administratorVersionBasePath = configuration.getAdministratorVersionBasePath();
        String srcTemplateBasePath = configuration.getAdminTemplateUnzipDirPath();
        String adminDirPath = administratorVersionBasePath + versionId + "/versionContent/" + versionId + "/DOC/";
        MyFileUtils.createFile(adminDirPath);
        String versionRollbackBookFilePath = adminDirPath + "版本回退手册.txt";
        String srcTemplateDirPath = srcTemplateBasePath + "/DOC/";
        String srcVersionRollbackBookFilePath = srcTemplateDirPath + "版本回退手册.txt";
        String srcVersionInfoDirPath = srcTemplateDirPath + "版本信息文件/";
        MyFileUtils.copyFile(srcVersionRollbackBookFilePath, versionRollbackBookFilePath);
        MyFileUtils.copyDir(srcVersionInfoDirPath, adminDirPath);
    }
}
