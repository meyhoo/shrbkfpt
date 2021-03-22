package com.shrb.versionowner.entity.business;

public class SystemUrl {
    private String systemUrlId;
    private String systemUrlInfo;
    private String devUrl;
    private String testUrl;
    private String zscUrl;
    private String prdUrl;
    private String zbUrl;

    public String getSystemUrlId() {
        return systemUrlId;
    }

    public void setSystemUrlId(String systemUrlId) {
        this.systemUrlId = systemUrlId;
    }

    public String getSystemUrlInfo() {
        return systemUrlInfo;
    }

    public void setSystemUrlInfo(String systemUrlInfo) {
        this.systemUrlInfo = systemUrlInfo;
    }

    public String getDevUrl() {
        return devUrl;
    }

    public void setDevUrl(String devUrl) {
        this.devUrl = devUrl;
    }

    public String getTestUrl() {
        return testUrl;
    }

    public void setTestUrl(String testUrl) {
        this.testUrl = testUrl;
    }

    public String getZscUrl() {
        return zscUrl;
    }

    public void setZscUrl(String zscUrl) {
        this.zscUrl = zscUrl;
    }

    public String getPrdUrl() {
        return prdUrl;
    }

    public void setPrdUrl(String prdUrl) {
        this.prdUrl = prdUrl;
    }

    public String getZbUrl() {
        return zbUrl;
    }

    public void setZbUrl(String zbUrl) {
        this.zbUrl = zbUrl;
    }
}
