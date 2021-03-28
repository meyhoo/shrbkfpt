package com.shrb.versionowner.service;


import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.utils.MyFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * 合并 /DOC/开放存版本 目录
 * 合并 /DOC/其他类型文件/部署包 目录
 * 合并 /DOC/其他类型文件/回滚包 目录
 * 合并 /DOC/手动部署Jar/部署包 目录
 * 合并 /DOC/手动部署Jar/回滚包 目录
 * 合并 /DOC/手动部署sql/部署包 目录
 * 合并 /DOC/手动部署sql/回滚包 目录
 */
@Component
public class DocDirMergeVersionProcessor implements IMergeVersionProcessor {

    @Autowired
    private Configuration configuration;

    @Override
    public void invoke(String versionId, List<String> committerNameList) throws Exception {
        String administratorVersionBasePath = configuration.getAdministratorVersionBasePath();
        String adminDirPath = administratorVersionBasePath + versionId + "/versionContent/" + versionId + "/DOC/";
        MyFileUtils.createFile(adminDirPath);
        String kfcVersionDirPath = adminDirPath + "开放存版本/";
        String otherFileRunDirPath = adminDirPath + "其他类型文件/部署包/";
        String otherFileRollbackDirPath = adminDirPath + "其他类型文件/回滚包/";
        String manualJarRunDirPath = adminDirPath + "手动部署Jar/部署包/";
        String manualJarRollbackDirPath = adminDirPath + "手动部署Jar/回滚包/";
        String manualSqlRunDirPath = adminDirPath + "手动部署sql/部署包/";
        String manualSqlRollbackDirPath = adminDirPath + "手动部署sql/回滚包/";
        for (String currentCommitterName : committerNameList) {
            String developerVersionBasePath = configuration.getDeveloperVersionBasePath();
            String devDirPath = developerVersionBasePath + currentCommitterName + "/" + versionId + "/versionContent/" + versionId + "/DOC/";
            String devKfcVersionDirPath = devDirPath + "开放存版本/";
            String devOtherFileRunDirPath = devDirPath + "其他类型文件/部署包/";
            String devOtherFileRollbackDirPath = devDirPath + "其他类型文件/回滚包/";
            String devManualJarRunDirPath = devDirPath + "手动部署Jar/部署包/";
            String devManualJarRollbackDirPath = devDirPath + "手动部署Jar/回滚包/";
            String devManualSqlRunDirPath = devDirPath + "手动部署sql/部署包/";
            String devManualSqlRollbackDirPath = devDirPath + "手动部署sql/回滚包/";
            File devKfcVersionDir = new File(devKfcVersionDirPath);
            File devOtherFileRunDir = new File(devOtherFileRunDirPath);
            File devOtherFileRollbackDir = new File(devOtherFileRollbackDirPath);
            File devManualJarRunDir = new File(devManualJarRunDirPath);
            File devManualJarRollbackDir = new File(devManualJarRollbackDirPath);
            File devManualSqlRunDir = new File(devManualSqlRunDirPath);
            File devManualSqlRollbackDir = new File(devManualSqlRollbackDirPath);
            if (devKfcVersionDir.exists()) {
                File[] fileList = devKfcVersionDir.listFiles();
                for (File file : fileList) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        Integer index = fileName.lastIndexOf(".");
                        String newFileName = fileName.substring(0, index) + "_" + currentCommitterName + fileName.substring(index, fileName.length());
                        MyFileUtils.copyFile(file.getAbsolutePath(), kfcVersionDirPath + newFileName);
                    }
                    if (file.isDirectory()) {
                        String dirName = file.getName();
                        String newDirName = dirName + "_" + currentCommitterName;
                        MyFileUtils.copyFolder(file.getAbsolutePath(), kfcVersionDirPath + newDirName);
                    }
                }
            }
            if (devOtherFileRunDir.exists()) {
                File[] fileList = devOtherFileRunDir.listFiles();
                for (File file : fileList) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        Integer index = fileName.lastIndexOf(".");
                        String newFileName = fileName.substring(0, index) + "_" + currentCommitterName + fileName.substring(index, fileName.length());
                        MyFileUtils.copyFile(file.getAbsolutePath(), otherFileRunDirPath + newFileName);
                    }
                    if (file.isDirectory()) {
                        String dirName = file.getName();
                        String newDirName = dirName + "_" + currentCommitterName;
                        MyFileUtils.copyFolder(file.getAbsolutePath(), otherFileRunDirPath + newDirName);
                    }
                }
            }
            if (devOtherFileRollbackDir.exists()) {
                File[] fileList = devOtherFileRollbackDir.listFiles();
                for (File file : fileList) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        Integer index = fileName.lastIndexOf(".");
                        String newFileName = fileName.substring(0, index) + "_" + currentCommitterName + fileName.substring(index, fileName.length());
                        MyFileUtils.copyFile(file.getAbsolutePath(), otherFileRollbackDirPath + newFileName);
                    }
                    if (file.isDirectory()) {
                        String dirName = file.getName();
                        String newDirName = dirName + "_" + currentCommitterName;
                        MyFileUtils.copyFolder(file.getAbsolutePath(), otherFileRollbackDirPath + newDirName);
                    }
                }
            }
            if (devManualJarRunDir.exists()) {
                File[] fileList = devManualJarRunDir.listFiles();
                for (File file : fileList) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        Integer index = fileName.lastIndexOf(".");
                        String newFileName = fileName.substring(0, index) + "_" + currentCommitterName + fileName.substring(index, fileName.length());
                        MyFileUtils.copyFile(file.getAbsolutePath(), manualJarRunDirPath + newFileName);
                    }
                    if (file.isDirectory()) {
                        String dirName = file.getName();
                        String newDirName = dirName + "_" + currentCommitterName;
                        MyFileUtils.copyFolder(file.getAbsolutePath(), manualJarRunDirPath + newDirName);
                    }
                }
            }
            if (devManualJarRollbackDir.exists()) {
                File[] fileList = devManualJarRollbackDir.listFiles();
                for (File file : fileList) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        Integer index = fileName.lastIndexOf(".");
                        String newFileName = fileName.substring(0, index) + "_" + currentCommitterName + fileName.substring(index, fileName.length());
                        MyFileUtils.copyFile(file.getAbsolutePath(), manualJarRollbackDirPath + newFileName);
                    }
                    if (file.isDirectory()) {
                        String dirName = file.getName();
                        String newDirName = dirName + "_" + currentCommitterName;
                        MyFileUtils.copyFolder(file.getAbsolutePath(), manualJarRollbackDirPath + newDirName);
                    }
                }
            }
            if (devManualSqlRunDir.exists()) {
                File[] fileList = devManualSqlRunDir.listFiles();
                for (File file : fileList) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        Integer index = fileName.lastIndexOf(".");
                        String newFileName = fileName.substring(0, index) + "_" + currentCommitterName + fileName.substring(index, fileName.length());
                        MyFileUtils.copyFile(file.getAbsolutePath(), manualSqlRunDirPath + newFileName);
                    }
                    if (file.isDirectory()) {
                        String dirName = file.getName();
                        String newDirName = dirName + "_" + currentCommitterName;
                        MyFileUtils.copyFolder(file.getAbsolutePath(), manualSqlRunDirPath + newDirName);
                    }
                }
            }
            if (devManualSqlRollbackDir.exists()) {
                File[] fileList = devManualSqlRollbackDir.listFiles();
                for (File file : fileList) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        Integer index = fileName.lastIndexOf(".");
                        String newFileName = fileName.substring(0, index) + "_" + currentCommitterName + fileName.substring(index, fileName.length());
                        MyFileUtils.copyFile(file.getAbsolutePath(), manualSqlRollbackDirPath + newFileName);
                    }
                    if (file.isDirectory()) {
                        String dirName = file.getName();
                        String newDirName = dirName + "_" + currentCommitterName;
                        MyFileUtils.copyFolder(file.getAbsolutePath(), manualSqlRollbackDirPath + newDirName);
                    }
                }
            }
        }
    }
}
