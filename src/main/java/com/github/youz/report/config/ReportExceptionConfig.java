package com.github.youz.report.config;


import com.github.youz.report.exception.ReportException;
import com.github.youz.report.web.vo.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理配置类
 */
@RestControllerAdvice
public class ReportExceptionConfig {

    /**
     * 异常处理器，处理ReportException异常
     *
     * @param e ReportException异常对象
     * @return ResponseEntity对象，包含异常信息
     */
    @ExceptionHandler(ReportException.class)
    public Result<String> reportException(ReportException e) {
        return Result.fail(null, e.getExceptionCode().getCode(), e.getMessage());
    }
}
