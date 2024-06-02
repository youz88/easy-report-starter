package com.github.youz.report.data;

public interface UploadCloudData {

    /**
     * 上传文件
     *
     * @param localFilePath 本地文件路径
     * @return 上传文件路径
     */
    String uploadFile(String localFilePath);
}
