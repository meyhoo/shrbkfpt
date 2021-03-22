package com.shrb.versionowner.service;

import com.shrb.versionowner.entity.business.DeveloperVersion;
import com.shrb.versionowner.entity.business.Task;
import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.utils.MyFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DeveloperVersionService {
    private static final Logger log = LoggerFactory.getLogger(DeveloperVersionService.class);

    private static final String TASK_INFO_FILE_PATH = "taskInfo.dat";

    @Autowired
    private RuntimeCacheService runtimeCacheService;

    @Autowired
    private Configuration configuration;

    public ConcurrentHashMap<String, HashMap<String, DeveloperVersion>> getDeveloperVersionMap() throws Exception {
        ConcurrentHashMap<String, HashMap<String, DeveloperVersion>> map = new ConcurrentHashMap<>();
        String basePath = configuration.getDeveloperVersionBasePath();
        //遍历basePath，子文件夹名是userId
        List<String> userDirList = MyFileUtils.listFilePath(basePath, "dir");
        for(String userIdDir : userDirList) {
            File userDir = new File(userIdDir);
            String userId = userDir.getName();
            //遍历userIdDir，子文件夹名是versionId
            List<String> versionDirList = MyFileUtils.listFilePath(userIdDir, "dir");
            HashMap<String, DeveloperVersion> versionMap = new HashMap<>();
            for(String versionIdDir : versionDirList) {
                File versionDir = new File(versionIdDir);
                String versionId = versionDir.getName();
                String taskInfoFilePath = versionIdDir + "/" + TASK_INFO_FILE_PATH;
                DeveloperVersion developerVersion = new DeveloperVersion();
                developerVersion.setTaskInfoFilePath(taskInfoFilePath);
                List<String> taskLines = MyFileUtils.readFileAllLines(taskInfoFilePath, "utf-8");
                ArrayList<Task> taskList = new ArrayList<>();
                for (String taskInfo : taskLines) {
                    String[] taskInfoArray = taskInfo.split("\\|");
                    Task task = new Task();
                    task.setUserId(taskInfoArray[0]);
                    task.setState(taskInfoArray[1]);
                    task.setTaskInfo(taskInfoArray[2]);
                    taskList.add(task);
                }
                developerVersion.setTaskList(taskList);
                developerVersion.setUserId(userId);
                developerVersion.setVersionId(versionId);
                versionMap.put(versionId, developerVersion);
            }
            map.put(userId, versionMap);
        }
        return map;
    }
}
