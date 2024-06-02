package com.github.youz.report.service;

import com.github.youz.report.web.dto.ExportFileDTO;
import com.github.youz.report.web.dto.ImportFileDTO;
import com.github.youz.report.web.dto.ReportListDTO;
import com.github.youz.report.web.vo.ImportFileVO;
import com.github.youz.report.web.vo.PageVO;
import com.github.youz.report.web.vo.ReportInfoVO;

import javax.servlet.http.HttpServletResponse;

public interface ReportService {

    /**
     * 导入文件方法
     *
     * @param reqDTO 导入文件请求DTO
     * @return 导入文件VO
     */
    ImportFileVO importFile(ImportFileDTO reqDTO);

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
