package com.github.youz.report.exception;

import com.github.youz.report.enums.ExceptionCode;
import lombok.Getter;

/**
 * 报表异常
 */
@Getter
public class ReportException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public ReportException(ExceptionCode exceptionCode, String message) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

}
