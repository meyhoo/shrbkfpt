package com.shrb.versionowner.service;

import com.alibaba.fastjson.JSONObject;
import com.shrb.versionowner.entity.api.ApiExtendResponse;
import com.shrb.versionowner.entity.api.ApiResponse;
import com.shrb.versionowner.entity.business.DeveloperVersion;
import com.shrb.versionowner.entity.business.Task;
import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.lock.LockFactory;
import com.shrb.versionowner.utils.CollectionUtils;
import com.shrb.versionowner.utils.MyFileUtils;
import com.shrb.versionowner.utils.WebHttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.shrb.versionowner.service.AdministratorVersionService.VERSION_COMMITTER_FILE_NAME;

@Service
public class DeveloperVersionService {
    private static final Logger log = LoggerFactory.getLogger(DeveloperVersionService.class);

    private static final String TASK_INFO_FILE_NAME = "taskInfo.dat";
    private static final String PRIORITY_INFO_FILE_NAME = "priorityInfo.dat";
    private static final String VERSION_CONTENT_DIR_NAME = "/versionContent";

    @Autowired
    private RuntimeCacheService runtimeCacheService;

    @Autowired
    private Configuration configuration;

    public void rewriteVersionCommitterToFile(String versionId) throws Exception {
        String basePath = configuration.getAdministratorVersionBasePath();
        String versionCommitterFilePath = basePath + versionId + "/" + VERSION_COMMITTER_FILE_NAME;
        ArrayList<String> list = runtimeCacheService.getVersionCommitters(versionId);
        StringBuffer sb = new StringBuffer();
        for (String committerName : list) {
            if (committerName.equals(list.get(0))) {
                sb.append(committerName);
            } else {
                sb.append("|").append(committerName);
            }
        }
        List<String> lines = new ArrayList<>();
        lines.add(sb.toString());
        MyFileUtils.writeLinesToFileFromHead(lines, versionCommitterFilePath, "utf-8");
    }

    public void rewriteDeveloperVersionTaskToFile(String userName, String versionId) throws Exception {
        String basePath = configuration.getDeveloperVersionBasePath();
        String developerVersionTaskInfoFilePath = basePath + userName + "/" + versionId + "/" + TASK_INFO_FILE_NAME;
        DeveloperVersion developerVersion = runtimeCacheService.getDeveloperVersion(userName, versionId);
        List<Task> taskList = developerVersion.getTaskList();
        List<String> taskLines = new ArrayList<>();
        for (Task task : taskList) {
            StringBuffer sb = new StringBuffer();
            sb.append(task.getUserId()).append("|")
                    .append(task.getState()).append("|")
                    .append(task.getTaskInfo());
            taskLines.add(sb.toString());
        }
        MyFileUtils.writeLinesToFileFromHead(taskLines, developerVersionTaskInfoFilePath, "utf-8");
    }

