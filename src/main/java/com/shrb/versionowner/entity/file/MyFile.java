package com.shrb.versionowner.entity.file;

/**
 * 对java.io.File对象常用信息做一个简单的包装
 */
public class MyFile {
    private String fileName;
    private String basePath;
    private String fileType;
    private Boolean dirFlag;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Boolean getDirFlag() {
        return dirFlag;
    }

    public void setDirFlag(Boolean dirFlag) {
        this.dirFlag = dirFlag;
    }
}
