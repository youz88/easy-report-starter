package com.github.youz.report.web.controller;

import com.github.youz.report.service.ReportService;
import com.github.youz.report.web.dto.ExportFileDTO;
import com.github.youz.report.web.dto.ImportFileDTO;
import com.github.youz.report.web.dto.ReportListDTO;
import com.github.youz.report.web.vo.*;
import com.mybatisflex.core.paginate.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/import")
    public Result<ImportFileVO> importFile(@RequestBody ImportFileDTO reqDTO) {
        return Result.success(reportService.importFile(reqDTO));
    }

    @PostMapping("/export")
    public Result<ExportFileVO> exportFile(@RequestBody ExportFileDTO reqDTO) {
        return Result.success(reportService.exportFile(reqDTO));
    }

    @PostMapping("/{id}")
    public Result<ReportInfoVO> reportInfo(@PathVariable Long id) {
        return Result.success(reportService.fileInfo(id));
    }

    @PostMapping("/list")
    public Result<Page<ReportListVO>> reportList(ReportListDTO reqDTO) {
        return Result.success(reportService.fileList(reqDTO));
    }
}
