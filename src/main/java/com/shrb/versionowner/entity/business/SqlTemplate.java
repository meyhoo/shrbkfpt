package com.shrb.versionowner.entity.business;

import com.shrb.versionowner.utils.MyFileUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlTemplate {

    private static final Pattern pattern = Pattern.compile("(#\\{)(.*?)(})");

    private String templateId;
    private String templateInfoFilePath;
    private String templateInfo;
    private String runSqlFilePath;
    private String runSqlContent;
    private List<Set<String>> runSqlPlaceholders;
    private String rollbackSqlFilePath;
    private String rollbackSqlContent;
    private List<Set<String>> rollbackSqlPlaceholders;

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getRunSqlFilePath() {
        return runSqlFilePath;
    }

    public void setRunSqlFilePath(String runSqlFilePath) {
        this.runSqlFilePath = runSqlFilePath;
    }

    public String getRollbackSqlFilePath() {
        return rollbackSqlFilePath;
    }

    public void setRollbackSqlFilePath(String rollbackSqlFilePath) {
        this.rollbackSqlFilePath = rollbackSqlFilePath;
    }

    public String getRunSqlContent() {
        return runSqlContent;
    }

    /**
     * setter会自动注入最新的placeholders
     * @param runSqlContent
     */
    public void setRunSqlContent(String runSqlContent) {
        this.runSqlContent = runSqlContent;
        List<String> lines = MyFileUtils.convertStringToLines(runSqlContent);
        List<Set<String>> placeholders = getPlaceholders(lines);
        this.runSqlPlaceholders = placeholders;
    }

    public String getRollbackSqlContent() {
        return rollbackSqlContent;
    }

    public void setRollbackSqlContent(String rollbackSqlContent) {
        this.rollbackSqlContent = rollbackSqlContent;
        List<String> lines = MyFileUtils.convertStringToLines(rollbackSqlContent);
        List<Set<String>> placeholders = getPlaceholders(lines);
        this.rollbackSqlPlaceholders = placeholders;
    }

    public List<Set<String>> getRunSqlPlaceholders() {
        return runSqlPlaceholders;
    }

    public void setRunSqlPlaceholders(List<Set<String>> runSqlPlaceholders) {
        this.runSqlPlaceholders = runSqlPlaceholders;
    }

    public List<Set<String>> getRollbackSqlPlaceholders() {
        return rollbackSqlPlaceholders;
    }

    public void setRollbackSqlPlaceholders(List<Set<String>> rollbackSqlPlaceholders) {
        this.rollbackSqlPlaceholders = rollbackSqlPlaceholders;
    }

    public String getTemplateInfo() {
        return templateInfo;
    }

    public void setTemplateInfo(String templateInfo) {
        this.templateInfo = templateInfo;
    }

    public String getTemplateInfoFilePath() {
        return templateInfoFilePath;
    }

    public void setTemplateInfoFilePath(String templateInfoFilePath) {
        this.templateInfoFilePath = templateInfoFilePath;
    }

    private List<Set<String>> getPlaceholders(List<String> lines) {
        List<Set<String>> placeholders = new ArrayList<>();
        for(String line : lines) {
            Set<String> set = new HashSet<>();
            Matcher m = pattern.matcher(line);
            while (m.find()) {
                set.add(m.group(2));
            }
            placeholders.add(set);
        }
        return placeholders;
    }
}
