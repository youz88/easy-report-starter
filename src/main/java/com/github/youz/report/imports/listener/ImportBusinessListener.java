package com.github.youz.report.imports.listener;

import com.github.youz.report.enums.BusinessType;
import com.github.youz.report.imports.bo.ImportContext;

public interface ImportBusinessListener {

    /**
     * 标识导入业务类型
     *
     * @return 导出业务类型
     */
    BusinessType businessType();

    /**
     * 导入业务处理
     *
     * @param context 导入上下文
     */
    void importFile(ImportContext context);
}
