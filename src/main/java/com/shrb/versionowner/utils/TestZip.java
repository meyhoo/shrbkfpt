package com.shrb.versionowner.utils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TestZip {
    public static void main(String[] args) throws Exception {
        String entry = "D:\\javaProjects\\versionowner\\src\\main\\resources\\devTemplate";//需要压缩的文件目录
        String outPath = "D:\\testFile\\version_20210325.zip";
        String base = "version_20210325";
        compressZip(entry, outPath, base);
    }

    /**
     * 压缩文件目录
     * @param srcPath 原目录路径
     * @param destPath 生成目标zip文件路径
     * @param baseDirName zip包一级目录名
     * @throws Exception
     */
    public static void compressZip(String srcPath, String destPath, String baseDirName) throws Exception {
        ZipOutputStream zipOutput = null;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            File file = new File(srcPath);
            fos = new FileOutputStream(destPath);
            bos = new BufferedOutputStream(fos);
            zipOutput = new ZipOutputStream(bos);
            compressZip(zipOutput, file, baseDirName);
            zipOutput.closeEntry();
            zipOutput.close();
        } catch (Exception e) {
            throw e;
        } finally {
            if (zipOutput!=null) {
                zipOutput.close();
            }
            if (bos!=null) {
                bos.close();
            }
            if (fos!=null) {
                fos.close();
            }
        }
    }

    private static void compressZip(ZipOutputStream zipOutput, File file, String base) throws IOException {
        if(file.isDirectory()){
            File[] listFiles = file.listFiles();// 列出所有的文件
            if (listFiles == null || listFiles.length == 0) {
                zipOutput.putNextEntry(new ZipEntry(base+"/"));
                zipOutput.closeEntry();
            }
            for(File fi : listFiles){
                if(fi.isDirectory()){
                    compressZip(zipOutput, fi, base + "/" + fi.getName());
                }else{
                    zip(zipOutput, fi, base);
                }
            }
        }else{
            zip(zipOutput, file, base);
        }
    }

    private static void zip(ZipOutputStream zipOutput, File file, String base) throws IOException, FileNotFoundException {
        ZipEntry zEntry = new ZipEntry(base + File.separator + file.getName());
        zipOutput.putNextEntry(zEntry);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        byte[] buffer = new byte[1024];
        int read = 0;
        while((read =bis.read(buffer)) != -1){
            zipOutput.write(buffer, 0, read);
        }
        bis.close();
    }
}
