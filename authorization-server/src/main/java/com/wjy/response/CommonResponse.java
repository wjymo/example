package com.wjy.response;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CommonResponse<T> {


    //@JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    private Integer code = 0;

    private String msg;

    public void setResultCode(ResultCode resultCode) {
        this.code = resultCode.code;
        this.msg = resultCode.message;
    }
}
