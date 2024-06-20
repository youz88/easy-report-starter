package com.github.youz.report.util;

import com.github.youz.report.constant.ReportConst;
import com.github.youz.report.enums.DateFormatType;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 文件工具类
 */
@Log4j2
public class MultipartFileUtil {

    /**
     * 默认读取大小 (8 * 1024)
     */
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    public static String getFileName(String filePath) {
        return filePath.substring(filePath.lastIndexOf(ReportConst.SLASH_SYMBOL) + 1, filePath.lastIndexOf(ReportConst.FULL_STOP_SYMBOL));
    }

    /**
     * 下载文件
     *
     * @param uploadFile 上传的文件
     * @return 返回下载文件在本地的路径
     */
    public static String downloadFile(MultipartFile uploadFile) {
        // 临时目录
        String timeDirectory = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormatType.YYYYMMDDHHMMSS.getValue()));

        // 构建本地文件地址
        String localFilePath = String.join(File.separator, ReportConst.IMPORT_ROOT_PATH, timeDirectory)
                + File.separator + uploadFile.getOriginalFilename();

        // 文件夹则创建
        File localFileDirectory = new File(localFilePath).getParentFile();
        if (!localFileDirectory.exists()) {
            localFileDirectory.mkdirs();
        }

        try {
            // 文件下载
            uploadFile.transferTo(new File(localFilePath));
        } catch (IOException e) {
            log.error("文件下载失败：", e);
            return null;
        }
        return localFilePath;
    }

    /**
     * 下载上传文件至本地
     *
     * @param uploadFilePath 上传文件地址
     * @return 返回下载文件在本地的路径
     */
    public static String downloadFile(String uploadFilePath) {
        // 临时目录
        String timeDirectory = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormatType.YYYYMMDDHHMMSS.getValue()));

        // 构建本地文件地址
        String localFilePath = ReportConst.IMPORT_ROOT_PATH
                + File.separator + timeDirectory
                + File.separator + uploadFilePath.substring(uploadFilePath.lastIndexOf(ReportConst.SLASH_SYMBOL) + 1);

        // 文件夹则创建
        File localFileDirectory = new File(localFilePath).getParentFile();
        if (!localFileDirectory.exists()) {
            localFileDirectory.mkdirs();
        }

        // 下载网络文件
        URLConnection conn;
        try {
            URL url = new URL(uploadFilePath);
            conn = url.openConnection();
        } catch (Exception e) {
            log.error("上传云文件地址[{}]错误", uploadFilePath, e);
            return null;
        }

        try (InputStream is = conn.getInputStream(); OutputStream os = Files.newOutputStream(Paths.get(localFilePath))) {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int byteread;
            while ((byteread = is.read(buffer)) != -1) {
                os.write(buffer, 0, byteread);
            }
        } catch (IOException e) {
            log.error("云文件下载失败", e);
            return null;
        }
        return localFilePath;
    }
}
