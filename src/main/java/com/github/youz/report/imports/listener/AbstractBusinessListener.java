package com.github.youz.report.imports.listener;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.github.youz.report.constant.ReportConst;
import com.github.youz.report.data.ReportTaskData;
import com.github.youz.report.enums.ImportStep;
import com.github.youz.report.enums.MessageCode;
import com.github.youz.report.enums.ReportStatus;
import com.github.youz.report.imports.bo.BasicImportTemplate;
import com.github.youz.report.imports.bo.ImportContext;
import com.github.youz.report.model.ReportTask;
import com.github.youz.report.util.ApplicationContextUtil;
import com.github.youz.report.util.DateUtil;
import com.github.youz.report.util.ExcelExportUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.*;

/**
 * 导入业务处理监听器
 */
@Getter
@Setter
@Log4j2
public abstract class AbstractBusinessListener<T extends BasicImportTemplate> extends AbstractAnalyticalDataListener<T> {

    /**
     * 导入失败文件后缀名称
     */
    private static final String FAIL_SUFFIX_NAME = "_导入失败";

    /**
     * 导入失败文件后缀名称
     */
    private static final String ERROR_MSG_FORMAT = "%d条记录操作成功, %d条记录操作失败";

    /**
     * 导入上下文
     */
    private ImportContext context;

    /**
     * 导入失败文件路径
     */
    private String failFilePath;

    /**
     * 导出写对象
     */
    private ExcelWriter excelWriter;

    /**
     * 导出sheet对象
     */
    private WriteSheet writeSheet;

    public AbstractBusinessListener(Class<T> clazz, ImportContext context) {
        super(clazz);
        this.context = context;
    }

    /**
     * 获取报表任务状态(参数校验失败)
     *
     * @return 报表任务状态
     */
    protected abstract ReportStatus customStatusByCheckFail();

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        super.doAfterAllAnalysed(analysisContext);

        // 完成写入操作
        if (Objects.nonNull(excelWriter)) {
            excelWriter.finish();
        }
    }

    @Override
    protected void appendFailRow(Integer rowIndex, Collection<String> source, String message) {
        // 仅校验阶段处理失败回写文件
        if (!getInvokeMethods().contains(ImportStep.CHECK)) {
            return;
        }

        // 创建失败回写文件
        if (Objects.isNull(excelWriter)) {
            createFailFile();
        }

        // 添加失败原因列
        int failColumnIndex = getTargetFieldMap().size();
        List<Object> list = new ArrayList<>(source);
        if (source.size() == failColumnIndex) {
            list.add(message);
        } else {
            list.set(failColumnIndex, message);
        }
        excelWriter.write(Collections.singletonList(list), writeSheet);

        // 追加错误行下标
        getCheckFailRows().add(rowIndex);
    }

    /**
     * 数据校验前置处理
     */
    protected void beforeCheckData() {
        // 监听配置初始化
        listenerInitConfig();

        // 更新导入任务状态 & 执行时间
        ReportTask update = new ReportTask()
                .setId(context.getId())
                .setStatus(ReportStatus.CHECK.getCode())
                .setExecTime(DateUtil.now());
        ApplicationContextUtil.getBean(ReportTaskData.class).updateById(update);
    }

    /**
     * 数据导入前置处理
     */
    protected void beforeImportData() {
        // 更新导入任务状态
        ReportTask update = new ReportTask()
                .setId(context.getId())
                .setStatus(ReportStatus.IMPORTING.getCode());
        ApplicationContextUtil.getBean(ReportTaskData.class).updateById(update);
    }

    /**
     * 导入结果后置处理
     */
    protected void afterProcess() {
        // 初始化更新导入任务
        ReportTask reportTask = new ReportTask()
                .setId(context.getId())
                .setCompleteTime(DateUtil.now());

        // 是否含有导入失败记录
        int failCount = getCheckFailRows().size();
        if (failCount == ReportConst.ZER0) {
            // 导入成功
            reportTask.setErrorMsg(String.format(ERROR_MSG_FORMAT, getTotal(), ReportConst.ZER0));
            reportTask.setStatus(ReportStatus.COMPLETED.getCode());
        } else {
            // 文件上传
//            String fileName = failFilePath.substring(failFilePath.lastIndexOf(File.separator) + 1);
//            String key = String.format(BizConst.RESOURCE_IMPORT_TEMPLATE, reportImport.getGid(),
//                    Md5Utils.getMD5(OrderUtil.generatorOrderno().getBytes(StandardCharsets.UTF_8)), fileName);
//            File file = new File(failFilePath);
//            MultipartFile multipartFile = MultipartFileUtil.fileToMultipartFile(file);
//            SpringContext.getBean(ResourceData.class).uploadBinary(key, multipartFile);

            // 更新失败原因
            reportTask.setErrorMsg(String.format(ERROR_MSG_FORMAT, getTotal() - failCount, failCount));
//            reportTask.setFailFilePath(key);
            reportTask.setStatus(customStatusByCheckFail().getCode());
        }
        ApplicationContextUtil.getBean(ReportTaskData.class).updateById(reportTask);

        //TODO 删除导入缓存
    }

    /**
     * 导入失败
     *
     * @param errorMsg 失败原因
     */
    protected void updateFail(String errorMsg) {
        ReportTask update = new ReportTask()
                .setId(context.getId())
                .setStatus(ReportStatus.IMPORT_FAIL.getCode())
                .setErrorMsg(errorMsg)
                .setCompleteTime(DateUtil.now());
        ApplicationContextUtil.getBean(ReportTaskData.class).updateById(update);

        //TODO 删除导入缓存
    }

    /**
     * 创建导入失败文件
     */
    private void createFailFile() {
        // 失败文件地址
        this.failFilePath = getLocalFilePath().substring(0, getLocalFilePath().lastIndexOf(ReportConst.FULL_STOP_SYMBOL))
                + MessageCode.IMPORT_FAIL_FILE_SUFFIX_NAME.localMessage()
                + getLocalFilePath().substring(getLocalFilePath().lastIndexOf(ReportConst.FULL_STOP_SYMBOL));

        // 表头追加失败原因列
        List<List<String>> failHeadList = new ArrayList<>(getHeadMap().values());
        failHeadList.add(Collections.singletonList(MessageCode.IMPORT_FAIL_REASON.localMessage()));

        // 创建writer,sheet对象
        this.excelWriter = ExcelExportUtil.createExcelWriter(failFilePath);
        this.writeSheet = ExcelExportUtil.createWriteSheet(failHeadList, getSheetName());
    }

    /**
     * 监听行配置
     */
    private void listenerInitConfig() {
        // 设置本地文件路径
        setLocalFilePath(context.getLocalFilePath());

        // 表头起始行 & 表体起始行
        setHeadRowIndex(1);
        setBodyRowIndex(2);
    }
}
