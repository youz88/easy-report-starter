package com.github.youz.report.imports.check;

import com.github.youz.report.annotation.ImportCell;
import com.github.youz.report.annotation.ImportNull;
import com.github.youz.report.enums.ExceptionCode;
import com.github.youz.report.enums.MessageCode;
import com.mybatisflex.core.util.StringUtil;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * 导入检查
 */
public abstract class AbstractImportCheck implements ImportCheck {

    /**
     * 自定义检查方法
     *
     * @param field 要检查的字段
     * @param value 字段的值
     */
    protected abstract void customCheck(Field field, String value);

    @Override
    public void check(Field field, String value) {
        if (StringUtil.isBlank(value)) {
            // 空值校验
            nullCheck(field, value);
        } else {
            // 自定义校验
            customCheck(field, value);
        }
    }

    /**
     * 进行空值校验
     *
     * @param field 字段对象
     * @param value 字段值
     */
    private void nullCheck(Field field, String value) {
        // 是否需要非空校验
        ImportNull importNull = AnnotatedElementUtils.findMergedAnnotation(field, ImportNull.class);
        if (importNull == null || !importNull.checkNull()) {
            return;
        }

        // 获取错误提示信息 & 抛出异常
        ImportCell cell = field.getAnnotation(ImportCell.class);
        String errMsg = StringUtil.isBlank(importNull.value())
                ? MessageCode.IMPORT_CELL_NOT_NULL.localMessage(Arrays.toString(cell.value()))
                : importNull.value();
        ExceptionCode.IMPORT_FAIL_CUSTOM_MSG.throwException(errMsg);
    }
}
