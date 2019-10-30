package io.xls.monitor.sync;

import io.xls.monitor.config.JHTestProperties;
import io.xls.monitor.service.MchtInfoService;
import io.xls.monitor.service.TransactionInformationService;
import org.springframework.stereotype.Component;

/**
 * @author kevin
 * @date 2019-10-22 13:48
 */
@Component
public class TestEnvDataSync extends AbstractSync {

    private final JHTestProperties jhTestProperties;

    private static final String source = "jhTest";

    public TestEnvDataSync(TransactionInformationService transactionInformationService,
                           MchtInfoService mchtInfoService, JHTestProperties jhTestProperties) {
        super(transactionInformationService, mchtInfoService);
        this.jhTestProperties = jhTestProperties;
    }

    @Override
    protected boolean isEnabled() {
        return jhTestProperties.isEnabled();
    }

    @Override
    protected String getTransUrl() {
        return jhTestProperties.getTransListUrl();
    }

    @Override
    protected String getMchtListUrl() {
        return jhTestProperties.getMchtListUrl();
    }

    @Override
    protected String getSource() {
        return source;
    }
}
