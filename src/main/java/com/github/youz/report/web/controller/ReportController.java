package com.github.youz.report.web.controller;

import com.github.youz.report.service.ReportService;
import com.github.youz.report.web.dto.ExportFileDTO;
import com.github.youz.report.web.dto.ImportFileDTO;
import com.github.youz.report.web.dto.ReportListDTO;
import com.github.youz.report.web.vo.ImportFileVO;
import com.github.youz.report.web.vo.PageVO;
import com.github.youz.report.web.vo.ReportInfoVO;
import com.github.youz.report.web.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/import-local")
    public Result<ImportFileVO> importFile(@RequestParam("file") MultipartFile file, ImportFileDTO reqDTO) {
        return Result.success(reportService.importLocalFile(file, reqDTO));
    }

    @PostMapping("/import-cloud")
    public Result<ImportFileVO> importFile(@RequestBody ImportFileDTO reqDTO) {
        return Result.success(reportService.importCloudFile(reqDTO));
    }

    @PostMapping("/export")
    public void exportFile(@RequestBody ExportFileDTO reqDTO, HttpServletResponse response) {
        reportService.exportFile(reqDTO, response);
    }

    @GetMapping("/{id}")
    public Result<ReportInfoVO> reportInfo(@PathVariable Long id) {
        return Result.success(reportService.fileInfo(id));
    }

    @GetMapping("/list")
    public Result<PageVO<ReportInfoVO>> reportList(ReportListDTO reqDTO) {
        return Result.success(reportService.fileList(reqDTO));
    }
}
