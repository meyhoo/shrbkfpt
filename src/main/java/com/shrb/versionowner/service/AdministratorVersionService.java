package com.shrb.versionowner.service;

import com.alibaba.fastjson.JSONObject;
import com.shrb.versionowner.entity.api.ApiExtendResponse;
import com.shrb.versionowner.entity.api.ApiResponse;
import com.shrb.versionowner.entity.business.AdministratorVersion;
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
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AdministratorVersionService {
    private static final Logger log = LoggerFactory.getLogger(AdministratorVersionService.class);

    private static final String ADMIN_VERSION_INFO_FILE_NAME = "administratorVersionInfo.dat";

    public static final String VERSION_COMMITTER_FILE_NAME = "versionCommitter.dat";

    public static final String VERSION_CONTENT_TEMPLATE_DIR_NAME = "/versionContentTemplate";

    @Autowired
    private Configuration configuration;

    @Autowired
    private RuntimeCacheService runtimeCacheService;

    @Autowired
    private MergeVersionService mergeVersionService;

    public ConcurrentHashMap<String, AdministratorVersion> getAdministratorVersionMap() throws Exception {
        ConcurrentHashMap<String, AdministratorVersion> map = new ConcurrentHashMap<>();
        String basePath = configuration.getAdministratorVersionBasePath();
        List<String> dirList = MyFileUtils.listFilePath(basePath, "dir");
        for(String dirPath : dirList) {
            File dir = new File(dirPath);
            String adminVersionInfoFilePath = dirPath + "/" + ADMIN_VERSION_INFO_FILE_NAME;
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
            String versionCommitterFilePath = dirPath + "/" + VERSION_COMMITTER_FILE_NAME;
            String versionCommitterStr = MyFileUtils.readFileToString(versionCommitterFilePath, "utf-8");
            ArrayList<String> userIds = new ArrayList<>();
            if (!StringUtils.isEmpty(versionCommitterStr)) {
                String[] versionCommitters = versionCommitterStr.split("\\|");
                for(String userId : versionCommitters) {
                    userIds.add(userId);
                }
            }
            String versionId = dir.getName();
            map.put(versionId, userIds);
        }
        return map;
    }

    public ApiExtendResponse searchAdministratorVersionList(HttpServletRequest request) throws Exception {
        JSONObject requestJson = WebHttpUtils.getHttpRequestJson(request);
        Integer draw = Integer.parseInt(requestJson.getString("draw"));
        Integer from = Integer.parseInt(requestJson.getString("start"));
        Integer pageSize = Integer.parseInt(requestJson.getString("pageCount"));
        String versionId = requestJson.getString("versionId");
        Map<String, Object> map = new HashMap<>();
        map.put("versionId", versionId);
        map.put("pageSize", pageSize);
        map.put("from", from);
        List<Map<String, Object>> list = runtimeCacheService.getAdministratorVersionList();
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

    public ApiExtendResponse searchCommitterTaskList(HttpServletRequest request) throws Exception {
        JSONObject requestJson = WebHttpUtils.getHttpRequestJson(request);
        Integer draw = Integer.parseInt(requestJson.getString("draw"));
        Integer from = Integer.parseInt(requestJson.getString("start"));
        Integer pageSize = Integer.parseInt(requestJson.getString("pageCount"));
        String versionId = requestJson.getString("versionId");
        Map<String, Object> map = new HashMap<>();
        map.put("versionId", versionId);
        map.put("pageSize", pageSize);
        map.put("from", from);
        List<Map<String, Object>> list = runtimeCacheService.getCommitterTaskList(versionId);
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

    public ApiResponse addAdministratorVersion(String versionId, String versionInfo) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        AdministratorVersion administratorVersion = runtimeCacheService.getAdministratorVersion(versionId);
        if (administratorVersion != null) {
            apiResponse.setErrorCode("999999");
            apiResponse.setErrorMsg("版本已存在");
            return apiResponse;
        }
        administratorVersion = new AdministratorVersion();
        administratorVersion.setVersionId(versionId);
        administratorVersion.setVersionInfo(versionInfo);
        String basePath = configuration.getAdministratorVersionBasePath();
        String adminVersionInfoFilePath = basePath + versionId + "/" + ADMIN_VERSION_INFO_FILE_NAME;
        String versionCommitterFilePath = basePath + versionId + "/" + VERSION_COMMITTER_FILE_NAME;
        String versionContentTemplateFilePath = basePath + versionId + VERSION_CONTENT_TEMPLATE_DIR_NAME + "/" + versionId + ".zip";
        String devTemplatePath = configuration.getDevTemplateUnzipDirPath();
        administratorVersion.setVersionInfoFilePath(adminVersionInfoFilePath);
        synchronized (LockFactory.getLock("administratorVersion_"+versionId)) {
            if (runtimeCacheService.getAdministratorVersion(versionId) != null) {
                apiResponse.setErrorCode("999999");
                apiResponse.setErrorMsg("版本已存在");
                return apiResponse;
            }
            runtimeCacheService.getAdministratorVersionMap().put(versionId, administratorVersion);
            runtimeCacheService.getVersionCommitterMap().put(versionId, new ArrayList<>());
            MyFileUtils.createFile(adminVersionInfoFilePath);
            MyFileUtils.createFile(versionCommitterFilePath);
            List<String> versionInfoLines = new ArrayList<>();
            versionInfoLines.add(versionInfo);
            MyFileUtils.writeLinesToFileFromHead(versionInfoLines, adminVersionInfoFilePath, "utf-8");
            MyFileUtils.compressZip(devTemplatePath, versionContentTemplateFilePath, versionId);
        }
        return apiResponse;
    }

    public ApiResponse deleteAdministratorVersion(String versionId) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        AdministratorVersion administratorVersion = runtimeCacheService.getAdministratorVersion(versionId);
        if (administratorVersion == null) {
            return apiResponse;
        }
        synchronized (LockFactory.getLock("administratorVersion_"+versionId)) {
            runtimeCacheService.getAdministratorVersionMap().remove(versionId);
            runtimeCacheService.getVersionCommitterMap().remove(versionId);
            //移到备份目录
            String originBasePath = configuration.getAdministratorVersionBasePath() + versionId;
            String bakBasePath = configuration.getAdministratorVersionBakBasePath();
            MyFileUtils.moveFileOrDir(new File(originBasePath), bakBasePath);
        }
        return apiResponse;
    }

    public void downloadVersion(HttpServletResponse resp, String versionId) throws Exception {
        mergeVersionService.mergeVersion(versionId);
        String fileName = versionId + "_RELEASE.zip";
        String basePath = configuration.getAdministratorVersionBasePath() + versionId + "/versionContent/";
        String versionZipFilePath = basePath + fileName;
        String srcVersionDirPath = basePath + versionId + "/";
        File file = new File(versionZipFilePath);
        if (file.exists()) {
            file.delete();
        }
        MyFileUtils.compressZip(srcVersionDirPath, versionZipFilePath, versionId + "_上线补丁");
        resp.setHeader("content-type", "application/octet-stream");
        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            os = resp.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(file));
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }
}
