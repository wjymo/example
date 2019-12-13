package com.wjy.xunwu.util;

import com.wjy.xunwu.response.CommonResponse;
import com.wjy.xunwu.response.ResultCode;

public class ResponseUtil {
    public static <T> CommonResponse<T> success(T object) {
        CommonResponse<T> commonResponse = new CommonResponse<>();
        commonResponse.setResult(object);
        commonResponse.setResultCode(ResultCode.SUCCESS);
        return commonResponse;
    }

    public static CommonResponse<String> success() {
        CommonResponse<String> cr = new CommonResponse<>();
        cr.setResultCode(ResultCode.SUCCESS);
        return cr;
    }

    public static <T> CommonResponse<T> success(T object, String msg) {
        CommonResponse<T> commonResponse = new CommonResponse<>();
        commonResponse.setResult(object);
        commonResponse.setCode(ResultCode.SUCCESS.code);
        commonResponse.setMsg(msg);
        return commonResponse;
    }

    public static <T> CommonResponse<T> success(T object, String msg, Integer code) {
        CommonResponse<T> commonResponse = new CommonResponse<>();
        commonResponse.setResult(object);
        commonResponse.setCode(code);
        commonResponse.setMsg(msg);
        return commonResponse;
    }

    public static <T> CommonResponse<T> error() {
        CommonResponse<T> commonResponse = new CommonResponse<>();
        commonResponse.setResultCode(ResultCode.ERROR);
        return commonResponse;
    }
    public static <T> CommonResponse<T> error(ResultCode resultCode) {
        CommonResponse<T> commonResponse = new CommonResponse<>();
        commonResponse.setResultCode(resultCode);
        return commonResponse;
    }


    public static <T> CommonResponse<T> error(String msg) {
        CommonResponse<T> commonResponse = new CommonResponse<>();
        commonResponse.setMsg(msg);
        commonResponse.setCode(ResultCode.ERROR.code);
        return commonResponse;
    }
    public static <T> CommonResponse<T> error(String msg,Integer code) {
        CommonResponse<T> commonResponse = new CommonResponse<>();
        commonResponse.setMsg(msg);
        commonResponse.setCode(code);
        return commonResponse;
    }

    public static <T> CommonResponse<T> error(T object, String msg) {
        CommonResponse<T> commonResponse = new CommonResponse<>();
        commonResponse.setResult(object);
        commonResponse.setCode(-1);
        commonResponse.setMsg(msg);
        return commonResponse;
    }
}
