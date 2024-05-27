package com.github.youz.report.web.vo;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * 请求返回结果
 */
@Data
public class Result<T> implements Serializable {

    /**
     * 数据体
     */
    private T data;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 返回说明
     */
    private String message;

    private Result(T data, Integer code, String message) {
        this.data = data;
        this.code = code;
        this.message = message;
    }

    /**
     * 请求成功
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(data, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase());
    }

}