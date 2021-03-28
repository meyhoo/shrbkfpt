package com.shrb.versionowner.service;

import com.shrb.versionowner.entity.business.DeveloperVersion;
import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.utils.MyFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 合并 /DEVPORTAL/DB/SCRIPT/portal.sql
 * 合并 /DEVPORTAL/DB/SCRIPT/portalmanager.sql
 * 合并 /DEVPORTAL/DB/SCRIPT/rollbackportal.sql
 * 合并 /DEVPORTAL/DB/SCRIPT/rollbackportalmanager.sql
 */
@Component
public class PortalSqlMergeVersionProcessor implements IMergeVersionProcessor {

    private static final Logger log = LoggerFactory.getLogger(PortalSqlMergeVersionProcessor.class);

    @Autowired
    private Configuration configuration;

    @Autowired
    private RuntimeCacheService runtimeCacheService;

    @Override
    public void invoke(String versionId, List<String> committerNameList) throws Exception {
        String administratorVersionBasePath = configuration.getAdministratorVersionBasePath();
        String developerVersionBasePath = configuration.getDeveloperVersionBasePath();
        String adminDirPath = administratorVersionBasePath + versionId + "/versionContent/" + versionId + "/DEVPORTAL/DB/SCRIPT/";
        String adminPortalSqlFilePath = adminDirPath + "portal.sql";
        String adminRollbackPortalSqlFilePath = adminDirPath + "rollbackportal.sql";
        String adminPortalManagerSqlFilePath = adminDirPath + "portalmanager.sql";
        String adminRollbackPortalManagerSqlFilePath = adminDirPath + "rollbackportalmanager.sql";
        List<DeveloperVersion> developerVersionList = new ArrayList<>();
        for (String currentCommitterName : committerNameList) {
            DeveloperVersion developerVersion = runtimeCacheService.getDeveloperVersion(currentCommitterName, versionId);
            developerVersionList.add(developerVersion);
        }
        Collections.sort(developerVersionList, new Comparator<DeveloperVersion>() {
            @Override
            public int compare(DeveloperVersion d1, DeveloperVersion d2) {
                int i = d1.getPriority() - d2.getPriority();
                return i>0?1:-1;
            }
        });
        List<String> portalRunSqlLines = new ArrayList<>();
        List<String> portalRollbackSqlLines = new ArrayList<>();
        List<String> portalManagerRunSqlLines = new ArrayList<>();
        List<String> portalManagerRollbackSqlLines = new ArrayList<>();
        for (int i = 0; i < developerVersionList.size(); i++) {
            DeveloperVersion developerVersion = developerVersionList.get(i);
            String currentCommitterName = developerVersion.getUserId();
            portalRunSqlLines.add("# start by " + currentCommitterName);
            portalManagerRunSqlLines.add("# start by " + currentCommitterName);
            String devDirPath = developerVersionBasePath + currentCommitterName + "/" + versionId + "/versionContent/" + versionId + "/DEVPORTAL/DB/SCRIPT/";
            String devPortalRunSqlFilePath = devDirPath + "portal.sql";
            String devPortalManagerRunSqlFilePath = devDirPath + "portalmanager.sql";
            File devPortalRunSqlFile = new File(devPortalRunSqlFilePath);
            File devPortalManagerRunSqlFile = new File(devPortalManagerRunSqlFilePath);
            if (devPortalRunSqlFile.exists()) {
                List<String> currentPortalRunSqlLines = MyFileUtils.readFileAllLines(devPortalRunSqlFilePath, "utf-8");
                portalRunSqlLines.addAll(currentPortalRunSqlLines);
            }
            if (devPortalManagerRunSqlFile.exists()) {
                List<String> currentPortalManagerRunSqlLines = MyFileUtils.readFileAllLines(devPortalManagerRunSqlFilePath, "utf-8");
                portalManagerRunSqlLines.addAll(currentPortalManagerRunSqlLines);
            }
            portalRunSqlLines.add("# end by " + currentCommitterName + "\n\n");
            portalManagerRunSqlLines.add("# end by " + currentCommitterName + "\n\n");
        }
        for (int i = developerVersionList.size()-1; i>=0; i--) {
            DeveloperVersion developerVersion = developerVersionList.get(i);
            String currentCommitterName = developerVersion.getUserId();
            portalRollbackSqlLines.add("# start by " + currentCommitterName);
            portalManagerRollbackSqlLines.add("# start by " + currentCommitterName);
            String devDirPath = developerVersionBasePath + currentCommitterName + "/" + versionId + "/versionContent/" + versionId + "/DEVPORTAL/DB/SCRIPT/";
            String devPortalRollbackSqlFilePath = devDirPath + "rollbackportal.sql";
            String devPortalManagerRollbackSqlFilePath = devDirPath + "rollbackportalmanager.sql";
            File devPortalRollbackSqlFile = new File(devPortalRollbackSqlFilePath);
            File devPortalManagerRollbackSqlFile = new File(devPortalManagerRollbackSqlFilePath);
            if (devPortalRollbackSqlFile.exists()) {
                List<String> currentPortalRollbackSqlLines = MyFileUtils.readFileAllLines(devPortalRollbackSqlFilePath, "utf-8");
                portalRollbackSqlLines.addAll(currentPortalRollbackSqlLines);
            }
            if (devPortalManagerRollbackSqlFile.exists()) {
                List<String> currentPortalManagerRollbackSqlLines = MyFileUtils.readFileAllLines(devPortalManagerRollbackSqlFilePath, "utf-8");
                portalManagerRollbackSqlLines.addAll(currentPortalManagerRollbackSqlLines);
            }
            portalRollbackSqlLines.add("# end by " + currentCommitterName + "\n\n");
            portalManagerRollbackSqlLines.add("# end by " + currentCommitterName + "\n\n");
        }
        MyFileUtils.writeLinesToFileFromHead(portalRunSqlLines, adminPortalSqlFilePath, "utf-8");
        MyFileUtils.writeLinesToFileFromHead(portalRollbackSqlLines, adminRollbackPortalSqlFilePath, "utf-8");
        MyFileUtils.writeLinesToFileFromHead(portalManagerRunSqlLines, adminPortalManagerSqlFilePath, "utf-8");
        MyFileUtils.writeLinesToFileFromHead(portalManagerRollbackSqlLines, adminRollbackPortalManagerSqlFilePath, "utf-8");
    }

}
