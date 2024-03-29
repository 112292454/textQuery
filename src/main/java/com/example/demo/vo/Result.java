package com.example.demo.vo;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.servlet.http.HttpServletResponse;

/**
 * @author gzy
 * @date 2022-11-09
 */
@Data
@Accessors(chain = true)
public class Result<T> {
	private Integer statusCode;

	private String msg;

	private T data;

	public static <T> Result<T> success(String msg) {
		Result<T> result = new Result<>();
		result.statusCode = HttpServletResponse.SC_OK;
		result.msg = msg;
		return result;
	}

	public static <T> Result<T> success() {
		Result<T> result = new Result<>();
		result.statusCode = HttpServletResponse.SC_OK;
		result.msg = "请求成功";
		return result;
	}
	public static <T> Result<T> error() {
		Result<T> result = new Result<>();
		result.statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		result.msg = "执行异常";
		return result;
	}

	public static <T> Result<T> error(String msg) {
		Result<T> result = new Result<>();
		result.statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		result.msg ="执行异常:" + msg;
		return result;
	}

	public static <T> Result<T> error(Integer statusCode, String msg) {
		Result<T> result = new Result<>();
		result.statusCode = statusCode;
		result.msg = msg;
		return result;
	}

	public Result<T> data(T data) {
		this.data = data;
		return this;
	}

	public boolean isSuccess(){
		return statusCode== HttpServletResponse.SC_OK;
	}

	public boolean isFailed(){
		return !isSuccess();
	}
}
