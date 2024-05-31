package com.github.youz.report.data;

public interface UploadCloudData {

    /**
     * 上传文件
     *
     * @param tempFilePath 临时文件路径
     * @return 上传文件路径
     */
    String uploadFile(String tempFilePath);
}
