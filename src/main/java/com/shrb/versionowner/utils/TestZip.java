package com.shrb.versionowner.utils;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class TestZip {
    public static void main(String[] args) throws Exception {
        String srcPath = "D:/javaProjects/versionowner/target/classes/devTemplate/devTemplate.zip";
        //String srcPath = "D:/javaProjects/versionowner/src/main/resources/devTemplate/devTemplate.zip";
        String destPath = "D:/versionowner/config/";
        //extractTo(new File(srcPath), new File(destPath));
        MyFileUtils.decompressZip(srcPath, destPath);
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

    @SuppressWarnings("unchecked")
    public static String extractTo( File src, File dest ) throws Exception {
        org.apache.tools.zip.ZipFile zip = new org.apache.tools.zip.ZipFile( src );
        Enumeration< ? extends org.apache.tools.zip.ZipEntry > entries = zip.getEntries();
        org.apache.tools.zip.ZipEntry entry = null;
        File file = null;
        InputStream zis = null;
        String rootPath = dest.getAbsolutePath();
        for ( ; entries.hasMoreElements(); ) {
            entry = entries.nextElement();
            try {
                zis = zip.getInputStream( entry );
                file = new File( dest.getAbsolutePath() + "/" + entry.getName() );
                if ( ! file.getParentFile().exists() ) {
                    file.getParentFile().mkdirs();
                }
                if ( entry.isDirectory() ) {
                    if ( file.getParentFile().getAbsolutePath().equals(
                            dest.getAbsolutePath() ) ) {
                        rootPath = dest.getAbsolutePath();
                    }
                    mkdir( file );
                } else {
                    if(!file.exists()){
                        file.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream( file );
                    int n = 0;
                    byte[] temp = new byte[ 1024 ];
                    while ( ( n = zis.read( temp ) ) != -1 ) {
                        fos.write( temp, 0, n );
                    }
                    fos.close();
                    fos = null;
                }
            } finally {
                if ( zis != null ) {
                    zis.close();
                    zis = null;
                }
            }
        }
        zip.close();
        return rootPath;
    }

    private static void mkdir( File file ) {
        file.mkdir();
    }

}
