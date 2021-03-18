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
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SqlTemplateService {

    private static final Logger log = LoggerFactory.getLogger(SqlTemplateService.class);

    private static final String RUN_SQL_FILE_NAME = "run.sql";

    private static final String ROLLBACK_SQL_FILE_NAME = "rollback.sql";

    private static final String TEMPLATE_INFO_FILE_NAME = "templateInfo.sql";

    private static final Pattern pattern = Pattern.compile("(#\\{)(.*?)(})");

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
            String templateInfoFilePath = dirPath + File.separator + TEMPLATE_INFO_FILE_NAME;
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
            String runSqlFilePath = dirPath+File.separator+RUN_SQL_FILE_NAME;
            List<String> runSqlContentList = MyFileUtils.readFileAllLines(runSqlFilePath, "utf-8");
            List<Set<String>> runSqlPlaceholders = new ArrayList<>();
            String runSqlContent = MyFileUtils.convertLinesToString(runSqlContentList);
            for(String line : runSqlContentList) {
                Set<String> set = new HashSet<>();
                Matcher m = pattern.matcher(line);
                while (m.find()) {
                    set.add(m.group(2));
                }
                runSqlPlaceholders.add(set);
            }
            sqlTemplate.setRunSqlFilePath(runSqlFilePath);
            sqlTemplate.setRunSqlContent(runSqlContent);
            sqlTemplate.setRunSqlPlaceholders(runSqlPlaceholders);
            String rollbackSqlFilePath = dirPath+File.separator+ROLLBACK_SQL_FILE_NAME;
            List<String> rollbackSqlContentList = MyFileUtils.readFileAllLines(rollbackSqlFilePath, "utf-8");
            List<Set<String>> rollbackSqlPlaceholders = new ArrayList<>();
            String rollbackSqlContent = MyFileUtils.convertLinesToString(rollbackSqlContentList);
            for(String line : rollbackSqlContentList) {
                Set<String> set = new HashSet<>();
                Matcher m = pattern.matcher(line);
                while (m.find()) {
                    set.add(m.group(2));
                }
                rollbackSqlPlaceholders.add(set);
            }
            sqlTemplate.setRollbackSqlFilePath(rollbackSqlFilePath);
            sqlTemplate.setRollbackSqlContent(rollbackSqlContent);
            sqlTemplate.setRollbackSqlPlaceholders(rollbackSqlPlaceholders);
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
        String templateInfoFilePath = basePath+templateId + File.separator + TEMPLATE_INFO_FILE_NAME;
        String runSqlFilePath = basePath+templateId+File.separator+RUN_SQL_FILE_NAME;
        String rollbackSqlFilePath = basePath+templateId+File.separator+ROLLBACK_SQL_FILE_NAME;
        sqlTemplate.setTemplateInfoFilePath(templateInfoFilePath);
        sqlTemplate.setRunSqlFilePath(runSqlFilePath);
        sqlTemplate.setRollbackSqlFilePath(rollbackSqlFilePath);
        List<String> runSqlContentList = MyFileUtils.convertStringToLines(runSqlContent);
        List<Set<String>> runSqlPlaceholders = new ArrayList<>();
        for(String line : runSqlContentList) {
            Set<String> set = new HashSet<>();
            Matcher m = pattern.matcher(line);
            while (m.find()) {
                set.add(m.group(2));
            }
            runSqlPlaceholders.add(set);
        }
        List<String> rollbackSqlContentList = MyFileUtils.convertStringToLines(rollbackSqlContent);
        List<Set<String>> rollbackSqlPlaceholders = new ArrayList<>();
        for(String line : rollbackSqlContentList) {
            Set<String> set = new HashSet<>();
            Matcher m = pattern.matcher(line);
            while (m.find()) {
                set.add(m.group(2));
            }
            rollbackSqlPlaceholders.add(set);
        }
        sqlTemplate.setRunSqlPlaceholders(runSqlPlaceholders);
        sqlTemplate.setRollbackSqlPlaceholders(rollbackSqlPlaceholders);
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

    public ApiResponse updateSqlTemplate(Map<String, String> map) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        String templateId = map.get("templateId");
        SqlTemplate sqlTemplate = runtimeCacheService.getSqlTemplate(templateId);
        if (sqlTemplate == null) {
            apiResponse.setErrorCode("999999");
            apiResponse.setErrorMsg("sql模板已存在");
            return apiResponse;
        }
        String templateInfo = map.get("templateInfo");
        String runSqlContent = map.get("runSqlContent");
        String rollbackSqlContent = map.get("rollbackSqlContent");

        //修改缓存并改写目录下的文件
        synchronized (LockFactory.getLock("sqltemplate")) {
            runtimeCacheService.getSqlTemplateMap().put(templateId, sqlTemplate);
            if (!StringUtils.isEmpty(templateInfo)) {
                sqlTemplate.setTemplateInfo(templateInfo);
                String templateInfoFilePath = sqlTemplate.getTemplateInfoFilePath();
                List<String> templateInfoLines = MyFileUtils.convertStringToLines(templateInfo);
                MyFileUtils.writeLinesToFileFromHead(templateInfoLines, templateInfoFilePath, "utf-8");
            }
            if (!StringUtils.isEmpty(runSqlContent)) {
                sqlTemplate.setRunSqlContent(runSqlContent);
                String runSqlFilePath = sqlTemplate.getRunSqlFilePath();
                List<String> runSqlLines = MyFileUtils.convertStringToLines(runSqlContent);
                MyFileUtils.writeLinesToFileFromHead(runSqlLines, runSqlFilePath, "utf-8");
            }
            if (!StringUtils.isEmpty(rollbackSqlContent)) {
                sqlTemplate.setRollbackSqlContent(rollbackSqlContent);
                String rollbackSqlFilePath = sqlTemplate.getRollbackSqlFilePath();
                List<String> rollbackSqlLines = MyFileUtils.convertStringToLines(rollbackSqlContent);
                MyFileUtils.writeLinesToFileFromHead(rollbackSqlLines, rollbackSqlFilePath, "utf-8");
            }
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