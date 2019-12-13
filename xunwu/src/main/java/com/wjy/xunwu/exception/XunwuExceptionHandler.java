package com.wjy.xunwu.exception;

import com.wjy.xunwu.response.CommonResponse;
import com.wjy.xunwu.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class XunwuExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse handler(Exception e, HttpServletRequest request, HandlerMethod handlerMethod) {
        if (e instanceof XunwuException) {
            XunwuException monitoringException=(XunwuException)e;
            return ResponseUtil.error(monitoringException.getMessage(),monitoringException.getCode());
        }else {
            if(log.isErrorEnabled()){
                log.error("【未捕获错误】,错误接口：{},错误方法：{}",request.getRequestURI(),handlerMethod.getMethod().getName(), e);
            }else {
                log.error("【未捕获错误】", e);
            }
            return ResponseUtil.error(e.getMessage());
        }
    }
}