    public void rewriteDeveloperVersionPriorityToFile(String userName, String versionId) throws Exception {
        String basePath = configuration.getDeveloperVersionBasePath();
        String developerVersionPriorityInfoFilePath = basePath + userName + "/" + versionId + "/" + PRIORITY_INFO_FILE_NAME;
        DeveloperVersion developerVersion = runtimeCacheService.getDeveloperVersion(userName, versionId);
        List<String> priorityLines = new ArrayList<>();
        priorityLines.add(developerVersion.getPriority().toString());
        MyFileUtils.writeLinesToFileFromHead(priorityLines, developerVersionPriorityInfoFilePath, "utf-8");
    }

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
                String taskInfoFilePath = versionIdDir + "/" + TASK_INFO_FILE_NAME;
                String priorityInfoFilePath = versionIdDir + "/" + PRIORITY_INFO_FILE_NAME;
                DeveloperVersion developerVersion = new DeveloperVersion();
                developerVersion.setTaskInfoFilePath(taskInfoFilePath);
                developerVersion.setPriorityInfoFilePath(priorityInfoFilePath);
                List<String> taskLines = MyFileUtils.readFileAllLines(taskInfoFilePath, "utf-8");
                ArrayList<Task> taskList = new ArrayList<>();
                for (String taskInfo : taskLines) {
                    if (!StringUtils.isEmpty(taskInfo)) {
                        String[] taskInfoArray = taskInfo.split("\\|");
                        Task task = new Task();
                        task.setUserId(taskInfoArray[0]);
                        task.setState(taskInfoArray[1]);
                        task.setTaskInfo(taskInfoArray[2]);
                        taskList.add(task);
                    }
                }
                developerVersion.setTaskList(taskList);
                String priorityStr = MyFileUtils.readFileToString(priorityInfoFilePath, "utf-8");
                developerVersion.setPriority(Integer.parseInt(priorityStr));
                developerVersion.setUserId(userId);
                developerVersion.setVersionId(versionId);
                versionMap.put(versionId, developerVersion);
            }
            map.put(userId, versionMap);
        }
        return map;
    }

    public ApiExtendResponse searchDeveloperVersionList(HttpServletRequest request, String userName) throws Exception {
        JSONObject requestJson = WebHttpUtils.getHttpRequestJson(request);
        Integer draw = Integer.parseInt(requestJson.getString("draw"));
        Integer from = Integer.parseInt(requestJson.getString("start"));
        Integer pageSize = Integer.parseInt(requestJson.getString("pageCount"));
        String versionId = requestJson.getString("versionId");
        Map<String, Object> map = new HashMap<>();
        map.put("versionId", versionId);
        map.put("pageSize", pageSize);
        map.put("from", from);
        List<Map<String, Object>> list = runtimeCacheService.getDeveloperVersionList(userName);
        CollectionUtils<Map<String, Object>> collectionUtils = new CollectionUtils<Map<String, Object>>(list, map);
        CollectionUtils.ResultInfo<Map<String, Object>> resultInfo = collectionUtils.getListByFuzzyMatch();
        ApiExtendResponse apiExtendResponse = new ApiExtendResponse();
        apiExtendResponse.setData(resultInfo.getList());
        apiExtendResponse.setDraw(draw);
        apiExtendResponse.setPageCount(pageSize);
        apiExtendResponse.setDataMaxCount(resultInfo.getCount());
        apiExtendResponse.setDataMaxPage(resultInfo.getCount() % pageSize == 0 ? resultInfo.getCount() / pageSize : resultInfo.getCount() / pageSize + 1);
        return apiExtendResponse;
    }

    public ApiExtendResponse searchDeveloperVersionTaskList(HttpServletRequest request, String userName) throws Exception {
        JSONObject requestJson = WebHttpUtils.getHttpRequestJson(request);
        Integer draw = Integer.parseInt(requestJson.getString("draw"));
        Integer from = Integer.parseInt(requestJson.getString("start"));
        Integer pageSize = Integer.parseInt(requestJson.getString("pageCount"));
        String versionId = requestJson.getString("versionId");
        Map<String, Object> map = new HashMap<>();
        map.put("versionId", versionId);
        map.put("pageSize", pageSize);
        map.put("from", from);
        List<Map<String, Object>> list = runtimeCacheService.getDeveloperVersionTaskList(userName, versionId);
        CollectionUtils<Map<String, Object>> collectionUtils = new CollectionUtils<Map<String, Object>>(list, map);
        CollectionUtils.ResultInfo<Map<String, Object>> resultInfo = collectionUtils.getListByFuzzyMatch();
        ApiExtendResponse apiExtendResponse = new ApiExtendResponse();
        apiExtendResponse.setData(resultInfo.getList());
        apiExtendResponse.setDraw(draw);
        apiExtendResponse.setPageCount(pageSize);
        apiExtendResponse.setDataMaxCount(resultInfo.getCount());
        apiExtendResponse.setDataMaxPage(resultInfo.getCount() % pageSize == 0 ? resultInfo.getCount() / pageSize : resultInfo.getCount() / pageSize + 1);
        return apiExtendResponse;
    }

    public ApiResponse addDeveloperVersionTask(String userName, String versionId, String taskInfo) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        DeveloperVersion developerVersion = runtimeCacheService.getDeveloperVersion(userName, versionId);
        if (developerVersion == null) {
            apiResponse.setErrorCode("999999");
            apiResponse.setErrorMsg("developerVersion不存在");
            return apiResponse;
        }
        List<Task> taskList = developerVersion.getTaskList();
        boolean existsFlag = false;
        for (Task e : taskList) {
            if (taskInfo.equals(e.getTaskInfo())) {
                existsFlag = true;
                break;
            }
        }
        if (existsFlag) {
            apiResponse.setErrorCode("999999");
            apiResponse.setErrorMsg("task已存在");
            return apiResponse;
        }
        synchronized (LockFactory.getLock("developerVersion_"+userName+"_"+versionId)) {
            developerVersion = runtimeCacheService.getDeveloperVersion(userName, versionId);
            if (developerVersion == null) {
                apiResponse.setErrorCode("999999");
                apiResponse.setErrorMsg("developerVersion不存在");
                return apiResponse;
            }
            taskList = developerVersion.getTaskList();
            for (Task e : taskList) {
                if (taskInfo.equals(e.getTaskInfo())) {
                    existsFlag = true;
                    break;
                }
            }
            if (existsFlag) {
                apiResponse.setErrorCode("999999");
                apiResponse.setErrorMsg("task已存在");
                return apiResponse;
            }
            Task task = new Task();
            task.setTaskInfo(taskInfo);
            task.setUserId(userName);
            task.setState("0");
            taskList.add(task);
            developerVersion.setTaskList(taskList);
            HashMap<String, DeveloperVersion> map = runtimeCacheService.getDeveloperVersionMap().get(userName);
            map.put(versionId, developerVersion);
            runtimeCacheService.getDeveloperVersionMap().put(userName, map);
            rewriteDeveloperVersionTaskToFile(userName, versionId);
        }
        return apiResponse;
    }

    public ApiResponse updateDeveloperVersionPriority(String userName, String versionId, String priority) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        DeveloperVersion developerVersion = runtimeCacheService.getDeveloperVersion(userName, versionId);
        if (developerVersion == null) {
            apiResponse.setErrorCode("999999");
            apiResponse.setErrorMsg("developerVersion不存在");
            return apiResponse;
        }
        synchronized (LockFactory.getLock("developerVersion_"+userName+"_"+versionId)) {
            developerVersion = runtimeCacheService.getDeveloperVersion(userName, versionId);
            if (developerVersion == null) {
                apiResponse.setErrorCode("999999");
                apiResponse.setErrorMsg("developerVersion不存在");
                return apiResponse;
            }
            developerVersion.setPriority(Integer.parseInt(priority));
            HashMap<String, DeveloperVersion> map = runtimeCacheService.getDeveloperVersionMap().get(userName);
            map.put(versionId, developerVersion);
            runtimeCacheService.getDeveloperVersionMap().put(userName, map);
            rewriteDeveloperVersionPriorityToFile(userName, versionId);
        }
        return apiResponse;
    }

    public ApiResponse updateDeveloperVersionTaskState(String userName, String versionId, String taskInfo, String state) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        DeveloperVersion developerVersion = runtimeCacheService.getDeveloperVersion(userName, versionId);
        if (developerVersion == null) {
            apiResponse.setErrorCode("999999");
            apiResponse.setErrorMsg("developerVersion不存在");
            return apiResponse;
        }
        List<Task> taskList = developerVersion.getTaskList();
        boolean existsFlag = false;
        for (Task e : taskList) {
            if (taskInfo.equals(e.getTaskInfo())) {
                existsFlag = true;
                break;
            }
        }
        if (!existsFlag) {
            apiResponse.setErrorCode("999999");
            apiResponse.setErrorMsg("task不存在");
            return apiResponse;
        }
        synchronized (LockFactory.getLock("developerVersion_"+userName+"_"+versionId)) {
            developerVersion = runtimeCacheService.getDeveloperVersion(userName, versionId);
            if (developerVersion == null) {
                apiResponse.setErrorCode("999999");
                apiResponse.setErrorMsg("developerVersion不存在");
                return apiResponse;
            }
            taskList = developerVersion.getTaskList();
            for (Task e : taskList) {
                if (taskInfo.equals(e.getTaskInfo())) {
                    e.setState(state);
                    existsFlag = true;
                    break;
                }
            }
            if (!existsFlag) {
                apiResponse.setErrorCode("999999");
                apiResponse.setErrorMsg("task不存在");
                return apiResponse;
            }
            developerVersion.setTaskList(taskList);
            HashMap<String, DeveloperVersion> map = runtimeCacheService.getDeveloperVersionMap().get(userName);
            map.put(versionId, developerVersion);
            runtimeCacheService.getDeveloperVersionMap().put(userName, map);
            rewriteDeveloperVersionTaskToFile(userName, versionId);
        }
        return apiResponse;
    }

    public ApiResponse deleteDeveloperVersionTask(String userName, String versionId, String taskInfo) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        DeveloperVersion developerVersion = runtimeCacheService.getDeveloperVersion(userName, versionId);
        if (developerVersion == null) {
            apiResponse.setErrorCode("999999");
            apiResponse.setErrorMsg("developerVersion不存在");
            return apiResponse;
        }
        List<Task> taskList = developerVersion.getTaskList();
        boolean existsFlag = false;
        for (Task e : taskList) {
            if (taskInfo.equals(e.getTaskInfo())) {
                existsFlag = true;
                break;
            }
        }
        if (!existsFlag) {
            return apiResponse;
        }
        synchronized (LockFactory.getLock("developerVersion_"+userName+"_"+versionId)) {
            developerVersion = runtimeCacheService.getDeveloperVersion(userName, versionId);
            if (developerVersion == null) {
                apiResponse.setErrorCode("999999");
                apiResponse.setErrorMsg("developerVersion不存在");
                return apiResponse;
            }
            taskList = developerVersion.getTaskList();
            int index = 0;
            for (Task e : taskList) {
                if (taskInfo.equals(e.getTaskInfo())) {
                    existsFlag = true;
                    break;
                }
                index++;
            }
            if (!existsFlag) {
                return apiResponse;
            }
            taskList.remove(index);
            developerVersion.setTaskList(taskList);
            HashMap<String, DeveloperVersion> map = runtimeCacheService.getDeveloperVersionMap().get(userName);
            map.put(versionId, developerVersion);
            runtimeCacheService.getDeveloperVersionMap().put(userName, map);
            rewriteDeveloperVersionTaskToFile(userName, versionId);
        }
        return apiResponse;
    }

    public ApiResponse becomeCommitter(String versionId, String userName) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        if (runtimeCacheService.getVersionCommitters(versionId).contains(userName)) {
            apiResponse.setErrorCode("999999");
            apiResponse.setErrorMsg("你已经是版本提交者了");
            return apiResponse;
        }
        synchronized (LockFactory.getLock("versionCommitter_"+versionId)) {
            ArrayList<String> committerList = runtimeCacheService.getVersionCommitters(versionId);
            if (committerList.contains(userName)) {
                apiResponse.setErrorCode("999999");
                apiResponse.setErrorMsg("你已经是版本提交者了");
                return apiResponse;
            }
            committerList.add(userName);
            runtimeCacheService.getVersionCommitterMap().put(versionId, committerList);
            rewriteVersionCommitterToFile(versionId);
        }
        synchronized (LockFactory.getLock("developerVersion_"+userName+"_"+versionId)) {
            HashMap<String, DeveloperVersion> map = runtimeCacheService.getDeveloperVersionMap().get(userName);
            if (map.get(versionId) != null) {
                return apiResponse;
            }
            String basePath = configuration.getDeveloperVersionBasePath() + userName + "/" + versionId;
            MyFileUtils.createFile(basePath);
            String priorityInfoFilePath = basePath + "/" + PRIORITY_INFO_FILE_NAME;
            String taskInfoFilePath = basePath + "/" + TASK_INFO_FILE_NAME;
            String versionContentDirPath = basePath + VERSION_CONTENT_DIR_NAME;
            DeveloperVersion developerVersion = new DeveloperVersion();
            developerVersion.setVersionId(versionId);
            Integer priority = Integer.MAX_VALUE;
            developerVersion.setPriority(Integer.MAX_VALUE);
            developerVersion.setTaskList(new ArrayList<>());
            developerVersion.setUserId(userName);
            developerVersion.setPriorityInfoFilePath(priorityInfoFilePath);
            developerVersion.setTaskInfoFilePath(taskInfoFilePath);
            developerVersion.setVersionContentDirPath(versionContentDirPath);
            MyFileUtils.createFile(priorityInfoFilePath);
            MyFileUtils.createFile(taskInfoFilePath);
            MyFileUtils.createFile(versionContentDirPath);
            List<String> lines = new ArrayList<>();
            lines.add(priority.toString());
            MyFileUtils.writeLinesToFileFromHead(lines, priorityInfoFilePath, "utf-8");
            map.put(versionId, developerVersion);
            runtimeCacheService.getDeveloperVersionMap().put(userName, map);
        }
        return apiResponse;
    }


}
