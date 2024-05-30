package com.github.youz.report.export.bo.common;

import com.github.youz.report.web.dto.ExportFileDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 导出上下文
 */
@Data
@Accessors(chain = true)
public class ExportContext {

    /**
     * 导出查询参数
     */
    private String queryParam;

    /**
     * 导出字段名称和顺序
     */
    private List<String> fieldNames;

    /**
     * 预导出结果
     */
    private PreExportResult preExportResult;

    // 仅作数据传输, 不可序列化
    /**
     * 分页大小
     */
    private transient int pageSize;

    /**
     * 当前页码
     */
    private transient int pageNum;

    /**
     * 当前行号
     */
    private transient int rowIndex;

    /**
     * 过滤字段
     */
    private transient List<Field> fieldList;

    /**
     * 构建导出上下文对象
     *
     * @param preExportResult 预处理导出结果对象
     * @param reqDTO          导出文件DTO对象
     * @return 构建的导出上下文对象
     */
    public static ExportContext build(PreExportResult preExportResult, ExportFileDTO reqDTO) {
        return new ExportContext()
                .setQueryParam(reqDTO.getQueryParam())
                .setFieldNames(reqDTO.getFieldNames())
                .setPreExportResult(preExportResult);
    }
}
