package com.github.youz.report.service;

import com.github.youz.report.model.ReportTask;
import com.github.youz.report.web.dto.ExportFileDTO;
import com.github.youz.report.web.dto.ImportFileDTO;
import com.github.youz.report.web.dto.ReportListDTO;
import com.github.youz.report.web.vo.ImportFileVO;
import com.github.youz.report.web.vo.PageVO;
import com.github.youz.report.web.vo.ReportInfoVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface ReportService {

    /**
     * 导入云文件
     *
     * @param reqDTO 导入文件请求DTO
     * @return 导入文件VO
     */
    ImportFileVO importCloudFile(ImportFileDTO reqDTO);

    /**
     * 导入本地文件
     *
     * @param file   导入的本地文件
     * @param reqDTO 导入文件请求DTO
     * @return 导入文件VO
     */
    ImportFileVO importLocalFile(MultipartFile file, ImportFileDTO reqDTO);

    /**
     * 异步导入文件
     *
     * @param reportTask 报表任务
     */
    void asyncImport(ReportTask reportTask);

    /**
     * 异步导出文件
     *
     * @param reportTask 报表任务
     */
    void asyncExport(ReportTask reportTask);

    /**
     * 导出文件
     *
     * @param reqDTO   导出文件请求DTO
     * @param response 响应
     */
    void exportFile(ExportFileDTO reqDTO, HttpServletResponse response);

    /**
     * 根据文件ID获取文件信息
     *
     * @param id 文件ID
     * @return 报表信息VO
     */
    ReportInfoVO fileInfo(Long id);

    /**
     * 获取文件列表分页信息
     *
     * @param reqDTO 报表列表请求DTO
     * @return 分页文件VO
     */
    PageVO<ReportInfoVO> fileList(ReportListDTO reqDTO);
}
