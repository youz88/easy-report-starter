package com.github.youz.report.imports.listener;

import com.github.youz.report.enums.ImportStep;
import com.github.youz.report.enums.ReportStatus;
import com.github.youz.report.imports.bo.BasicImportTemplate;
import com.github.youz.report.imports.bo.ImportContext;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;

/**
 * 允许部分成功失败
 */
@Log4j2
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractPartSuccessBusinessListener<T extends BasicImportTemplate> extends AbstractBusinessListener<T> {

    public AbstractPartSuccessBusinessListener(Class<T> clazz, ImportContext importContext) {
        super(clazz, importContext);
    }

    @Override
    protected ReportStatus customStatusByCheckFail() {
        return getCheckFailRows().size() == getTotal() ? ReportStatus.IMPORT_FAIL : ReportStatus.COMPLETED;
    }

    /**
     * 读取并导入文件信息
     */
    @Override
    public void read() {
        // 数据校验前置处理
        beforeCheckData();
        try {
            // 导入数据
            importData();
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
     * 导入数据
     */
    private void importData() {
        setInvokeMethods(Arrays.asList(ImportStep.CHECK, ImportStep.IMPORTS));
        super.read();
    }
}
