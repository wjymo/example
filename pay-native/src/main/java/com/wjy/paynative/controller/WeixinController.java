package com.wjy.paynative.controller;

import com.alipay.api.AlipayApiException;
import com.github.wxpay.sdk.WXPay;
import com.wjy.paynative.config.WeixinProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/weixin")
public class WeixinController {

    @Autowired
    private WXPay wxPay;
    @Autowired
    private WeixinProperty weixinProperty;
    @GetMapping("/pay")
    public String aliPay(@RequestParam String orderId,@RequestParam String amount, HttpServletResponse response) throws IOException {
        //1.封装请求参数
        Map<String,String> map=new HashMap();
        map.put("body","胡尧商城");
        //商品描述
        map.put("out_trade_no",orderId);//订单号
        // map.put("total_fee",String.valueOf(money*100));
        // 金额,以分为单位
        BigDecimal payMoney = new BigDecimal(amount);
        BigDecimal fen = payMoney.multiply(new BigDecimal("100"));//1.00
        fen = fen.setScale(0,BigDecimal.ROUND_UP);// 1
        map.put("total_fee",String.valueOf(fen));
        map.put("notify_url",weixinProperty.getNotify_url());//回调地址
        map.put("trade_type","NATIVE");//交易类型
        Map<String, String> mapResult = null; //调用统一 下单
        try {
            mapResult = wxPay.unifiedOrder( map );
        } catch (Exception e) {
            e.printStackTrace();
        }
        String codeUrl = mapResult.get("code_url");
        return codeUrl;
    }

    @PostMapping("/notify")
    public void asyncNotify(@RequestBody String notifyData, HttpServletRequest request,HttpServletResponse response) throws IOException {
        System.out.println(notifyData);
        response.setContentType("text/xml");
        response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
    }
}

