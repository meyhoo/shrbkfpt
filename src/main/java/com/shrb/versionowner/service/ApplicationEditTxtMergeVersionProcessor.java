package com.shrb.versionowner.service;

import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.utils.MyFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 合并 /应用修改.txt
 */
@Component
public class ApplicationEditTxtMergeVersionProcessor implements IMergeVersionProcessor {

    @Autowired
    private Configuration configuration;

    @Override
    public void invoke(String versionId, List<String> committerNameList) throws Exception {
        String adminFilePath = configuration.getAdministratorVersionBasePath() + versionId + "/versionContent/" + versionId + "/应用修改.txt";
        MyFileUtils.createFile(adminFilePath);
        //<应用名, [{提交者, [提交内容]}]>
        Map<String, List<ApplicationEditTxtReadResult>> linesMap = new HashMap<>();
        for (String currentCommitterName : committerNameList) {
            String devFilePath = configuration.getDeveloperVersionBasePath() + currentCommitterName + "/" + versionId + "/versionContent/" + versionId + "/应用修改.txt";
            File file = new File(devFilePath);
            if (!file.exists()) {
                continue;
            }
            List<String> lines = MyFileUtils.readFileAllLines(devFilePath, "utf-8");
            //<应用名, [提交内容]>
            Map<String, List<String>> applicationEditMap = new HashMap<>();
            //应用归类
            for (String line : lines) {
                String applicationName = getApplicationName(line);
                List<String> currentApplicationEditLines = applicationEditMap.get(applicationName);
                if (currentApplicationEditLines == null) {
                    currentApplicationEditLines = new ArrayList<>();
                }
                currentApplicationEditLines.add(line);
                applicationEditMap.put(applicationName, currentApplicationEditLines);
            }
            applicationEditMap.forEach((applicationNameKey, editList) -> {
                List<ApplicationEditTxtReadResult> applicationEditTxtReadResultList = linesMap.get(applicationNameKey);
                if (applicationEditTxtReadResultList == null) {
                    applicationEditTxtReadResultList = new ArrayList<>();
                }
                ApplicationEditTxtReadResult applicationEditTxtReadResult = new ApplicationEditTxtReadResult();
                applicationEditTxtReadResult.committerName = currentCommitterName;
                applicationEditTxtReadResult.lines = editList;
                applicationEditTxtReadResultList.add(applicationEditTxtReadResult);
                linesMap.put(applicationNameKey, applicationEditTxtReadResultList);
            });
        }
        List<String> writeLines = new ArrayList<>();
        StringBuffer title = new StringBuffer();
        title.append("===========================================\n");
        title.append(versionId).append(" 批次涉及应用改动清单：\n  ");
        linesMap.forEach((applicationName, applicationEditTxtReadResultList) -> {
            title.append(applicationName).append("、");
        });
        title.append("\n===========================================\n\n");
        writeLines.add(title.toString());
        linesMap.forEach((applicationName, applicationEditTxtReadResultList) -> {
            StringBuffer applicationTitle = new StringBuffer();
            applicationTitle.append("######### ").append(applicationName).append(" #########");
            writeLines.add(applicationTitle.toString());
            for (ApplicationEditTxtReadResult applicationEditTxtReadResult : applicationEditTxtReadResultList) {
                StringBuffer committerBegin = new StringBuffer();
                committerBegin.append(">> ").append(applicationEditTxtReadResult.committerName);
                writeLines.add(committerBegin.toString());
                for (String line : applicationEditTxtReadResult.lines) {
                    writeLines.add(line);
                }
                writeLines.add("\n");
            }
            writeLines.add("\n\n");
        });
        MyFileUtils.writeLinesToFileFromHead(writeLines, adminFilePath, "utf-8");
    }

    private static class ApplicationEditTxtReadResult {
        public String committerName;
        public List<String> lines;
    }

    private static String getApplicationName(String line) {
        if (StringUtils.isEmpty(line)) {
            return "";
        }
        String[] units = line.split("/");
        if (line.startsWith("/")) {
            return units[1];
        }
        return units[0];
    }

}
