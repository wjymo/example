package com.wjy.paynative.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.wjy.paynative.config.AliPayProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 支付宝
 */
@Slf4j
@Controller
@RequestMapping("/ali")
public class AliController {

    @Autowired
    private AliPayProperty aliPayProperty;
    @GetMapping("/pay")
    public void aliPay(@RequestParam String orderId,@RequestParam String amount, HttpServletResponse response) throws IOException {
        //实例化客户端,填入所需参数
        AlipayClient alipayClient = new DefaultAlipayClient(aliPayProperty.getGateway_url(),
                aliPayProperty.getApp_id(), aliPayProperty.getApp_private_key(), aliPayProperty.getFormat()
                , aliPayProperty.getCharset(), aliPayProperty.getAlipay_public_key(), aliPayProperty.getSign_type());
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //在公共参数中设置回跳和通知地址
        request.setReturnUrl(aliPayProperty.getReturn_url());
        request.setNotifyUrl(aliPayProperty.getNotify_url());
        //根据订单编号,查询订单相关信息
//        Order order = payService.selectById(orderId);
        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = orderId;
        //付款金额，必填
        String total_amount = amount;
        //订单名称，必填
        String subject = "胡尧牛逼";
        //商品描述，可空
        String body = "";
        request.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        String form = "";
        try {
            form = alipayClient.pageExecute(request).getBody(); // 调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=" + aliPayProperty.getCharset());
        response.getWriter().write(form);// 直接将完整的表单html输出到页面
        response.getWriter().flush();
        response.getWriter().close();
    }


    @PostMapping("/notify")
    @ResponseBody
    public String asyncNotify(@RequestBody String notifyData,HttpServletRequest request) throws UnsupportedEncodingException, AlipayApiException {
        log.info("notifyData: {}",notifyData);
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("utf-8"), "utf-8");
            params.put(name, valueStr);
        }
        log.info("params: {}",params);//查看参数都有哪些

        String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "utf-8");
        String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "utf-8");
        String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "utf-8");
        log.info("out_trade_no:{},trade_no:{},trade_status:{}",out_trade_no,trade_no,trade_status);
        boolean signVerified = AlipaySignature.rsaCheckV1(params,
                aliPayProperty.getAlipay_public_key(), aliPayProperty.getCharset()
                , aliPayProperty.getSign_type()); // 调用SDK验证签名
        //验证签名通过
        if(signVerified){
            if(StringUtils.equals(trade_status,"TRADE_FINISHED")){
                //处理订单

            }else if(StringUtils.equals(trade_status,"TRADE_SUCCESS")){
                //处理订单

            }
            return "success";
        }
        return "failed";
    }

    @GetMapping("/returnUrl")
    public String returnUrl(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, AlipayApiException {
        // 获取支付宝GET过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("utf-8"), "utf-8");
            params.put(name, valueStr);
        }

        log.info("params: {}",params);//查看参数都有哪些
        boolean signVerified = AlipaySignature.rsaCheckV1(params,
                aliPayProperty.getAlipay_public_key(), aliPayProperty.getCharset()
                , aliPayProperty.getSign_type()); // 调用SDK验证签名
        //验证签名通过
        if(signVerified){
            // 商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no")
                    .getBytes("ISO-8859-1"), "UTF-8");
            // 支付宝交易号
            String trade_no = new String(request.getParameter("trade_no")
                    .getBytes("ISO-8859-1"), "UTF-8");
            // 付款金额
            String total_amount = new String(request.getParameter("total_amount")
                    .getBytes("ISO-8859-1"), "UTF-8");

            log.info("商户订单号="+out_trade_no);
            log.info("支付宝交易号="+trade_no);
            log.info("付款金额="+total_amount);
            //支付成功，修复支付状态
//            payService.updateById(Integer.valueOf(out_trade_no));
            return "ok";//跳转付款成功页面
        }else{
            return "no";//跳转付款失败页面
        }
    }

}
