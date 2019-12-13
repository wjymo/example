package com.wjy.xunwu.exception;

import com.wjy.xunwu.response.ResultCode;

public class XunwuException extends RuntimeException {
    private Integer code;
    public XunwuException(ResultCode resultCode) {
        super(resultCode.message);
        this.code = resultCode.code;
    }

    public XunwuException(String msg) {
        super(msg);
        //通用错误错误码
        this.code = 999;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
