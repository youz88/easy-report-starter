package com.github.youz.report.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.github.youz.report.enums.ExceptionCode;
import com.github.youz.report.enums.ExecutionType;
import com.github.youz.report.enums.ExportStep;
import com.github.youz.report.export.bo.ExportContext;
import com.github.youz.report.export.bo.SyncExportResult;
import com.github.youz.report.export.chain.ExportChain;
import com.github.youz.report.export.chain.RootExportChain;
import com.github.youz.report.export.handler.ExportBusinessHandler;
import com.github.youz.report.model.ReportTask;
import com.github.youz.report.web.vo.ExportFileVO;
import com.github.youz.report.web.vo.Result;
import com.mybatisflex.core.util.ArrayUtil;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.util.List;

/**
 * 报表导出工具类
 */
@Log4j2
public class ExcelExportUtil {

    /**
     * 导出报表数据
     *
     * @param handler    导出处理器
     * @param reportTask 报表任务对象
     * @param response   响应对象
     */
    public static void webExport(ExportBusinessHandler handler, ReportTask reportTask, HttpServletResponse response) {
        if (ExecutionType.isSync(reportTask.getExecType())) {
            syncWebExport(handler, reportTask, response);
        } else {
            asyncWebExport(reportTask, response);
        }
    }

    /**
     * 异步定时任务导出
     *
     * @param reportTask 报表任务对象
     */
    public static void jobExport(ReportTask reportTask) {
        // 根据报表任务状态获取导出步骤
        ExportStep exportStep = ExportStep.of(reportTask.getStatus());
        Class<? extends ExportChain>[] chainClasses = exportStep.getChainClasses();
        if (ArrayUtil.isEmpty(chainClasses)) {
            return;
        }

        // 创建根导出链 & 赋值临时导出链
        ExportChain rootChain = ApplicationContextUtil.getBean(RootExportChain.class);
        ExportChain temp = rootChain;

        // 遍历导出链的类数组
        for (Class<? extends ExportChain> chainClass : chainClasses) {
            ExportChain chain = ApplicationContextUtil.getBean(chainClass);
            temp = temp.setNext(chain);
        }

        // 处理导出任务
        rootChain.handler(reportTask);
    }

    /**
     * 创建WriteSheet对象
     *
     * @param headList 表头
     * @return WriteSheet
     */
    public static WriteSheet createWriteSheet(List<List<String>> headList, String sheetName) {
        return EasyExcel.writerSheet(sheetName)
                .registerWriteHandler(ExcelStyleUtil.createExcelStyle())
                .registerWriteHandler(ExcelStyleUtil.createAutoColumn())
                .head(headList)
                .build();
    }

    /**
     * 创建ExcelWriter对象
     *
     * @param localFilePath 本地文件路径
     * @return ExcelWriter
     */
    public static ExcelWriter createExcelWriter(String localFilePath) {
        File localFile = new File(localFilePath);
        File tempDirectory = localFile.getParentFile();

        // 如果父目录不存在，则创建父目录
        if (!tempDirectory.exists()) {
            boolean mkdirs = tempDirectory.mkdirs();
            ExceptionCode.EXPORT_DATA_EMPTY.assertNotNull(mkdirs);
        }

        // 创建ExcelWriter对象并返回
        return EasyExcel.write(localFile).build();
    }

    /**
     * 同步导出报表任务数据到Excel文件，并以附件形式返回给前端
     *
     * @param handler    导出处理器
     * @param reportTask 报表任务对象
     * @param response   响应对象，用于将Excel文件作为附件返回给前端
     */
    private static void syncWebExport(ExportBusinessHandler handler, ReportTask reportTask, HttpServletResponse response) {
        // 调用导出处理器的同步导出方法，获取同步导出结果
        ExportContext exportContext = JsonUtil.toObject(reportTask.getContext(), ExportContext.class);
        SyncExportResult syncExportResult = handler.syncExport(exportContext);

        try {
            // 设置响应的内容类型为Excel文件
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            // 对文件名进行编码，防止中文乱码
            String fileName = URLEncoder.encode(reportTask.getFileName(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            response.setCharacterEncoding("utf-8");

            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream())
                    .autoCloseStream(Boolean.FALSE)
                    .head(syncExportResult.getHeadList())
                    .sheet(syncExportResult.getSheetName())
                    .doWrite(syncExportResult.getDataList());
        } catch (Exception e) {
            // 如果出现异常，记录错误日志 & 抛出异常
            log.error("下载文件失败", e);
            ExceptionCode.EXPORT_DOWNLOAD_FAIL.throwException();
        }
    }

    /**
     * 异步导出报表任务数据到JSON格式，并将结果返回给前端
     *
     * @param reportTask 报表任务对象
     * @param response   响应对象，用于将JSON格式数据返回给前端
     */
    @SneakyThrows
    private static void asyncWebExport(ReportTask reportTask, HttpServletResponse response) {
        // 设置响应的内容格式
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        // 将结果对象转换为JSON字符串，并写入响应的输出流中
        Result<ExportFileVO> result = Result.success(ExportFileVO.assemblyData(reportTask));
        response.getWriter().println(JsonUtil.toJson(result));
    }
}
