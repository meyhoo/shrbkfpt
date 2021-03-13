package com.shrb.versionowner.utils;

import com.shrb.versionowner.entity.file.MyFile;
import com.shrb.versionowner.entity.file.PathJudgeResult;
import org.apache.commons.io.FileUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * 文件处理工具类
 * @author zhongzijie
 */
public class MyFileUtils {

    /**
     * 获取myFile对象
     * @param path
     * @see MyFile
     * @return
     */
    public static MyFile getMyFile(String path) {
        MyFile myFile = new MyFile();
        File file = new File(path);
        if (file.isDirectory()) {
            myFile.setDirFlag(true);
            myFile.setBasePath(path);
            return myFile;
        }
        if (file.isFile()) {
            myFile.setDirFlag(false);
            String fileName = file.getName();
            int index = fileName.lastIndexOf(".");
            String fileType = index == -1 ? "" : fileName.substring(index,fileName.length());
            myFile.setFileName(fileName);
            myFile.setBasePath(file.getParentFile().getPath());
            myFile.setFileType(fileType);
            return myFile;
        }
        return null;
    }

    /**
     * 解析path
     * @param path
     * @return
     */
    public static PathJudgeResult getPathJudgeResult(String path) {
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        PathJudgeResult pathJudgeResult = new PathJudgeResult();
        pathJudgeResult.setPath(path);
        MyFile myFile = getMyFile(path);
        if (myFile!=null) {
            pathJudgeResult.setBasePath(myFile.getBasePath());
            pathJudgeResult.setFileName(myFile.getFileName());
            pathJudgeResult.setFileType(myFile.getFileType());
            pathJudgeResult.setDirFlag(myFile.getDirFlag());
            pathJudgeResult.setExistFlag(true);
            return pathJudgeResult;
        }
        pathJudgeResult.setExistFlag(false);
        int lastSeparatorIndex = path.lastIndexOf("/");
        String basePath;
        if (path.contains(".")) {
            //是文件
            basePath = path.substring(0, lastSeparatorIndex+1);
            String fileName = path.substring(lastSeparatorIndex+1, path.length());
            int lastPointIndex = fileName.lastIndexOf(".");
            String fileType = fileName.substring(lastPointIndex+1, fileName.length());
            pathJudgeResult.setDirFlag(false);
            pathJudgeResult.setBasePath(basePath);
            pathJudgeResult.setFileName(fileName);
            pathJudgeResult.setFileType(fileType);
        } else {
            //非文件
            basePath = path;
            pathJudgeResult.setDirFlag(true);
            pathJudgeResult.setBasePath(basePath);
        }
        return pathJudgeResult;
    }

    /**
     * 判断文件是否存在
     * @param path
     * @return
     */
    public static boolean judgeFileExists(String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        PathJudgeResult pathJudgeResult = getPathJudgeResult(path);
        if (pathJudgeResult.getDirFlag()) {
            return false;
        }
        if (pathJudgeResult.getExistFlag()) {
            return true;
        }
        return false;
    }

    /**
     * 创建文件或目录
     * @param path
     * @throws Exception
     */
    public static void createFile(String path) throws Exception{
        PathJudgeResult pathJudgeResult = getPathJudgeResult(path);
        if (pathJudgeResult.getExistFlag()) {
            return;
        }

        if (pathJudgeResult.getDirFlag()) {
            File dir = new File(path);
            dir.mkdirs();
        } else {
            File baseDir = new File(pathJudgeResult.getBasePath());
            baseDir.mkdirs();
            File file = new File(path);
            file.createNewFile();
        }
    }

    /**
     * 拷贝文件
     * @param srcPath
     * @param destPath
     * @throws Exception
     */
    public static void copyFile(String srcPath, String destPath) throws Exception{
        FileUtils.copyFile(new File(srcPath), new File(destPath));
    }

    /**
     * 一次性把文件读到List集合中
     * @param path
     * @param charset
     * @return
     * @throws Exception
     */
    public static List<String> readFileAllLines(String path, String charset) throws Exception{
        PathJudgeResult pathJudgeResult = getPathJudgeResult(path);
        if (pathJudgeResult.getDirFlag()) {
            throw new Exception("this is a directory");
        }
        if (!pathJudgeResult.getExistFlag()) {
            throw new Exception("file does not exists");
        }
        return Files.readAllLines(Paths.get(path), Charset.forName(charset));
    }

    /**
     * 一次性把所有行写入文件，从头部写入
     * @param lines
     * @param path
     * @param charset
     * @throws Exception
     */
    public static void writeLinesToFileFromHead(List<String> lines, String path, String charset) throws Exception{
        createFile(path);
        Files.write(Paths.get(path), lines, Charset.forName(charset));
    }

    /**
     * 一次性把所有行写入文件，从尾部写入
     * @param lines
     * @param path
     * @param charset
     * @throws Exception
     */
    public static void writeLineToFileFromTail(List<String> lines, String path, String charset) throws Exception{
        createFile(path);
        FileChannel fileChannel = new RandomAccessFile(path, "rw").getChannel();
        fileChannel.position(fileChannel.size());
        for (String line : lines) {
            fileChannel.write(ByteBuffer.wrap((line+"\n").getBytes(Charset.forName(charset))));
        }
        fileChannel.close();
    }

}
