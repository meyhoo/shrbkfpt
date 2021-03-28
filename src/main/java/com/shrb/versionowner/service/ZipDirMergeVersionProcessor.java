package com.shrb.versionowner.service;

import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.utils.MyFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;

/**
 * 合并 /zip 目录
 */
@Component
public class ZipDirMergeVersionProcessor implements IMergeVersionProcessor {

    private static final Logger log = LoggerFactory.getLogger(ZipDirMergeVersionProcessor.class);

    @Autowired
    private Configuration configuration;

    @Override
    public void invoke(String versionId, List<String> committerNameList) throws Exception {
        String administratorVersionBasePath = configuration.getAdministratorVersionBasePath();
        String developerVersionBasePath = configuration.getDeveloperVersionBasePath();
        String adminDirPath = administratorVersionBasePath + versionId + "/versionContent/" + versionId + "/zip/";
        MyFileUtils.createFile(adminDirPath);
        Map<String, List<String>> committerZipMap = new HashMap<>();
        for (String currentCommitterName : committerNameList) {
            String devDirPath = developerVersionBasePath + currentCommitterName + "/" + versionId + "/versionContent/" + versionId + "/zip/";
            File dir = new File(devDirPath);
            if (!dir.exists()) {
                continue;
            }
            File[] zipFiles = dir.listFiles();
            List<String> zipNameList = new ArrayList<>();
            for (File zipFile : zipFiles) {
                zipNameList.add(zipFile.getName());
            }
            committerZipMap.put(currentCommitterName, zipNameList);
        }
        //检查重复
        //没有提交重复的关系集合，<zip包名, 提交者>
        Map<String, String> zipCommitterMap = new HashMap<>();
        //提交重名的关系集合，<zip包名, [提交者]>
        Map<String, Set<String>> repeatZipMap = new HashMap<>();
        List<String> writeLines = new ArrayList<>();
        committerZipMap.forEach((committerName, zipList) -> {
            writeLines.add(">> " + committerName);
            int count = 0;
            for (String zipName : zipList) {
                writeLines.add(" " + zipName);
                count++;
                String name = zipCommitterMap.get(zipName);
                if (StringUtils.isEmpty(name)) {
                    zipCommitterMap.put(zipName, committerName);
                } else {
                    Set<String> repeatCommitterSet = repeatZipMap.get(zipName);
                    if (repeatCommitterSet == null) {
                        repeatCommitterSet = new HashSet<>();
                    }
                    repeatCommitterSet.add(committerName);
                    repeatCommitterSet.add(name);
                    repeatZipMap.put(zipName, repeatCommitterSet);
                    zipCommitterMap.remove(zipName);
                }
            }
            writeLines.add("总数：" + count + " 个\n\n");
        });
        //先批量拷贝过去，重复的文件覆盖掉
        for (String currentCommitterName : committerNameList) {
            String devDirPath = developerVersionBasePath + currentCommitterName + "/" + versionId + "/versionContent/" + versionId + "/zip/";
            File dir = new File(devDirPath);
            if (!dir.exists()) {
                continue;
            }
            MyFileUtils.copyFolder(devDirPath, adminDirPath);
        }
        if (repeatZipMap.size() != 0) {
            repeatZipMap.forEach((zipName, committerNameSet) -> {
                writeLines.add("================= 重复提交 =================");
                String repeatZipFilePath = adminDirPath + zipName;
                File repeatZipFile = new File(repeatZipFilePath);
                //重复的文件删掉
                repeatZipFile.delete();
                for (String currentCommitterName : committerNameSet) {
                    String devDirPath = developerVersionBasePath + currentCommitterName + "/" + versionId + "/versionContent/" + versionId + "/zip/";
                    String repeatCommitZipFilePath = devDirPath + zipName;
                    Integer index = zipName.lastIndexOf(".");
                    String newZipName = zipName.substring(0, index) + "-" + currentCommitterName + ".zip";
                    writeLines.add(newZipName);
                    String newZipFilePath = adminDirPath + newZipName;
                    try {
                        MyFileUtils.copyFile(repeatCommitZipFilePath, newZipFilePath);
                    } catch (Exception e) {
                        log.error("MyFileUtils.copyFile({}, {}) 抛出异常", repeatCommitZipFilePath, newZipFilePath, e);
                    }
                }
            });
        }
        String mergeInfoFilePath = adminDirPath + "合并报告.txt";
        MyFileUtils.writeLinesToFileFromHead(writeLines, mergeInfoFilePath, "utf-8");
    }

}
