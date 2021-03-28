package com.shrb.versionowner.service;

import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.utils.MyFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 补齐 /SMARTESB/DB/backup.sql 、rollback.sql 、run.sql 、SC.sql 、SX.sql 、UAT.sql 、ZSC.sql
 */
@Component
public class AutoSqlPaddingProcessor implements IMergeVersionProcessor {

    @Autowired
    private Configuration configuration;

    @Override
    public void invoke(String versionId, List<String> committerNameList) throws Exception {
        String administratorVersionBasePath = configuration.getAdministratorVersionBasePath();
        String srcTemplateBasePath = configuration.getAdminTemplateUnzipDirPath();
        String adminDirPath = administratorVersionBasePath + versionId + "/versionContent/" + versionId + "/SMARTESB/DB/";
        MyFileUtils.createFile(adminDirPath);
        String backupSqlFilePath = adminDirPath + "backup.sql";
        String rollbackSqlFilePath = adminDirPath + "rollback.sql";
        String runSqlFilePath = adminDirPath + "run.sql";
        String scSqlFilePath = adminDirPath + "SC.sql";
        String sxSqlFilePath = adminDirPath + "SX.sql";
        String uatSqlFilePath = adminDirPath + "UAT.sql";
        String zscSqlFilePath = adminDirPath + "ZSC.sql";
        String srcTemplateDirPath = srcTemplateBasePath + "/SMARTESB/DB/";
        String srcBackupSqlFilePath = srcTemplateDirPath + "backup.sql";
        String srcRollbackSqlFilePath = srcTemplateDirPath + "rollback.sql";
        String srcRunSqlFilePath = srcTemplateDirPath + "run.sql";
        String srcScSqlFilePath = srcTemplateDirPath + "SC.sql";
        String srcSxSqlFilePath = srcTemplateDirPath + "SX.sql";
        String srcUatSqlFilePath = srcTemplateDirPath + "UAT.sql";
        String srcZscSqlFilePath = srcTemplateDirPath + "ZSC.sql";
        MyFileUtils.copyFile(srcBackupSqlFilePath, backupSqlFilePath);
        MyFileUtils.copyFile(srcRollbackSqlFilePath, rollbackSqlFilePath);
        MyFileUtils.copyFile(srcRunSqlFilePath, runSqlFilePath);
        MyFileUtils.copyFile(srcScSqlFilePath, scSqlFilePath);
        MyFileUtils.copyFile(srcSxSqlFilePath, sxSqlFilePath);
        MyFileUtils.copyFile(srcUatSqlFilePath, uatSqlFilePath);
        MyFileUtils.copyFile(srcZscSqlFilePath, zscSqlFilePath);
    }

}
