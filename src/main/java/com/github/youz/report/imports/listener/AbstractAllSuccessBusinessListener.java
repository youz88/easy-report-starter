package com.github.youz.report.imports.listener;

import com.github.youz.report.enums.ImportStep;
import com.github.youz.report.enums.ReportStatus;
import com.github.youz.report.imports.bo.BasicImportTemplate;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;

/**
 * 必须全部成功
 */
@Log4j2
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
        try {
            // 数据校验
            checkData();

            // 数据校验是否失败
            if (CollectionUtils.isEmpty(getCheckFailRows())) {
                // 数据导入前置处理
                beforeImportData();

                // 导入数据
                importData();
            }
        } catch (Exception e) {
            log.error("导入文件失败：", e);

            // 更新报表任务状态
            updateFail(e.getMessage());
            return;
        }
        // 后置处理导入结果集
        afterProcess();
    }

    /**
     * 导入参数校验
     */
    private void checkData() {
        setInvokeMethods(Collections.singletonList(ImportStep.CHECK));
        readFile(getContext().getLocalFilePath());
    }

    /**
     * 导入数据
     */
    private void importData() {
        setInvokeMethods(Collections.singletonList(ImportStep.IMPORTS));
        readFile(getContext().getLocalFilePath());
    }

}
