package com.shrb.versionowner.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shrb.versionowner.entity.api.ApiExtendResponse;
import com.shrb.versionowner.entity.api.ApiResponse;
import com.shrb.versionowner.entity.business.SqlTemplate;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SqlTemplateService {

    private static final Logger log = LoggerFactory.getLogger(SqlTemplateService.class);

    private static final String RUN_SQL_FILE_NAME = "run.sql";

    private static final String ROLLBACK_SQL_FILE_NAME = "rollback.sql";

    private static final String TEMPLATE_INFO_FILE_NAME = "templateInfo.dat";



    @Autowired
    private Configuration configuration;
    @Autowired
    private RuntimeCacheService runtimeCacheService;

    /**
     * 扫描目录，获取sql模板信息
     * @return
     */
    public ConcurrentHashMap<String, SqlTemplate> getSqlTemplateMap() throws Exception {
        ConcurrentHashMap<String, SqlTemplate> sqlTemplateMap = new ConcurrentHashMap<>();
        String basePath = configuration.getSqlTemplatesBasePath();
        List<String> dirList = MyFileUtils.listFilePath(basePath, "dir");
        for(String dirPath : dirList) {
            File dir = new File(dirPath);
            String templateInfoFilePath = dirPath + "/" + TEMPLATE_INFO_FILE_NAME;
            File templateInfoFile = new File(templateInfoFilePath);
            String templateInfo;
            if (!templateInfoFile.exists()) {
                templateInfo = "";
            } else {
                templateInfo = MyFileUtils.readFileToString(templateInfoFilePath, "utf-8");
            }
            String templateId = dir.getName();
            SqlTemplate sqlTemplate = new SqlTemplate();
            sqlTemplate.setTemplateId(templateId);
            sqlTemplate.setTemplateInfo(templateInfo);
            sqlTemplate.setTemplateInfoFilePath(templateInfoFilePath);
            String runSqlFilePath = dirPath + "/" + RUN_SQL_FILE_NAME;
            List<String> runSqlContentList = MyFileUtils.readFileAllLines(runSqlFilePath, "utf-8");
            String runSqlContent = MyFileUtils.convertLinesToString(runSqlContentList);
            sqlTemplate.setRunSqlFilePath(runSqlFilePath);
            sqlTemplate.setRunSqlContent(runSqlContent);
            String rollbackSqlFilePath = dirPath + "/" + ROLLBACK_SQL_FILE_NAME;
            List<String> rollbackSqlContentList = MyFileUtils.readFileAllLines(rollbackSqlFilePath, "utf-8");
            String rollbackSqlContent = MyFileUtils.convertLinesToString(rollbackSqlContentList);
            sqlTemplate.setRollbackSqlFilePath(rollbackSqlFilePath);
            sqlTemplate.setRollbackSqlContent(rollbackSqlContent);
            sqlTemplateMap.put(templateId, sqlTemplate);
        }
        return sqlTemplateMap;
    }

    public ApiExtendResponse searchSqlTemplateList(HttpServletRequest request) throws Exception {
        JSONObject requestJson = WebHttpUtils.getHttpRequestJson(request);
        Integer draw = Integer.parseInt(requestJson.getString("draw"));
        Integer from = Integer.parseInt(requestJson.getString("start"));
        Integer pageSize = Integer.parseInt(requestJson.getString("pageCount"));
        String templateId = requestJson.getString("templateId");
        String templateInfo = requestJson.getString("templateInfo");
        Map<String, Object> map = new HashMap<>();
        map.put("templateId", templateId);
        map.put("templateInfo", templateInfo);
        map.put("pageSize", pageSize);
        map.put("from", from);
        List<Map<String, Object>> list = runtimeCacheService.getSqlTemplateList();
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

    public ApiResponse addSqlTemplate(String templateId, String templateInfo, String runSqlContent, String rollbackSqlContent) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        SqlTemplate sqlTemplate = runtimeCacheService.getSqlTemplate(templateId);
        if(sqlTemplate != null) {
            apiResponse.setErrorCode("999999");
            apiResponse.setErrorMsg("sql模板已存在");
            return apiResponse;
        }
        sqlTemplate = new SqlTemplate();
        sqlTemplate.setTemplateId(templateId);
        sqlTemplate.setTemplateInfo(templateInfo);
        sqlTemplate.setRunSqlContent(runSqlContent);
        sqlTemplate.setRollbackSqlContent(rollbackSqlContent);
        String basePath = configuration.getSqlTemplatesBasePath();
        String templateInfoFilePath = basePath+templateId + "/" + TEMPLATE_INFO_FILE_NAME;
        String runSqlFilePath = basePath+templateId + "/" + RUN_SQL_FILE_NAME;
        String rollbackSqlFilePath = basePath+templateId + "/" + ROLLBACK_SQL_FILE_NAME;
        sqlTemplate.setTemplateInfoFilePath(templateInfoFilePath);
        sqlTemplate.setRunSqlFilePath(runSqlFilePath);
        sqlTemplate.setRollbackSqlFilePath(rollbackSqlFilePath);
        List<String> runSqlContentList = MyFileUtils.convertStringToLines(runSqlContent);
        List<String> rollbackSqlContentList = MyFileUtils.convertStringToLines(rollbackSqlContent);
        synchronized (LockFactory.getLock("sqltemplate")) {
            if (runtimeCacheService.getSqlTemplate(templateId) != null) {
                apiResponse.setErrorCode("999999");
                apiResponse.setErrorMsg("sql模板已存在");
                return apiResponse;
            }
            runtimeCacheService.getSqlTemplateMap().put(templateId, sqlTemplate);
            MyFileUtils.createFile(templateInfoFilePath);
            MyFileUtils.createFile(runSqlFilePath);
            MyFileUtils.createFile(rollbackSqlFilePath);
            List<String> templateInfoLines = new ArrayList<>();
            templateInfoLines.add(sqlTemplate.getTemplateInfo());
            MyFileUtils.writeLinesToFileFromHead(templateInfoLines, templateInfoFilePath, "utf-8");
            MyFileUtils.writeLinesToFileFromHead(runSqlContentList, runSqlFilePath, "utf-8");
            MyFileUtils.writeLinesToFileFromHead(rollbackSqlContentList, rollbackSqlFilePath, "utf-8");
        }
        return apiResponse;
    }

    public ApiResponse updateSqlTemplate(JSONObject jsonObject) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        String templateId = jsonObject.getString("templateId");
        SqlTemplate sqlTemplate = runtimeCacheService.getSqlTemplate(templateId);
        if (sqlTemplate == null) {
            apiResponse.setErrorCode("999999");
            apiResponse.setErrorMsg("sql模板不存在");
            return apiResponse;
        }
        String templateInfo = jsonObject.getString("templateInfo");
        String runSqlContent = jsonObject.getString("runSqlContent");
        String rollbackSqlContent = jsonObject.getString("rollbackSqlContent");

        //修改缓存并改写目录下的文件
        synchronized (LockFactory.getLock("sqltemplate")) {
            runtimeCacheService.getSqlTemplateMap().put(templateId, sqlTemplate);

            sqlTemplate.setTemplateInfo(templateInfo);
            String templateInfoFilePath = sqlTemplate.getTemplateInfoFilePath();
            List<String> templateInfoLines = MyFileUtils.convertStringToLines(templateInfo);
            MyFileUtils.writeLinesToFileFromHead(templateInfoLines, templateInfoFilePath, "utf-8");

            sqlTemplate.setRunSqlContent(runSqlContent);
            String runSqlFilePath = sqlTemplate.getRunSqlFilePath();
            List<String> runSqlLines = MyFileUtils.convertStringToLines(runSqlContent);
            MyFileUtils.writeLinesToFileFromHead(runSqlLines, runSqlFilePath, "utf-8");

            sqlTemplate.setRollbackSqlContent(rollbackSqlContent);
            String rollbackSqlFilePath = sqlTemplate.getRollbackSqlFilePath();
            List<String> rollbackSqlLines = MyFileUtils.convertStringToLines(rollbackSqlContent);
            MyFileUtils.writeLinesToFileFromHead(rollbackSqlLines, rollbackSqlFilePath, "utf-8");

        }
        return apiResponse;
    }

    public ApiResponse deleteSqlTemplate(String templateId) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        SqlTemplate sqlTemplate = runtimeCacheService.getSqlTemplate(templateId);
        if (sqlTemplate == null) {
            return apiResponse;
        }
        synchronized (LockFactory.getLock("sqltemplate")) {
            if (runtimeCacheService.getSqlTemplate(templateId) == null) {
                return apiResponse;
            }
            runtimeCacheService.getSqlTemplateMap().remove(templateId);
            String dirPath = configuration.getSqlTemplatesBasePath() + sqlTemplate.getTemplateId();
            File dir = new File(dirPath);
            MyFileUtils.deleteDirOrFile(dir);
        }
        return apiResponse;
    }

    public ApiResponse deleteSqlTemplates(JSONArray array) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        synchronized (LockFactory.getLock("sqltemplate")) {
            for(int i=0; i<array.size(); i++) {
                String templateId = array.getString(i);
                SqlTemplate sqlTemplate = runtimeCacheService.getSqlTemplate(templateId);
                if (sqlTemplate == null) {
                    continue;
                }
                runtimeCacheService.getSqlTemplateMap().remove(templateId);
                String dirPath = configuration.getSqlTemplatesBasePath() + sqlTemplate.getTemplateId();
                File dir = new File(dirPath);
                MyFileUtils.deleteDirOrFile(dir);
            }
        }
        return apiResponse;
    }


}
