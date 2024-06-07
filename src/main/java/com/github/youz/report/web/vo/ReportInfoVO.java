package com.github.youz.report.web.vo;

import com.github.youz.report.constant.ReportConst;
import com.github.youz.report.enums.BusinessType;
import com.github.youz.report.enums.ExecutionType;
import com.github.youz.report.enums.OperationType;
import com.github.youz.report.enums.ReportStatus;
import com.github.youz.report.model.ReportTask;
import com.github.youz.report.util.JsonUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.util.StringUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class ReportInfoVO {

    private Long id;

    /**
     * 业务类型
     *
     * @see BusinessType
     */
    private Integer businessType;

    /**
     * 操作类型(0: 未知, 1: 导入, 2: 导出)
     *
     * @see com.github.youz.report.enums.OperationType
     */
    private Integer opType;

    /**
     * 操作类型名称
     */
    private String opTypeName;

    /**
     * 执行类型(0: 未知, 1: 同步, 2: 异步)
     *
     * @see com.github.youz.report.enums.ExecutionType
     */
    private Integer execType;

    /**
     * 执行类型名称
     */
    private String execTypeName;

    /**
     * 执行状态(0: 待执行, 5: 执行中, 10: 执行失败, 15: 生成本地文件成功, 20: 上传文件失败, 25: 已完成)
     */
    private Integer status;

    /**
     * 执行状态名称
     *
     * @see com.github.youz.report.enums.ReportStatus
     */
    private String statusName;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件路径(如果上传到云存储, 则此字段为云存储的文件路径, 否则为本地文件路径)
     */
    private String filePath;

    /**
     * 导入失败文件路径(如果上传到云存储并且上传成功, 则此字段为云存储的文件路径, 否则为本地文件路径)
     */
    private String importFilePath;

    /**
     * 执行时间(秒)
     */
    private Integer execTime;

    /**
     * 完成时间(秒)
     */
    private Integer completeTime;

    /**
     * 错误描述
     */
    private String errorMsg;

    /**
     * 将ReportTask对象转换成ReportInfoVO对象
     *
     * @param reportTask  要转换的ReportTask对象
     * @param uploadCloud 导出文件是否上传到云存储
     * @return 转换后的ReportInfoVO对象
     */
    public static ReportInfoVO assemblyData(ReportTask reportTask, boolean uploadCloud) {
        if (reportTask == null) {
            return null;
        }

        return JsonUtil.convert(reportTask, ReportInfoVO.class)
                .setOpTypeName(OperationType.getMessageByCode(reportTask.getOpType()))
                .setExecTypeName(ExecutionType.getMessageByCode(reportTask.getExecType()))
                .setStatusName(ReportStatus.getMessageByCode(reportTask.getStatus()))
                .setFilePath(assemblyFilePath(reportTask, uploadCloud))
                .setImportFilePath(assemblyImportFailPath(reportTask));
    }

    /**
     * 将分页信息组装成ReportListVO对象
     *
     * @param pageInfo    分页信息
     * @param uploadCloud 导出文件是否上传到云存储
     * @return 组装后的ReportListVO对象
     */
    public static PageVO<ReportInfoVO> assemblyData(Page<ReportTask> pageInfo, boolean uploadCloud) {
        PageVO<ReportInfoVO> pageVO = new PageVO<>();
        pageVO.setTotal(pageInfo.getTotalRow())
                .setPageNum(pageInfo.getPageNumber())
                .setPageSize(pageInfo.getPageSize());
        if (CollectionUtils.isEmpty(pageInfo.getRecords())) {
            return pageVO;
        }

        List<ReportInfoVO> voList = pageInfo.getRecords().stream()
                .map(reportTask -> assemblyData(reportTask, uploadCloud))
                .collect(Collectors.toList());
        pageVO.setList(voList);
        return pageVO;
    }

    /**
     * 组装文件路径
     *
     * @param reportTask  报表任务对象
     * @param uploadCloud 导出文件是否上传到云存储
     * @return 组装后的文件路径
     */
    private static String assemblyFilePath(ReportTask reportTask, boolean uploadCloud) {
        if (OperationType.EXPORT.getCode() == reportTask.getOpType()) {
            // 导出文件是否上传到云存储, 是则直接返回云存储路径
            if (uploadCloud) {
                return StringUtil.isBlank(reportTask.getUploadFilePath())
                        ? ReportConst.EMPTY : reportTask.getUploadFilePath();
            } else {
                return StringUtil.isBlank(reportTask.getLocalFilePath())
                        ? ReportConst.EMPTY : reportTask.getLocalFilePath().replace(ReportConst.EXPORT_ROOT_PATH, ReportConst.EMPTY);
            }
        } else {
            // 优先返回云存储路径, 如果没有则返回本地文件路径
            return StringUtil.isBlank(reportTask.getUploadFilePath())
                    ? reportTask.getLocalFilePath()
                    : reportTask.getUploadFilePath();
        }
    }

    /**
     * 组装导入失败文件路径
     *
     * @param reportTask 报表任务对象
     * @return 导入失败文件路径
     */
    private static String assemblyImportFailPath(ReportTask reportTask) {
        // 仅导入需展示失败文件路径
        if (OperationType.IMPORTS.getCode() != reportTask.getOpType() || StringUtil.isBlank(reportTask.getFailFilePath())) {
            return ReportConst.EMPTY;
        }

        // 如果是本地文件路径, 返回本地相对路径
        return reportTask.getFailFilePath().startsWith(ReportConst.IMPORT_ROOT_PATH)
                ? reportTask.getLocalFilePath().replace(ReportConst.EXPORT_ROOT_PATH, ReportConst.EMPTY)
                : reportTask.getFailFilePath();
    }
}
