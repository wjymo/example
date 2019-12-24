package com.wjy.exception;


import com.wjy.response.ResultCode;

public class NettyException extends RuntimeException {
    private Integer code;
    private ResultCode resultCode;
    public NettyException(ResultCode resultCode) {
        super(resultCode.message);
        this.code = resultCode.code;
    }

    public NettyException(String msg) {
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
