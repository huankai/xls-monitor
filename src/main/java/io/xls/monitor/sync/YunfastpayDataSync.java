package io.xls.monitor.sync;

import io.xls.monitor.config.JHYunfastpayProperties;
import io.xls.monitor.service.MchtInfoService;
import io.xls.monitor.service.TransactionInformationService;
import org.springframework.stereotype.Component;

/**
 * @author kevin
 * @date 2019-10-22 13:51
 */
@Component
public class YunfastpayDataSync extends AbstractSync {

    private final JHYunfastpayProperties properties;

    private static final String source = "jhYunfastpay";

    public YunfastpayDataSync(TransactionInformationService transactionInformationService,
                              MchtInfoService mchtInfoService, JHYunfastpayProperties properties) {
        super(transactionInformationService, mchtInfoService);
        this.properties = properties;
    }

    @Override
    protected boolean isEnabled() {
        return properties.isEnabled();
    }

    @Override
    protected String getTransUrl() {
        return properties.getTransListUrl();
    }

    @Override
    protected String getMchtListUrl() {
        return properties.getMchtListUrl();
    }

    @Override
    protected String getSource() {
        return source;
    }
}
