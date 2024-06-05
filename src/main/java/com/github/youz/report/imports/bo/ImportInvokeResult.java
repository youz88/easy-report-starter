package com.github.youz.report.imports.bo;

import com.github.youz.report.enums.MessageCode;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 导入调用结果返回对象
 */
@Data
@Accessors(chain = true)
public class ImportInvokeResult {

    /**
     * 导入失败数据列表
     */
    private List<DataStatus> dataStatusList;

    /**
     * 导入数据状态列表
     */
    @Data
    @Accessors(chain = true)
    public static class DataStatus {

        /**
         * 数据下标
         */
        private Integer index;

        /**
         * 是否执行成功
         */
        private Boolean success;

        /**
         * 失败原因
         */
        private String reason;

        /**
         * 初始化失败记录
         *
         * @param index  数据行下标
         * @param reason 失败原因
         * @return 失败记录
         */
        public static DataStatus fail(Integer index, String reason) {
            return new DataStatus()
                    .setIndex(index)
                    .setReason(reason)
                    .setSuccess(Boolean.FALSE);
        }

        /**
         * 初始化成功记录
         *
         * @param index 数据行下标
         * @return 成功记录
         */
        public static DataStatus success(Integer index) {
            return new DataStatus()
                    .setIndex(index)
                    .setSuccess(Boolean.TRUE);
        }
    }

    /**
     * 初始化网络异常失败记录
     *
     * @param list 数据行
     * @return 失败记录
     */
    public static List<DataStatus> networkError(List<? extends BasicImportTemplate> list) {
        return list.stream().map(dto ->
                new DataStatus()
                        .setIndex(dto.getIndex())
                        .setSuccess(Boolean.FALSE)
                        .setReason(MessageCode.IMPORT_NETWORK_ANOMALY.localMessage())
        ).collect(Collectors.toList());
    }
}
