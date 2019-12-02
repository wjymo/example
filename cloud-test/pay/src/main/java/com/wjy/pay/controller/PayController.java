package com.wjy.pay.controller;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import com.wjy.pay.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.ws.rs.GET;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/pay")
@Slf4j
public class PayController {
    @Autowired
    private PayService payService;

    @GetMapping
    public ModelAndView create(@RequestParam("orderId") String orderId,
                               @RequestParam("amount") BigDecimal amount,
                               @RequestParam("payType") BestPayTypeEnum bestPayTypeEnum){
        Map<String,String> map=new HashMap<>();
        PayResponse payResponse = payService.create(orderId, amount, bestPayTypeEnum);
        if(bestPayTypeEnum==BestPayTypeEnum.WXPAY_NATIVE){
            String codeUrl = payResponse.getCodeUrl();
            map.put("codeUrl",codeUrl);
            return new ModelAndView("createForWxNative",map);
        }else if(bestPayTypeEnum==BestPayTypeEnum.ALIPAY_PC){
            String body = payResponse.getBody();
            map.put("body",body);
            return new ModelAndView("createForAlipayPc",map);
        }
        throw new RuntimeException("暂不支持的支付类型");
    }

    @PostMapping("/notify")
    @ResponseBody
    public String asyncNotify(@RequestBody String notifyData) {
        String s = payService.asyncNotify(notifyData);
        return s;
    }

}
