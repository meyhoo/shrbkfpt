package com.shrb.versionowner.entity.file;

/**
 * 记录path识别结果信息
 */
public class PathJudgeResult {
    private String path;
    private Boolean dirFlag;
    private String fileName;
    private String fileType;
    private String basePath;
    private Boolean existFlag;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getDirFlag() {
        return dirFlag;
    }

    public void setDirFlag(Boolean dirFlag) {
        this.dirFlag = dirFlag;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public Boolean getExistFlag() {
        return existFlag;
    }

    public void setExistFlag(Boolean existFlag) {
        this.existFlag = existFlag;
    }
}
