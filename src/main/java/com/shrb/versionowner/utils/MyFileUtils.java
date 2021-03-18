package com.shrb.versionowner.utils;

import com.shrb.versionowner.entity.business.User;
import com.shrb.versionowner.entity.file.MyFile;
import com.shrb.versionowner.entity.file.PathJudgeResult;
import org.apache.commons.io.FileUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicStampedReference;

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
    public static List<String> readFileAllLines(String path, String charset) throws Exception {
        PathJudgeResult pathJudgeResult = getPathJudgeResult(path);
        if (pathJudgeResult.getDirFlag()) {
            throw new Exception("this is a directory");
        }
        if (!pathJudgeResult.getExistFlag()) {
            throw new Exception("file does not exists");
        }
        return Files.readAllLines(Paths.get(path), Charset.forName(charset));
    }

    public static String readFileToString(String path, String charset) throws Exception {
        return convertLinesToString(readFileAllLines(path, charset));
    }

    public static String convertLinesToString(List<String> lines) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (i != lines.size() - 1) {
                sb.append(line).append("\n");
            } else {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    public static List<String> convertStringToLines(String content) {
        String[] contents = content.split("\n");
        List<String> lines = new ArrayList<>();
        for (String line : contents) {
            lines.add(line);
        }
        return lines;
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

    public static File[] listFile(String path) {
        File file = new File(path);
        return file.listFiles();
    }

    /**
     * 列出文件夹下的所有文件或文件夹
     * @param path
     * @param args
     * @return
     */
    public static List<String> listFilePath(String path, String...args) {
        List<String> list = new ArrayList<>();
        File file = new File(path);
        File[] files = file.listFiles();
        for(File currentFile : files) {
            if(args.length == 0) {
                list.add(currentFile.getAbsolutePath());
                continue;
            }
            if("file".equals(args[0])) {
                if(currentFile.isFile()) {
                    list.add(currentFile.getAbsolutePath());
                    continue;
                }
            }
            if("dir".equals(args[0])) {
                if(currentFile.isDirectory()) {
                    list.add(currentFile.getAbsolutePath());
                    continue;
                }
            }
        }
        return list;
    }

    /**
     * 递归删除
     * @param file
     */
    public static void deleteDirOrFile(File file) {
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteDirOrFile(files[i]);
            }
        }
        file.delete();
    }

}
