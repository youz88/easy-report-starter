-- auto-generated definition
create table report_task
(
    id               bigint unsigned auto_increment
        primary key,
    pid              bigint unsigned     default 0  not null comment '关联父任务ID',
    sliced_index     int(4) unsigned     default 1  not null comment '分片索引(因数据量大而分割为子任务时相同pid存在多个)',
    business_type    int(4) unsigned     default 0  not null comment '业务类型(1: 用户, 2: 商品...)',
    op_type          tinyint(1) unsigned default 0  not null comment '操作类型(0: 未知, 1: 导入, 2: 导出)',
    exec_type        tinyint(1) unsigned default 0  not null comment '执行类型(0: 未知, 1: 同步, 2: 异步)',
    context          json                           null comment '[导入|导出]上下文参数',
    status           int unsigned        default 0  not null comment '执行状态(0: 待执行, 5: 执行中, 10: 生成本地文件成功, 15: 已完成, 50: 执行失败, 55: 上传文件失败)',
    file_name        varchar(64)         default '' not null comment '文件名',
    local_file_path   varchar(255)       default '' not null comment '临时文件路径',
    upload_file_path varchar(255)        default '' not null comment '上传文件路径',
    fail_file_path   varchar(255)        default '' not null comment '失败文件路径',
    exec_time        bigint unsigned     default 0  not null comment '执行时间(秒)',
    complete_time    bigint unsigned     default 0  not null comment '完成时间(秒)',
    error_msg        varchar(255)        default '' not null comment '错误描述',
    user_id          bigint unsigned     default 0  not null comment '操作用户ID',
    deleted          tinyint(1) unsigned default 0  not null comment '是否删除(0: 未删除, 1: 已删除)',
    create_time      bigint unsigned     default 0  null comment '创建时间(秒)',
    update_time      bigint unsigned     default 0  null comment '修改时间(秒)'
)
comment '报表任务表' row_format = DYNAMIC;

