package com.github.youz.report.converter.imports;

import com.alibaba.excel.converters.ReadConverterContext;
import com.github.youz.report.constant.ReportConst;
import com.github.youz.report.enums.DateFormatType;
import com.mybatisflex.core.util.StringUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 时间戳转换器(秒)
 */
public class ImportDateConverter extends AbstractConverter<Long> {

    @Override
    public Long convertToJavaData(ReadConverterContext<?> context) {
        String dataStr = getDataStr(context);
        if (StringUtil.isBlank(dataStr)) {
            return ReportConst.ZER0_L;
        }

        // 时间格式化
        LocalDateTime localDateTime = format(dataStr);

        // 校验
        assertTrue(localDateTime != null, context);

        return localDateTime.atZone(ZoneId.systemDefault())
                .toInstant()
                .getEpochSecond();
    }

    /**
     * 格式化日期
     *
     * @param dataStr 日期
     * @return 日期
     */
    private LocalDateTime format(String dataStr) {
        for (DateFormatType dateFormatType : DateFormatType.values()) {
            try {
                return LocalDate.parse(dataStr, DateTimeFormatter.ofPattern(dateFormatType.getValue())).atStartOfDay();
            } catch (Exception e) {
                //ignore
            }
        }
        return null;
    }

}
