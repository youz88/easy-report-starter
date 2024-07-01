package com.github.youz.report.imports.listener;

import com.github.youz.report.enums.ImportStep;
import com.github.youz.report.enums.ReportStatus;
import com.github.youz.report.imports.bo.BasicImportTemplate;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;

/**
 * 必须全部成功
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractAllSuccessBusinessListener<T extends BasicImportTemplate> extends AbstractBusinessListener<T> {

    @Override
    protected ReportStatus customStatusByCheckFail() {
        return ReportStatus.IMPORT_FAIL;
    }

    /**
     * 读取并导入文件信息
     */
    @Override
    public void customRead() {
        // 数据校验前置处理
        beforeCheckData();

        // 数据校验
        checkData();

        // 数据校验是否失败
        if (CollectionUtils.isEmpty(getCheckFailRows())) {
            // 数据导入前置处理
            beforeImportData();

            // 导入数据
            importData();
        }

        // 后置处理导入结果集
        afterProcess();
    }

    /**
     * 导入参数校验
     */
    private void checkData() {
        readFile(Collections.singletonList(ImportStep.CHECK));
    }

    /**
     * 导入数据
     */
    private void importData() {
        readFile(Collections.singletonList(ImportStep.IMPORTS));
    }

}
