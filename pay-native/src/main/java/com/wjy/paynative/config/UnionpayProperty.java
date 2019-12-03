package com.wjy.paynative.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "pay.union")
public class UnionpayProperty {

    private String merId;
    private String version;
    private String encoding;
    private String bizType;
    private String txnTime;
    private String currencyCode="156";
    private String txnAmt;
    private String txnType;
    private String txnSubType;
    private String accessType;
    private String channelType;
    private String backUrl;
    private String frontUrl;
//    private String currencyCode;
//    private String currencyCode;
//    private String currencyCode;
//    private String currencyCode;
}
