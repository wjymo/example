package com.wjy.paynative.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.github.wxpay.sdk.WXPay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Autowired
    private AliPayProperty aliPayProperty;

    @Bean
    public AlipayClient alipayClient(){
        AlipayClient alipayClient = new DefaultAlipayClient(aliPayProperty.getGateway_url(),
                aliPayProperty.getApp_id(), aliPayProperty.getApp_private_key(), aliPayProperty.getFormat()
                , aliPayProperty.getCharset(), aliPayProperty.getAlipay_public_key(), aliPayProperty.getSign_type());
        return alipayClient;
    }

    @Autowired
    private WeixinProperty weixinProperty;
    @Bean
    public WXPay  wxPay(){
        WXPay wxPay = new WXPay(weixinProperty);
        return wxPay;
    }
}
