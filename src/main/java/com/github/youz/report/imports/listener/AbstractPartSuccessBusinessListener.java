package com.github.youz.report.imports.listener;

import com.github.youz.report.enums.ImportStep;
import com.github.youz.report.enums.ReportStatus;
import com.github.youz.report.imports.bo.BasicImportTemplate;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * 允许部分成功失败
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractPartSuccessBusinessListener<T extends BasicImportTemplate> extends AbstractBusinessListener<T> {

    @Override
    protected ReportStatus customStatusByCheckFail() {
        return getCheckFailRows().size() == getTotal() ? ReportStatus.IMPORT_FAIL : ReportStatus.COMPLETED;
    }

    /**
     * 读取并导入文件信息
     */
    @Override
    public void customRead() {
        // 数据校验前置处理
        beforeCheckData();

        // 导入数据
        importData();

        // 后置处理导入结果集
        afterProcess();
    }

    /**
     * 导入数据
     */
    private void importData() {
        readFile(Arrays.asList(ImportStep.CHECK, ImportStep.IMPORTS));
    }
}
