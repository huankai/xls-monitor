package io.xls.monitor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 聚合支付(测试环境)
 *
 * @author kevin
 * @date 2019-10-21 8:59
 */
@Data
@ConfigurationProperties(prefix = "xls.jh.test")
public class JHTestProperties {

    private String baseUrl = "http://192.168.1.220:8091/quickpay-api";

    private boolean enabled = true;

    private String mchtSuffix = "/mcht/query";

    private String transSuffix = "/trans/query";

    public String getMchtListUrl() {
        return baseUrl + mchtSuffix;
    }

    public String getTransListUrl() {
        return baseUrl + transSuffix;
    }
}
