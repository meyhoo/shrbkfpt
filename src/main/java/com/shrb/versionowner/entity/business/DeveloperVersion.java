package com.shrb.versionowner.entity.business;

import java.util.List;

public class DeveloperVersion {
    private String versionId;
    private String userId;
    private List<Task> taskList;
    private String taskInfoFilePath;
    private Integer priority;
    private String priorityInfoFilePath;
    private String versionContentDirPath;

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public String getTaskInfoFilePath() {
        return taskInfoFilePath;
    }

    public void setTaskInfoFilePath(String taskInfoFilePath) {
        this.taskInfoFilePath = taskInfoFilePath;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getPriorityInfoFilePath() {
        return priorityInfoFilePath;
    }

    public void setPriorityInfoFilePath(String priorityInfoFilePath) {
        this.priorityInfoFilePath = priorityInfoFilePath;
    }

    public String getVersionContentDirPath() {
        return versionContentDirPath;
    }

    public void setVersionContentDirPath(String versionContentDirPath) {
        this.versionContentDirPath = versionContentDirPath;
    }
}
