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
     * 返回一个表示操作成功的Result对象
     *
     * @param data 操作成功返回的数据
     * @param <T>  数据的类型
     * @return 一个包含成功状态码和成功原因的Result对象
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(data, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase());
    }

    /**
     * 返回一个表示操作失败的Result对象。
     *
     * @param data    失败时返回的数据
     * @param code    失败时返回的状态码
     * @param message 失败时返回的错误信息
     * @param <T>     数据类型
     * @return 表示操作失败的Result对象
     */
    public static <T> Result<T> fail(T data, Integer code, String message) {
        return new Result<>(data, code, message);
    }

}