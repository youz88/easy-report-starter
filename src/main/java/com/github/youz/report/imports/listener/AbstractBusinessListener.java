package com.github.youz.report.imports.listener;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.github.youz.report.config.ReportProperties;
import com.github.youz.report.constant.CacheConst;
import com.github.youz.report.constant.ReportConst;
import com.github.youz.report.data.RedisData;
import com.github.youz.report.data.ReportTaskData;
import com.github.youz.report.data.UploadCloudData;
import com.github.youz.report.enums.ImportStep;
import com.github.youz.report.enums.MessageCode;
import com.github.youz.report.enums.ReportStatus;
import com.github.youz.report.imports.bo.BasicImportTemplate;
import com.github.youz.report.imports.bo.ImportContext;
import com.github.youz.report.model.ReportTask;
import com.github.youz.report.util.ApplicationContextUtil;
import com.github.youz.report.util.DateUtil;
import com.github.youz.report.util.ExcelExportUtil;
import com.github.youz.report.util.StreamUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 导入业务处理监听器
 */
@Getter
@Setter
@Log4j2
public abstract class AbstractBusinessListener<T extends BasicImportTemplate> extends AbstractAnalyticalDataListener<T> {

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

        // 组装失败行 & 写入
        List<Object> failRow = assemblyFailRow(rowIndex, source, message);
        excelWriter.write(StreamUtil.toList(failRow), writeSheet);

        // 追加错误行下标
        getCheckFailRows().add(rowIndex);
    }

    /**
     * 数据校验前置处理
     */
    protected void beforeCheckData() {
        // 监听行配置初始化
        listenerRowConfig();

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
            reportTask.setErrorMsg(MessageCode.IMPORT_ERROR_MSG_FORMAT.localMessage(getTotal(), ReportConst.ZER0))
                    .setStatus(ReportStatus.COMPLETED.getCode());
        } else {
            // 如果开启上传云存储, 则上传失败文件
            if (ApplicationContextUtil.getBean(ReportProperties.class).getCommon().isUploadCloud()) {
                try {
                    failFilePath = ApplicationContextUtil.getBean(UploadCloudData.class).uploadFile(failFilePath);
                } catch (Exception e) {
                    log.error("上传失败文件到云存储异常", e);
                }
            }

            // 更新失败原因
            reportTask.setErrorMsg(MessageCode.IMPORT_ERROR_MSG_FORMAT.localMessage(getTotal() - failCount, failCount))
                    .setFailFilePath(failFilePath)
                    .setStatus(customStatusByCheckFail().getCode());
        }

        // 更新导入任务
        ApplicationContextUtil.getBean(ReportTaskData.class).updateById(reportTask);

        // 删除导入缓存
        importUnlock(context);
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

        // 删除导入缓存
        importUnlock(context);
    }

    /**
     * 监听行配置
     */
    protected void listenerRowConfig() {
        // 表头起始行 & 表体起始行
        setHeadRowIndex(1);
        setBodyRowIndex(2);
    }

    /**
     * 读取导入文件
     */
    protected void read() {
        super.read(context.getLocalFilePath());
    }

    /**
     * 创建导入失败文件
     */
    private void createFailFile() {
        // 表头追加失败原因列
        List<List<String>> failHeadList = new ArrayList<>(getHeadMap().values());
        failHeadList.add(StreamUtil.toList(MessageCode.IMPORT_FAIL_REASON.localMessage()));

        // 失败文件地址
        String localFilePath = context.getLocalFilePath();
        this.failFilePath = localFilePath.substring(0, localFilePath.lastIndexOf(ReportConst.FULL_STOP_SYMBOL))
                + MessageCode.IMPORT_FAIL_FILE_SUFFIX_NAME.localMessage()
                + localFilePath.substring(localFilePath.lastIndexOf(ReportConst.FULL_STOP_SYMBOL));

        // 创建writer,sheet对象
        this.excelWriter = ExcelExportUtil.createExcelWriter(failFilePath);
        this.writeSheet = ExcelExportUtil.createWriteSheet(failHeadList, getSheetName());
    }

    /**
     * 删除导入缓存
     *
     * @param context 导入上下文
     */
    private void importUnlock(ImportContext context) {
        String cacheKey = String.format(CacheConst.REPORT_IMPORT_KEY, context.getUserId(), context.getBusinessType());
        ApplicationContextUtil.getBean(RedisData.class).importUnlock(cacheKey);
    }

    /**
     * 将指定行号的错误数据行组装成包含错误信息的List对象
     *
     * @param rowIndex 行号
     * @param source   数据源集合
     * @param message  错误信息
     * @return 组装后的包含错误信息的List对象
     */
    private List<Object> assemblyFailRow(Integer rowIndex, Collection<String> source, String message) {
        List<Object> list = new ArrayList<>(source);
        // 如果该行为表头行, 无需填写失败原因
        if (rowIndex < getBodyRowIndex() - ReportConst.ONE) {
            return list;
        }

        // 添加错误信息
        int failColumnIndex = getHeadMap().size();
        if (source.size() == failColumnIndex) {
            // 末尾添加错误信息
            list.add(message);
        } else {
            // 为避免数据源长度小于表头长度, 补全数据源
            while (list.size() <= failColumnIndex) {
                list.add(ReportConst.EMPTY);
            }
            list.set(failColumnIndex, message);
        }
        return list;
    }
}
