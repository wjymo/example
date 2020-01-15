package com.wjy.paynative.config;

import com.github.wxpay.sdk.WXPayConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Data
@Component
@ConfigurationProperties(prefix = "pay.weixin")
public class WeixinProperty implements WXPayConfig {
    private  String app_id = "应用的APPID";
    private  String mchID = "商户id";
    private  String mck_key = "商户密钥";
//    private  String alipay_public_key = "支付宝公钥";
    //这是沙箱接口路径,正式路径为https://openapi.alipay.com/gateway.do
    private  String gateway_url ="https://openapi.alipaydev.com/gateway.do";
//    private  String format = "JSON";
//    //签名方式
//    private  String sign_type = "RSA2";
    //支付宝异步通知路径,付款完毕后会异步调用本项目的方法,必须为公网地址
    private  String notify_url = "http://公网地址/notifyUrl";
    //支付宝同步通知路径,也就是当付款完毕后跳转本项目的页面,可以不是公网地址
    private  String return_url = "http://公网地址/returnUrl";

    @Override
    public String getAppID() {
        return getApp_id();
    }

    @Override
    public String getMchID() {
        return getMchID();
    }

    @Override
    public String getKey() {
        return getMck_key();
    }

    @Override
    public InputStream getCertStream() {
        return null;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return 0;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return 0;
    }
}
