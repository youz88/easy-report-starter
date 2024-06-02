package com.github.youz.report.model;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/** 报表任务表 --> report_task */
@Data
@NoArgsConstructor
@Table("report_task")
@Accessors(chain = true)
public class ReportTask implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 关联父任务ID --> pid */
    private Long pid;

    /** 分片索引(数据量大而分割为子任务时索引) --> sliced_index */
    private Integer slicedIndex;

    /** 业务类型(1: 用户, 2: 商品...) --> business_type */
    private Integer businessType;

    /** 操作类型(0: 未知, 1: 导入, 2: 导出) --> op_type */
    private Integer opType;

    /** 执行类型(0: 未知, 1: 同步, 2: 异步) --> exec_type */
    private Integer execType;

    /** 执行状态(0: 待执行, 5: 执行中, 10: 生成本地文件成功, 15: 已完成, 50: 执行失败, 55: 上传文件失败) --> status */
    private Integer status;

    /** [导入|导出]上下文参数 --> context */
    private String context;

    /** 文件名 --> file_name */
    private String fileName;

    /** 本地文件路径 --> local_file_path */
    private String localFilePath;

    /** 上传文件路径 --> upload_file_path */
    private String uploadFilePath;

    /** 执行时间(秒) --> exec_time */
    private Long execTime;

    /** 完成时间(秒) --> complete_time */
    private Long completeTime;

    /** 错误描述 --> error_msg */
    private String errorMsg;

    /** 操作用户ID --> user_id */
    private Long userId;

    /** 是否删除(0: 未删除, 1: 已删除) --> deleted */
    private Integer deleted;

    /** 创建时间(秒) --> create_time */
    private Long createTime;

    /** 修改时间(秒) --> update_time */
    private Long updateTime;
}