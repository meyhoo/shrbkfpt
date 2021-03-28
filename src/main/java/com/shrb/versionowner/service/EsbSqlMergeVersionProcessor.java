package com.shrb.versionowner.service;

import com.shrb.versionowner.entity.business.DeveloperVersion;
import com.shrb.versionowner.entity.business.SystemUrl;
import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.utils.MyFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

/**
 * 合并 /SMARTESB/DB/SCRIPT/run.sql
 *  并生成 protocol-prd.sql
 *        protocol-uat.sql
 *        protocol-zsc.sql
 *
 * 合并 /SMARTESB/DB/SCRIPT/rollback.sql
 *  并生成 rollback_protocol.sql
 */
@Component
public class EsbSqlMergeVersionProcessor implements IMergeVersionProcessor {

    private static final Logger log = LoggerFactory.getLogger(EsbSqlMergeVersionProcessor.class);

    @Autowired
    private Configuration configuration;

    @Autowired
    private RuntimeCacheService runtimeCacheService;

    @Override
    public void invoke(String versionId, List<String> committerNameList) throws Exception {
        //要考虑SQL先后顺序
        String administratorVersionBasePath = configuration.getAdministratorVersionBasePath();
        String developerVersionBasePath = configuration.getDeveloperVersionBasePath();
        String adminDirPath = administratorVersionBasePath + versionId + "/versionContent/" + versionId + "/SMARTESB/DB/SCRIPT/";
        String protocolPrdFilePath = adminDirPath + "protocol-prd.sql";
        String protocolUatFilePath = adminDirPath + "protocol-uat.sql";
        String protocolZscFilePath = adminDirPath + "protocol-zsc.sql";
        String rollbackProtocolFilePath = adminDirPath + "rollback_protocol.sql";
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
        List<String> runSqlLines = new ArrayList<>();
        for (int i = 0; i < developerVersionList.size(); i++) {
            DeveloperVersion developerVersion = developerVersionList.get(i);
            String currentCommitterName = developerVersion.getUserId();
            runSqlLines.add("# start by " + currentCommitterName);
            String devDirPath = developerVersionBasePath + currentCommitterName + "/" + versionId + "/versionContent/" + versionId + "/SMARTESB/DB/SCRIPT/";
            String devRunSqlFilePath = devDirPath + "run.sql";
            File file = new File(devRunSqlFilePath);
            if (!file.exists()) {
                runSqlLines.add("# end by " + currentCommitterName + "\n\n");
                continue;
            }
            List<String> currentRunSqlLines = MyFileUtils.readFileAllLines(devRunSqlFilePath, "utf-8");
            runSqlLines.addAll(currentRunSqlLines);
            runSqlLines.add("# end by " + currentCommitterName + "\n\n");
        }
        List<SystemUrl> systemUrlList = runtimeCacheService.getSystemUrlList();
        List<String> prdRunSqlLines = new ArrayList<>();
        List<String> uatRunSqlLines = new ArrayList<>();
        List<String> zscRunSqlLines = new ArrayList<>();
        for (String runSqlLine : runSqlLines) {
            String prdRunSqlLine = "";
            String uatRunSqlLine = "";
            String zscRunSqlLine = "";
            for (SystemUrl systemUrl : systemUrlList) {
                prdRunSqlLine = runSqlLine.replaceAll(systemUrl.getDevUrl(), systemUrl.getPrdUrl());
                prdRunSqlLines.add(prdRunSqlLine);
                uatRunSqlLine = runSqlLine.replaceAll(systemUrl.getDevUrl(), systemUrl.getTestUrl());
                uatRunSqlLines.add(uatRunSqlLine);
                zscRunSqlLine = runSqlLine.replaceAll(systemUrl.getDevUrl(), systemUrl.getZscUrl());
                zscRunSqlLines.add(zscRunSqlLine);
            }
        }
        MyFileUtils.writeLinesToFileFromHead(prdRunSqlLines, protocolPrdFilePath, "utf-8");
        MyFileUtils.writeLinesToFileFromHead(uatRunSqlLines, protocolUatFilePath, "utf-8");
        MyFileUtils.writeLinesToFileFromHead(zscRunSqlLines, protocolZscFilePath, "utf-8");

        List<String> rollbackSqlLines = new ArrayList<>();
        for (int i = developerVersionList.size()-1; i>=0; i--) {
            DeveloperVersion developerVersion = developerVersionList.get(i);
            String currentCommitterName = developerVersion.getUserId();
            rollbackSqlLines.add("# start by " + currentCommitterName);
            String devDirPath = developerVersionBasePath + currentCommitterName + "/" + versionId + "/versionContent/" + versionId + "/SMARTESB/DB/SCRIPT/";
            String devRollbackSqlFilePath = devDirPath + "rollback.sql";
            File file = new File(devRollbackSqlFilePath);
            if (!file.exists()) {
                continue;
            }
            List<String> currentRollbackSqlLines = MyFileUtils.readFileAllLines(devRollbackSqlFilePath, "utf-8");
            rollbackSqlLines.addAll(currentRollbackSqlLines);
            rollbackSqlLines.add("# end by " + currentCommitterName + "\n\n");
        }
        MyFileUtils.writeLinesToFileFromHead(rollbackSqlLines, rollbackProtocolFilePath, "utf-8");
    }
}
