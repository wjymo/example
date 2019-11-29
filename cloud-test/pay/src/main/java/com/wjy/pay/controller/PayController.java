package com.wjy.pay.controller;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import com.wjy.pay.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/pay")
@Slf4j
public class PayController {
    @Autowired
    private PayService payService;

    @GetMapping("/")
    public ModelAndView create(@RequestParam("orderId") String orderId,
                               @RequestParam("amount") BigDecimal amount){
        PayResponse payResponse = payService.create(orderId, amount, BestPayTypeEnum.WXPAY_NATIVE);
        String codeUrl = payResponse.getCodeUrl();
        Map<String,String> map=new HashMap<String, String>();
        map.put("codeUrl",codeUrl);
        return new ModelAndView("createForWxNative",map);
    }

}
