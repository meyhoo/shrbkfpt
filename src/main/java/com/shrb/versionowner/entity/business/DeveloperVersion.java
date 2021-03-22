package com.shrb.versionowner.entity.business;

import java.util.List;

public class DeveloperVersion {
    private String versionId;
    private String userId;
    private List<Task> taskList;
    private String taskInfoFilePath;

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
}
