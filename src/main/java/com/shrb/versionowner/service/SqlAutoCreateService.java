package com.shrb.versionowner.service;

import com.alibaba.fastjson.JSONObject;
import com.shrb.versionowner.entity.api.ApiResponse;
import com.shrb.versionowner.entity.business.SqlTemplate;
import com.shrb.versionowner.entity.business.SqlTemplateGroup;
import com.shrb.versionowner.utils.MyFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SqlAutoCreateService {
    private static final Logger log = LoggerFactory.getLogger(SqlAutoCreateService.class);

    @Autowired
    private RuntimeCacheService runtimeCacheService;

    /**
     * 解析templateGroup，获取完整的runSqlContent、rollbackSqlContent、placeholders
     * @param templateGroupId
     * @return
     * @throws Exception
     */
    public Map<String, Object> analysisTemplateGroup(String templateGroupId) throws Exception {
        SqlTemplateGroup sqlTemplateGroup = runtimeCacheService.getSqlTemplateGroup(templateGroupId);
        if (sqlTemplateGroup == null) {
            throw new Exception("找不到对应的sql模板组：" + templateGroupId);
        }
        List<String> templateIdList = sqlTemplateGroup.getTemplateIds();
        List<String> runSqlLineList = new ArrayList<>();
        List<String> rollbackSqlLineList = new ArrayList<>();
        Set<String> placeholders = new HashSet<>();
        for (String templateId : templateIdList) {
            SqlTemplate sqlTemplate = runtimeCacheService.getSqlTemplate(templateId);
            if (sqlTemplate == null) {
                throw new Exception("找不到对应的sql模板：" + templateId);
            }
            List<String> runSqlLines = MyFileUtils.convertStringToLines(sqlTemplate.getRunSqlContent());
            List<String> rollbackSqlLines = MyFileUtils.convertStringToLines(sqlTemplate.getRollbackSqlContent());
            runSqlLineList.addAll(runSqlLines);
            rollbackSqlLineList.addAll(rollbackSqlLines);
            List<Set<String>> runSqlPlaceholders = sqlTemplate.getRunSqlPlaceholders();
            List<Set<String>> rollbackSqlPlaceholders = sqlTemplate.getRollbackSqlPlaceholders();
            for (Set<String> set : runSqlPlaceholders) {
                placeholders.addAll(set);
            }
            for (Set<String> set : rollbackSqlPlaceholders) {
                placeholders.addAll(set);
            }
        }
        List<String> placeholderList = new ArrayList<>(placeholders);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("placeholders", placeholderList);
        resultMap.put("runSqlContent", MyFileUtils.convertLinesToString(runSqlLineList));
        resultMap.put("rollbackSqlContent", MyFileUtils.convertLinesToString(rollbackSqlLineList));
        return resultMap;
    }

    public ApiResponse createSql(JSONObject jsonObject) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        String runSqlTemplateContent = jsonObject.getString("runSqlContent");
        String rollbackSqlTemplateContent = jsonObject.getString("rollbackSqlContent");
        jsonObject.remove("runSqlContent");
        jsonObject.remove("rollbackSqlContent");
        for(Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            String regex = "#\\{"+entry.getKey()+"}";
            runSqlTemplateContent = runSqlTemplateContent.replaceAll(regex, entry.getValue().toString());
            rollbackSqlTemplateContent = rollbackSqlTemplateContent.replaceAll(regex, entry.getValue().toString());
        }
        JSONObject data = new JSONObject();
        data.put("runSqlContent", runSqlTemplateContent);
        data.put("rollbackSqlContent", rollbackSqlTemplateContent);
        apiResponse.setData(data);
        return apiResponse;
    }
}
