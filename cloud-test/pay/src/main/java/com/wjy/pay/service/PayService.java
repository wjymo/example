package com.wjy.pay.service;

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
        payRequest.setOrderName("2795713-最好的支付sdk");
        payRequest.setOrderId(orderId);
        payRequest.setOrderAmount(amount.doubleValue());
        payRequest.setPayTypeEnum(bestPayTypeEnum);
        PayResponse response = bestPayService.pay(payRequest);
        log.info("response={}", response);
        return response;
    }
}
