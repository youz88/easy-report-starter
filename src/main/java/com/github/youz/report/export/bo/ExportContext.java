package com.github.youz.report.export.bo;

import com.github.youz.report.util.JsonUtil;
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
     * 总数
     */
    private Long total;

    /**
     * 执行类型
     */
    private Integer execType;

    /**
     * 分片索引(数据量大而分割为子任务时索引)
     */
    private Integer slicedIndex;

    /**
     * 目录名
     */
    private String directoryName;

    /**
     * 文件名
     */
    private String fileName;

    // 仅作数据传输, 不可序列化存储
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
     * 动态表头模版
     */
    private transient Object dynamicTemplate;

    /**
     * 构建导出上下文对象
     *
     * @param preExportResult 预处理导出结果对象
     * @param reqDTO          导出文件DTO对象
     * @return 构建的导出上下文对象
     */
    public static ExportContext build(PreExportResult preExportResult, ExportFileDTO reqDTO) {
        return JsonUtil.convert(preExportResult, ExportContext.class)
                .setQueryParam(reqDTO.getQueryParam())
                .setFieldNames(reqDTO.getFieldNames());
    }
}
