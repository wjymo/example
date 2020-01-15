package com.wjy.paynative;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.wjy.paynative.config.AliPayProperty;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestAlipay {
    @Autowired
    private AliPayProperty aliPayProperty;

    /**
     * 查询支付宝的订单状态，有：成功，交易不存在
     * 交易成功：
     * {
     * 	"body": "{\"alipay_trade_query_response\":{\"code\":\"10000\",\"msg\":\"Success\",\"buyer_logon_id\":\"181******80\",\"buyer_pay_amount\":\"0.00\",\"buyer_user_id\":\"2088112834761133\",\"invoice_amount\":\"0.00\",\"out_trade_no\":\"1231243889989\",\"point_amount\":\"0.00\",\"receipt_amount\":\"0.00\",\"send_pay_date\":\"2020-01-14 17:41:18\",\"total_amount\":\"0.01\",\"trade_no\":\"2020011422001461131410914523\",\"trade_status\":\"TRADE_SUCCESS\"},\"sign\":\"qMk5SH99gCLnKZwFpEJ4Ynex6btmnDXaXYgiKIXvUfofZ0u56odNJD768YV32aC7M8bkONG00y0hi8/2aiCtdEFaDSi7mHU1WE3Se8dqSsO2KHTuxTApTUvKs9I+p7DBj+0QJ5QF2ZsYIFKWJsNWqCJRfu6kmYpQzbhC9RcyEcZI6m4RhlClN+J7OLPE6SQRcMoVYSN66Y+228w5f26Dv+PC2ZUhbKa8I19RvLB1QCnWh5meOr7FdOC0dxDLl0ccIEh0YHGxAtrH7/2MD15zkMXYbgVP/kHxOpuhRLsl69KKj2EaiJEGVR2MbXVmuNb0c8he1j0VHaGvqXZx3xlJtg==\"}",
     * 	"buyerLogonId": "181******80",
     * 	"buyerPayAmount": "0.00",
     * 	"buyerUserId": "2088112834761133",
     * 	"code": "10000",
     * 	"errorCode": "10000",
     * 	"invoiceAmount": "0.00",
     * 	"msg": "Success",
     * 	"outTradeNo": "1231243889989",
     * 	"params": {
     * 	"params": {
     * 		"biz_content": "{\"out_trade_no\":\"1231243889989\",\"trade_no\":\"2020011422001461131410914523\",      \"query_options\":[        \"TRADE_SETTLE_INFO\"      ]  }"
     *        },
     * 	"pointAmount": "0.00",
     * 	"receiptAmount": "0.00",
     * 	"sendPayDate": 1578994878000,
     * 	"success": true,
     * 	"totalAmount": "0.01",
     * 	"tradeNo": "2020011422001461131410914523",
     * 	"tradeStatus": "TRADE_SUCCESS"
     * }
     * @throws AlipayApiException
     */
    @Test
    public void testGetAliOrderStatus() throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
                aliPayProperty.getApp_id(),
                aliPayProperty.getApp_private_key(),
                "json","utf-8",aliPayProperty.getAlipay_public_key()
                ,"RSA2");
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent("{" +
                "\"out_trade_no\":\"1231243889989\"," +
                "\"trade_no\":\"2020011422001461131410914523\"," +
//                "\"org_pid\":\"2088101117952222\"," +
                "      \"query_options\":[" +
                "        \"TRADE_SETTLE_INFO\"" +
                "      ]" +
                "  }");
//        request.setBizContent("{" +
//                "\"out_trade_no\":\"111111111113233\"," +
//                "\"trade_no\":\"20200114220014611314109145222\"," +
////                "\"org_pid\":\"2088101117952222\"," +
//                "      \"query_options\":[" +
//                "        \"TRADE_SETTLE_INFO\"" +
//                "      ]" +
//                "  }");
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        log.info("response:{}", JSON.toJSONString(response));
        if(response.isSuccess()){
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
    }
}
