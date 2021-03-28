package com.shrb.versionowner.service;

import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.utils.MyFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 合并 /前端 目录
 */
@Component
public class FrontEndDirMergeVersionProcessor implements IMergeVersionProcessor {

    @Autowired
    private Configuration configuration;

    @Override
    public void invoke(String versionId, List<String> committerNameList) throws Exception {
        String adminDirPath = configuration.getAdministratorVersionBasePath() + versionId + "/versionContent/" + versionId + "/前端/";
        MyFileUtils.createFile(adminDirPath);
        for (String currentCommitterName : committerNameList) {
            String devDirPath = configuration.getDeveloperVersionBasePath() + currentCommitterName + "/" + versionId + "/versionContent/" + versionId + "/前端/";
            File dir = new File(devDirPath);
            if (!dir.exists()) {
                continue;
            }
            MyFileUtils.copyFolder(devDirPath, adminDirPath);
        }
    }

}
