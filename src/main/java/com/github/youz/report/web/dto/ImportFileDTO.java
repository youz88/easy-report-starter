package com.github.youz.report.web.dto;

import com.github.youz.report.enums.BusinessType;
import lombok.Data;

@Data
public class ImportFileDTO {

    /**
     * 操作用户 id
     */
    private Long userId;

    /**
     * 上传文件路径
     */
    private String uploadFilePath;

    /**
     * 业务类型
     *
     * @see BusinessType
     */
    private Integer businessType;

}
