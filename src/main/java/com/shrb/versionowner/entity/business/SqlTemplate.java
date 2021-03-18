package com.shrb.versionowner.entity.business;

import java.util.List;
import java.util.Set;

public class SqlTemplate {
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

    public void setRunSqlContent(String runSqlContent) {
        this.runSqlContent = runSqlContent;
    }

    public String getRollbackSqlContent() {
        return rollbackSqlContent;
    }

    public void setRollbackSqlContent(String rollbackSqlContent) {
        this.rollbackSqlContent = rollbackSqlContent;
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
}
