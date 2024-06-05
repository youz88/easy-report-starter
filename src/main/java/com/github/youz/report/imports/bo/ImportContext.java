package com.github.youz.report.imports.bo;

import com.github.youz.report.util.JsonUtil;
import com.github.youz.report.web.dto.ImportFileDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 导入上下文
 */
@Data
@Accessors(chain = true)
public class ImportContext {

    private Long id;

    /**
     * 本地文件路径
     */
    private String localFilePath;

    /**
     * 构建导入上下文对象
     *
     * @param reqDTO 导出文件DTO对象
     * @return 构建的导入上下文对象
     */
    public static ImportContext build(ImportFileDTO reqDTO) {
        return JsonUtil.convert(reqDTO, ImportContext.class);
    }
}
