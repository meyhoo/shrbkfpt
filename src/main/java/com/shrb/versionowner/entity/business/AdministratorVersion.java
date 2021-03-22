package com.shrb.versionowner.entity.business;

public class AdministratorVersion {
    private String versionId;
    private String versionInfo;
    private String versionInfoFilePath;

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getVersionInfo() {
        return versionInfo;
    }

    public void setVersionInfo(String versionInfo) {
        this.versionInfo = versionInfo;
    }

    public String getVersionInfoFilePath() {
        return versionInfoFilePath;
    }

    public void setVersionInfoFilePath(String versionInfoFilePath) {
        this.versionInfoFilePath = versionInfoFilePath;
    }
}
