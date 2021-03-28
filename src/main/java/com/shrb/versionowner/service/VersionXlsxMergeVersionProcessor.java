package com.shrb.versionowner.service;

import com.shrb.versionowner.entity.configuration.Configuration;
import com.shrb.versionowner.utils.MyFileUtils;
import com.shrb.versionowner.utils.PoiUtils;
import com.shrb.versionowner.utils.TxtWriteUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

/**
 * 合并 /DOC/版本部署手册.xlsx
 *           >>版本部署手册.txt
 */
@Component
public class VersionXlsxMergeVersionProcessor implements IMergeVersionProcessor {

    private final static String SHEET1 = "1-服务分类";
    private final static String SHEET2 = "2-服务下发";
    private final static String SHEET3 = "3-服务发布";
    private final static String SHEET4 = "4-新增系统";
    private final static String SHEET5 = "5-新增服务分类";
    private final static String SHEET6 = "6-其他部署";

    private final static String bars = "==============================================================================================";
    private final static String paddingLeft = "        ";
    private final static Integer defaultLength = 40;

    @Autowired
    private Configuration configuration;

    @Override
    public void invoke(String versionId, List<String> committerNameList) throws Exception {
        String administratorVersionBasePath = configuration.getAdministratorVersionBasePath();
        String developerVersionBasePath = configuration.getDeveloperVersionBasePath();
        String adminFilePath = administratorVersionBasePath + versionId + "/versionContent/" + versionId + "/DOC/版本部署手册.txt";
        MyFileUtils.createFile(adminFilePath);
        // <sheet名, [行]>
        Map<String, List<String>> writeMap = new HashMap<>();
        for (String currentCommitterName : committerNameList) {
            String devFilePath = developerVersionBasePath + currentCommitterName + "/" + versionId + "/versionContent/" + versionId + "/DOC/版本部署手册.xlsx";
            File file = new File(devFilePath);
            if (!file.exists()) {
                continue;
            }

            List<List<String>> resultList1 = PoiUtils.readXlsxFile(devFilePath, SHEET1);
            List<List<String>> resultList2 = PoiUtils.readXlsxFile(devFilePath, SHEET2);
            List<List<String>> resultList3 = PoiUtils.readXlsxFile(devFilePath, SHEET3);
            List<List<String>> resultList4 = PoiUtils.readXlsxFile(devFilePath, SHEET4);
            List<List<String>> resultList5 = PoiUtils.readXlsxFile(devFilePath, SHEET5);
            List<List<String>> resultList6 = PoiUtils.readXlsxFile(devFilePath, SHEET6);

            List<String> resultLines1 = writeMap.get(SHEET1);
            if (resultLines1 == null) {
                resultLines1 = new ArrayList<>();
            }
            List<String> resultLines2 = writeMap.get(SHEET2);
            if (resultLines2 == null) {
                resultLines2 = new ArrayList<>();
            }
            List<String> resultLines3 = writeMap.get(SHEET3);
            if (resultLines3 == null) {
                resultLines3 = new ArrayList<>();
            }
            List<String> resultLines4 = writeMap.get(SHEET4);
            if (resultLines4 == null) {
                resultLines4 = new ArrayList<>();
            }
            List<String> resultLines5 = writeMap.get(SHEET5);
            if (resultLines5 == null) {
                resultLines5 = new ArrayList<>();
            }
            List<String> resultLines6 = writeMap.get(SHEET6);
            if (resultLines6 == null) {
                resultLines6 = new ArrayList<>();
            }

            for (int i = 1; i < resultList1.size(); i++) {
                List<String> fieldList = resultList1.get(i);
                if (fieldList == null || fieldList.size() == 0) {
                    continue;
                }
                StringBuffer line = new StringBuffer();
                line.append(paddingLeft);
                line.append(TxtWriteUtils.appentStr4Length(fieldList.get(0), defaultLength));
                line.append(TxtWriteUtils.appentStr4Length(fieldList.get(1), defaultLength));
                line.append(TxtWriteUtils.appentStr4Length(fieldList.get(2), defaultLength));
                line.append(TxtWriteUtils.appentStr4Length(currentCommitterName, defaultLength));
                resultLines1.add(line.toString());
            }

            for (int i = 1; i < resultList2.size(); i++) {
                List<String> fieldList = resultList2.get(i);
                if (fieldList == null || fieldList.size() == 0) {
                    continue;
                }
                StringBuffer line = new StringBuffer();
                line.append(paddingLeft);
                line.append(TxtWriteUtils.appentStr4Length(fieldList.get(0), defaultLength));
                line.append(TxtWriteUtils.appentStr4Length(fieldList.get(1), defaultLength));
                line.append(TxtWriteUtils.appentStr4Length(fieldList.get(2), defaultLength));
                line.append(TxtWriteUtils.appentStr4Length(currentCommitterName, defaultLength));
                resultLines2.add(line.toString());
            }

            StringBuffer publishServiceLine = new StringBuffer();
            publishServiceLine.append(">> " + currentCommitterName + ":       ");
            int init = 1;
            for (int i = 1; i < resultList3.size(); i++) {
                List<String> fieldList = resultList3.get(i);
                if (fieldList == null || fieldList.size() == 0) {
                    init++;
                    continue;
                }
                String serviceName = fieldList.get(0);
                if (i == init) {
                    publishServiceLine.append(serviceName);
                } else {
                    publishServiceLine.append(",").append(serviceName);
                }
            }
            resultLines3.add(publishServiceLine.toString());

            for (int i = 1; i < resultList4.size(); i++) {
                List<String> fieldList = resultList4.get(i);
                if (fieldList == null || fieldList.size() == 0) {
                    continue;
                }
                StringBuffer line = new StringBuffer();
                line.append(paddingLeft);
                line.append(TxtWriteUtils.appentStr4Length(fieldList.get(0), defaultLength));
                line.append(TxtWriteUtils.appentStr4Length(fieldList.get(1), defaultLength));
                line.append(TxtWriteUtils.appentStr4Length(fieldList.get(2), defaultLength));
                line.append(TxtWriteUtils.appentStr4Length(fieldList.get(3), defaultLength));
                line.append(TxtWriteUtils.appentStr4Length(currentCommitterName, defaultLength));
                resultLines4.add(line.toString());
            }

            for (int i = 1; i < resultList5.size(); i++) {
                List<String> fieldList = resultList5.get(i);
                if (fieldList == null || fieldList.size() == 0) {
                    continue;
                }
                StringBuffer line = new StringBuffer();
                line.append(paddingLeft);
                line.append(TxtWriteUtils.appentStr4Length(fieldList.get(0), defaultLength));
                line.append(TxtWriteUtils.appentStr4Length(fieldList.get(1), defaultLength));
                line.append(TxtWriteUtils.appentStr4Length(currentCommitterName, defaultLength));
                resultLines5.add(line.toString());
            }

            for (int i = 1; i < resultList6.size(); i++) {
                List<String> fieldList = resultList6.get(i);
                if (fieldList == null || fieldList.size() == 0) {
                    continue;
                }
                StringBuffer line = new StringBuffer();
                line.append(">> " + currentCommitterName + ":  ");
                line.append(TxtWriteUtils.appentStr4Length(fieldList.get(0), defaultLength));
                resultLines6.add(line.toString());
            }

            writeMap.put(SHEET1, resultLines1);
            writeMap.put(SHEET2, resultLines2);
            writeMap.put(SHEET3, resultLines3);
            writeMap.put(SHEET4, resultLines4);
            writeMap.put(SHEET5, resultLines5);
            writeMap.put(SHEET6, resultLines6);
        }

        List<String> writeLines = new ArrayList<>();

        writeLines.add("一、zip部署");
        writeLines.add("\n1. 浏览器登录 http://10.32.128.23:8081/esbconsole (ZSC)");
        writeLines.add("2. 浏览器登录 http://10.32.68.109:8081/esbconsole 、  http://10.32.68.110:8081/esbconsole (SC)");
        writeLines.add("3. 浏览器登录 http://10.10.98.51:8081/esbconsole (ZB)\n");
        writeLines.add("\n1. 新建工作区 -> 激活 -> zip包导入 -> 审核");
        writeLines.add("2. 新增系统");
        writeLines.add(bars);
        writeLines.add(getColumns("系统ID", "中文名称", "服务类型", "报文类型", "提交者"));
        writeLines.add(bars);
        List<String> addSystemList = writeMap.get(SHEET4);
        sortList(addSystemList);
        writeLines.addAll(addSystemList);
        writeLines.add("\n3. 新增服务分类");
        writeLines.add(bars);
        writeLines.add(getColumns("分类ID", "分类名称", "提交者"));
        writeLines.add(bars);
        List<String> addServiceTypeList = writeMap.get(SHEET5);
        sortList(addServiceTypeList);
        writeLines.addAll(addServiceTypeList);
        writeLines.add("\n4. zip包服务分类");
        writeLines.add(bars);
        writeLines.add(getColumns("维度ID", "分类ID", "服务名", "提交者"));
        writeLines.add(bars);
        List<String> zipServiceTypeList = writeMap.get(SHEET1);
        sortList(zipServiceTypeList);
        writeLines.addAll(zipServiceTypeList);
        writeLines.add("\n5. zip包下发");
        writeLines.add(bars);
        writeLines.add(getColumns("维度ID", "分类ID", "服务名", "提交者"));
        writeLines.add(bars);
        List<String> serviceIssueList = writeMap.get(SHEET2);
        sortList(serviceIssueList);
        writeLines.addAll(serviceIssueList);
        writeLines.add("\n6. 服务发布（需要先跑portal库的脚本）");
        List<String> servicePublishList = writeMap.get(SHEET3);
        writeLines.addAll(servicePublishList);
        writeLines.add("\n二、应用部署");
        writeLines.add("\n1. 生产环境清单");
        //TODO 待优化  新增生产应用配置
        writeLines.add("\n2. 需要部署的应用清单");
        //TODO 待优化  新增本批次要部署的应用
        writeLines.add("\n三、其他部署");
        List<String> otherList = writeMap.get(SHEET6);
        writeLines.addAll(otherList);

        MyFileUtils.writeLinesToFileFromHead(writeLines, adminFilePath, "utf-8");
    }

    private void sortList(List<String> list) {
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String d1, String d2) {
                int i = d1.compareTo(d2);
                return i>0?1:-1;
            }
        });
    }

    private String getColumns(String... args) throws Exception {
        String result = paddingLeft;
        for (String s : args) {
            result = result + TxtWriteUtils.appentStr4Length(s, defaultLength);
        }
        return result;
    }

}
