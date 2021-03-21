package com.shrb.versionowner.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shrb.versionowner.entity.api.ApiExtendResponse;
import com.shrb.versionowner.entity.api.ApiResponse;
import com.shrb.versionowner.entity.business.SqlTemplateGroup;
import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.lock.LockFactory;
import com.shrb.versionowner.utils.CollectionUtils;
import com.shrb.versionowner.utils.MyFileUtils;
import com.shrb.versionowner.utils.WebHttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SqlTemplateGroupService {
    private static final Logger log = LoggerFactory.getLogger(SqlTemplateGroupService.class);

    private static final String TEMPLATE_GROUP_INFO_FILE_NAME = "templateGroupInfo.dat";

    private static final String TEMPLATE_IDS_FILE_NAME = "templateIds.dat";

    @Autowired
    private Configuration configuration;
    @Autowired
    private RuntimeCacheService runtimeCacheService;

    public ConcurrentHashMap<String, SqlTemplateGroup> getSqlTemplateGroupMap() throws Exception {
        ConcurrentHashMap<String, SqlTemplateGroup> sqlTemplateGroupMap = new ConcurrentHashMap<>();
        String basePath = configuration.getSqlTemplateGroupBasePath();
        List<String> dirList = MyFileUtils.listFilePath(basePath, "dir");
        for(String dirPath : dirList) {
            File dir = new File(dirPath);
            String templateGroupInfoFilePath = dirPath + "/" + TEMPLATE_GROUP_INFO_FILE_NAME;
            File templateGroupInfoFile = new File(templateGroupInfoFilePath);
            String templateGroupInfo;
            if (!templateGroupInfoFile.exists()) {
                templateGroupInfo = "";
            } else {
                templateGroupInfo = MyFileUtils.readFileToString(templateGroupInfoFilePath, "utf-8");
            }
            String templateGroupId = dir.getName();
            SqlTemplateGroup sqlTemplateGroup = new SqlTemplateGroup();
            sqlTemplateGroup.setTemplateGroupId(templateGroupId);
            sqlTemplateGroup.setTemplateGroupInfo(templateGroupInfo);
            sqlTemplateGroup.setTemplateGroupInfoFilePath(templateGroupInfoFilePath);
            String templateIdsFilePath = dirPath + "/" + TEMPLATE_IDS_FILE_NAME;
            File templateIdsFile = new File(templateIdsFilePath);
            String templateIdStr;
            if (!templateIdsFile.exists()) {
                templateIdStr = "";
            } else {
                templateIdStr = MyFileUtils.readFileToString(templateIdsFilePath, "utf-8");
            }
            sqlTemplateGroup.setTemplateIdStr(templateIdStr);
            sqlTemplateGroup.setTemplateIdsFilePath(templateIdsFilePath);
            sqlTemplateGroupMap.put(templateGroupId, sqlTemplateGroup);
        }
        return sqlTemplateGroupMap;
    }

    public ApiExtendResponse searchSqlTemplateGroupList(HttpServletRequest request) throws Exception {
        JSONObject requestJson = WebHttpUtils.getHttpRequestJson(request);
        Integer draw = Integer.parseInt(requestJson.getString("draw"));
        Integer from = Integer.parseInt(requestJson.getString("start"));
        Integer pageSize = Integer.parseInt(requestJson.getString("pageCount"));
        String templateGroupId = requestJson.getString("templateGroupId");
        Map<String, Object> map = new HashMap<>();
        map.put("templateGroupId", templateGroupId);
        map.put("pageSize", pageSize);
        map.put("from", from);
        List<Map<String, Object>> list = runtimeCacheService.getSqlTemplateGroupList();
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

    public ApiResponse addSqlTemplateGroup(String templateGroupId, String templateGroupInfo, String templateIds) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        SqlTemplateGroup sqlTemplateGroup = runtimeCacheService.getSqlTemplateGroup(templateGroupId);
        if(sqlTemplateGroup != null) {
            apiResponse.setErrorCode("999999");
            apiResponse.setErrorMsg("sql模板组已存在");
            return apiResponse;
        }
        sqlTemplateGroup = new SqlTemplateGroup();
        sqlTemplateGroup.setTemplateGroupId(templateGroupId);
        sqlTemplateGroup.setTemplateGroupInfo(templateGroupInfo);
        sqlTemplateGroup.setTemplateIdStr(templateIds);
        String basePath = configuration.getSqlTemplateGroupBasePath();
        String templateGroupInfoFilePath = basePath+templateGroupId + "/" + TEMPLATE_GROUP_INFO_FILE_NAME;
        String templateIdsFilePath = basePath+templateGroupId + "/" + TEMPLATE_IDS_FILE_NAME;
        sqlTemplateGroup.setTemplateGroupInfoFilePath(templateGroupInfoFilePath);
        sqlTemplateGroup.setTemplateIdsFilePath(templateIdsFilePath);
        synchronized (LockFactory.getLock("sqltemplategroup")) {
            if (runtimeCacheService.getSqlTemplateGroup(templateGroupId) != null) {
                apiResponse.setErrorCode("999999");
                apiResponse.setErrorMsg("sql模板组已存在");
                return apiResponse;
            }
            runtimeCacheService.getSqlTemplateGroupMap().put(templateGroupId, sqlTemplateGroup);
            MyFileUtils.createFile(templateIdsFilePath);
            MyFileUtils.createFile(templateGroupInfoFilePath);
            List<String> templateIdsLines = MyFileUtils.convertStringToLines(templateIds);
            List<String> templateGroupInfoLines = MyFileUtils.convertStringToLines(templateGroupInfo);
            MyFileUtils.writeLinesToFileFromHead(templateIdsLines, templateIdsFilePath, "utf-8");
            MyFileUtils.writeLinesToFileFromHead(templateGroupInfoLines, templateGroupInfoFilePath, "utf-8");
        }
        return apiResponse;
    }

    public ApiResponse updateSqlTemplateGroup(JSONObject jsonObject) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        String templateGroupId = jsonObject.getString("templateGroupId");
        SqlTemplateGroup sqlTemplateGroup = runtimeCacheService.getSqlTemplateGroup(templateGroupId);
        if (sqlTemplateGroup == null) {
            apiResponse.setErrorCode("999999");
            apiResponse.setErrorMsg("sql模板组不存在");
            return apiResponse;
        }
        String templateGroupInfo = jsonObject.getString("templateGroupInfo");
        String templateIds = jsonObject.getString("templateIds");

        synchronized (LockFactory.getLock("sqltemplategroup")) {
            sqlTemplateGroup.setTemplateGroupInfo(templateGroupInfo);
            String templateGroupInfoFilePath = sqlTemplateGroup.getTemplateGroupInfoFilePath();
            List<String> templateGroupInfoLines = MyFileUtils.convertStringToLines(templateGroupInfo);
            MyFileUtils.writeLinesToFileFromHead(templateGroupInfoLines, templateGroupInfoFilePath, "utf-8");

            sqlTemplateGroup.setTemplateIdStr(templateIds);
            String templateIdsFilePath = sqlTemplateGroup.getTemplateIdsFilePath();
            List<String> templateIdsLines = MyFileUtils.convertStringToLines(templateIds);
            MyFileUtils.writeLinesToFileFromHead(templateIdsLines, templateIdsFilePath, "utf-8");

            runtimeCacheService.getSqlTemplateGroupMap().put(templateGroupId, sqlTemplateGroup);
        }
        return apiResponse;
    }

    public ApiResponse deleteSqlTemplateGroup(String templateGroupId) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        SqlTemplateGroup sqlTemplateGroup = runtimeCacheService.getSqlTemplateGroup(templateGroupId);
        if (sqlTemplateGroup == null) {
            return apiResponse;
        }
        synchronized (LockFactory.getLock("sqltemplategroup")) {
            if (runtimeCacheService.getSqlTemplateGroup(templateGroupId) == null) {
                return apiResponse;
            }
            runtimeCacheService.getSqlTemplateGroupMap().remove(templateGroupId);
            String dirPath = configuration.getSqlTemplateGroupBasePath() + templateGroupId;
            File dir = new File(dirPath);
            MyFileUtils.deleteDirOrFile(dir);
        }
        return apiResponse;
    }

    public ApiResponse deleteSqlTemplateGroups(JSONArray array) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        synchronized (LockFactory.getLock("sqltemplategroup")) {
            for(int i=0; i<array.size(); i++) {
                String templateGroupId = array.getString(i);
                SqlTemplateGroup sqlTemplateGroup = runtimeCacheService.getSqlTemplateGroup(templateGroupId);
                if (sqlTemplateGroup == null) {
                    continue;
                }
                runtimeCacheService.getSqlTemplateGroupMap().remove(templateGroupId);
                String dirPath = configuration.getSqlTemplateGroupBasePath() + templateGroupId;
                File dir = new File(dirPath);
                MyFileUtils.deleteDirOrFile(dir);
            }
        }
        return apiResponse;
    }
}
