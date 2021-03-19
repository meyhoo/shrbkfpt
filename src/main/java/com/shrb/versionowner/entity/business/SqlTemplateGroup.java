package com.shrb.versionowner.entity.business;

import java.util.ArrayList;
import java.util.List;

public class SqlTemplateGroup {
    private String templateGroupId;
    private String templateGroupInfoFilePath;
    private String templateGroupInfo;
    private String templateIdsFilePath;
    private List<String> templateIds;

    public String getTemplateGroupId() {
        return templateGroupId;
    }

    public void setTemplateGroupId(String templateGroupId) {
        this.templateGroupId = templateGroupId;
    }

    public String getTemplateGroupInfoFilePath() {
        return templateGroupInfoFilePath;
    }

    public void setTemplateGroupInfoFilePath(String templateGroupInfoFilePath) {
        this.templateGroupInfoFilePath = templateGroupInfoFilePath;
    }

    public String getTemplateGroupInfo() {
        return templateGroupInfo;
    }

    public void setTemplateGroupInfo(String templateGroupInfo) {
        this.templateGroupInfo = templateGroupInfo;
    }

    public String getTemplateIdsFilePath() {
        return templateIdsFilePath;
    }

    public void setTemplateIdsFilePath(String templateIdsFilePath) {
        this.templateIdsFilePath = templateIdsFilePath;
    }

    public List<String> getTemplateIds() {
        return templateIds;
    }

    public void setTemplateIds(List<String> templateIds) {
        this.templateIds = templateIds;
    }

    /**
     * 用于前端、本地缓存、文件的数据格式转换 String→List→String
     * @return
     */
    public String getTemplateIdStr() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.templateIds.size(); i++) {
            if (i == this.templateIds.size() - 1) {
                sb.append(this.templateIds.get(i));
            } else {
                sb.append(this.templateIds.get(i)).append("|");
            }
        }
        return sb.toString();
    }

    public void setTemplateIdStr(String templateIdStr) {
        List<String> newTemplateIds = new ArrayList<>();
        String[] templateIdArray = templateIdStr.split("\\|");
        for (String templateId : templateIdArray) {
            newTemplateIds.add(templateId);
        }
        this.templateIds = newTemplateIds;
    }

}
