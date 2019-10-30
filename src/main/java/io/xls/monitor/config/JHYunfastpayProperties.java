package io.xls.monitor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 聚合支付
 *
 * @author kevin
 * @date 2019-10-21 8:59
 */
@Data
@ConfigurationProperties(prefix = "xls.jh.yunfastpay")
public class JHYunfastpayProperties {

    private String baseUrl = "https://api.yunfastpay.com/quickpay-api";

    private boolean enabled;

    private String mchtSuffix = "/mcht/query";

    private String transSuffix = "/trans/query";

    public String getMchtListUrl() {
        return baseUrl + mchtSuffix;
    }

    public String getTransListUrl() {
        return baseUrl + transSuffix;
    }

}
