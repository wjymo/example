package com.wjy.pay.service;

import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class PayService {
    @Autowired
    private BestPayService bestPayService;

    public PayResponse create(String orderId, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum){
        PayRequest payRequest=new PayRequest();
        payRequest.setOrderName("2795713-胡尧的订单");
        payRequest.setOrderId(orderId);
        payRequest.setOrderAmount(amount.doubleValue());
        payRequest.setPayTypeEnum(bestPayTypeEnum);
        PayResponse response = bestPayService.pay(payRequest);
        log.info("response={}", response);
        return response;
    }

    public String asyncNotify(String notifyData) {
        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("payResponse:{}",payResponse);
        //2. 金额校验（从数据库查订单）

        //3. 修改订单支付状态

        BestPayPlatformEnum payPlatformEnum = payResponse.getPayPlatformEnum();
        if (payPlatformEnum==BestPayPlatformEnum.WX){
            return "<xml>\n" +
                    "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                    "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                    "</xml>";
        }else if(payPlatformEnum ==BestPayPlatformEnum.ALIPAY){
            return "success";
        }
        throw new RuntimeException("支付类型不对");
    }
}
