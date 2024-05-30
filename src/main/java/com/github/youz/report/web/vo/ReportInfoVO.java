package com.github.youz.report.web.vo;

import com.github.youz.report.enums.BusinessType;
import com.github.youz.report.model.ReportTask;
import com.github.youz.report.util.JsonUtil;
import com.mybatisflex.core.paginate.Page;
import lombok.Data;

@Data
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
     */
    private Integer opType;

    /**
     * 执行类型(0: 未知, 1: 同步, 2: 异步)
     */
    private Integer execType;

    /**
     * 执行状态(0: 待执行, 5: 执行中, 10: 执行失败, 15: 生成本地文件成功, 20: 上传文件失败, 25: 已完成)
     */
    private Integer status;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 上传文件路径
     */
    private String uploadFilePath;

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
     * @param reportTask 要转换的ReportTask对象
     * @return 转换后的ReportInfoVO对象
     */
    public static ReportInfoVO assemblyData(ReportTask reportTask) {
        return JsonUtil.convert(reportTask, ReportInfoVO.class);
    }

    /**
     * 将分页信息组装成ReportListVO对象
     *
     * @param pageInfo 分页信息
     * @return 组装后的ReportListVO对象
     */
    public static PageVO<ReportInfoVO> assemblyData(Page<ReportTask> pageInfo) {
        PageVO<ReportInfoVO> pageVO = new PageVO<>();
        return pageVO.setTotal(pageInfo.getTotalRow())
                .setPageNum(pageInfo.getPageNumber())
                .setPageSize(pageInfo.getPageSize())
                .setList(JsonUtil.convertList(pageInfo.getRecords(), ReportInfoVO.class));
    }
}
