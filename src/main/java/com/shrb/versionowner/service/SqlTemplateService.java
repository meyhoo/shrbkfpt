package com.shrb.versionowner.service;

import com.shrb.versionowner.entity.business.SqlTemplate;
import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.utils.MyFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SqlTemplateService {

    private static final Logger log = LoggerFactory.getLogger(SqlTemplateService.class);

    private static final String RUN_SQL_FILE_NAME = "run.sql";

    private static final String ROLLBACK_SQL_FILE_NAME = "rollback.sql";

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
            String templateId = dir.getName();
            SqlTemplate sqlTemplate = new SqlTemplate();
            sqlTemplate.setTemplateId(templateId);
            String runSqlFilePath = dirPath+File.separator+RUN_SQL_FILE_NAME;
            List<String> runSqlContent = MyFileUtils.readFileAllLines(runSqlFilePath, "utf-8");
            List<Set<String>> runSqlPlaceholders = new ArrayList<>();
            for(String line : runSqlContent) {
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
            List<String> rollbackSqlContent = MyFileUtils.readFileAllLines(rollbackSqlFilePath, "utf-8");
            List<Set<String>> rollbackSqlPlaceholders = new ArrayList<>();
            for(String line : rollbackSqlContent) {
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
}
