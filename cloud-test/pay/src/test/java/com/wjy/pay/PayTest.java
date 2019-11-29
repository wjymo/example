package com.wjy.pay;


import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import com.wjy.pay.service.PayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PayTest {

    @Autowired
    private PayService payService;

    @Test
    public void testPay(){
        PayResponse payResponse = payService.create("1233432456",
                BigDecimal.valueOf(0.01), BestPayTypeEnum.WXPAY_NATIVE);
        System.out.println(1);
    }

}
