package com.shrb.versionowner.service;

import com.shrb.versionowner.entity.business.AdministratorVersion;
import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.utils.MyFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AdministratorVersionService {
    private static final Logger log = LoggerFactory.getLogger(AdministratorVersionService.class);

    private static final String ADMIN_VERSION_INFO_FILE_PATH = "administratorVersionInfo.dat";

    private static final String VERSION_COMMITTER_FILE_PATH = "versionCommitter.dat";

    @Autowired
    private Configuration configuration;

    @Autowired
    private RuntimeCacheService runtimeCacheService;

    public ConcurrentHashMap<String, AdministratorVersion> getAdministratorVersionMap() throws Exception {
        ConcurrentHashMap<String, AdministratorVersion> map = new ConcurrentHashMap<>();
        String basePath = configuration.getAdministratorVersionBasePath();
        List<String> dirList = MyFileUtils.listFilePath(basePath, "dir");
        for(String dirPath : dirList) {
            File dir = new File(dirPath);
            String adminVersionInfoFilePath = dirPath + "/" + ADMIN_VERSION_INFO_FILE_PATH;
            File adminVersionInfoFile = new File(adminVersionInfoFilePath);
            String adminVersionInfo;
            if (!adminVersionInfoFile.exists()) {
                adminVersionInfo = "";
            } else {
                adminVersionInfo = MyFileUtils.readFileToString(adminVersionInfoFilePath, "utf-8");
            }
            String versionId = dir.getName();
            AdministratorVersion administratorVersion = new AdministratorVersion();
            administratorVersion.setVersionId(versionId);
            administratorVersion.setVersionInfo(adminVersionInfo);
            administratorVersion.setVersionInfoFilePath(adminVersionInfoFilePath);
            map.put(versionId, administratorVersion);
        }
        return map;
    }

    public ConcurrentHashMap<String, ArrayList<String>> getVersionCommitterMap() throws Exception {
        ConcurrentHashMap<String, ArrayList<String>> map = new ConcurrentHashMap<>();
        String basePath = configuration.getAdministratorVersionBasePath();
        List<String> dirList = MyFileUtils.listFilePath(basePath, "dir");
        for(String dirPath : dirList) {
            File dir = new File(dirPath);
            String versionCommitterFilePath = dirPath + "/" + VERSION_COMMITTER_FILE_PATH;
            String versionCommitterStr = MyFileUtils.readFileToString(versionCommitterFilePath, "utf-8");
            String[] versionCommitters = versionCommitterStr.split("\\|");
            ArrayList<String> userIds = new ArrayList<>();
            for(String userId : versionCommitters) {
                userIds.add(userId);
            }
            String versionId = dir.getName();
            map.put(versionId, userIds);
        }
        return map;
    }
}
