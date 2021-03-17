package com.shrb.versionowner.entity.business;

import java.util.List;
import java.util.Set;

public class SqlTemplate {
    private String templateId;
    private String runSqlFilePath;
    private List<String> runSqlContent;
    private List<Set<String>> runSqlPlaceholders;
    private String rollbackSqlFilePath;
    private List<String> rollbackSqlContent;
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

    public List<String> getRunSqlContent() {
        return runSqlContent;
    }

    public void setRunSqlContent(List<String> runSqlContent) {
        this.runSqlContent = runSqlContent;
    }

    public List<String> getRollbackSqlContent() {
        return rollbackSqlContent;
    }

    public void setRollbackSqlContent(List<String> rollbackSqlContent) {
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
}
