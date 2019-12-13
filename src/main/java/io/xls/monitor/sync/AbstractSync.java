package io.xls.monitor.sync;

import io.xls.commons.utils.JsonUtils;
import io.xls.commons.utils.StringUtils;
import io.xls.commons.utils.date.DatePattern;
import io.xls.commons.utils.date.DateUtils;
import io.xls.core.httpclient.HttpClientUtils;
import io.xls.core.httpclient.utils.HttpUtils;
import io.xls.monitor.MonitorApplication;
import io.xls.monitor.domain.es.MchtInfo;
import io.xls.monitor.domain.es.TransactionInformation;
import io.xls.monitor.service.MchtInfoService;
import io.xls.monitor.service.TransactionInformationService;
import io.xls.monitor.util.AddressUtils;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kevin
 * @date 2019-10-22 13:52
 */
@RequiredArgsConstructor
abstract class AbstractSync implements DataSync {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private volatile String transSyncStartDate = null;

    private volatile String transSyncStartTime = null;

    private volatile String mchtInfoSyncStartDate = null;

    private volatile String mchtInfoSyncStartTime = null;

    private final TransactionInformationService transactionInformationService;

    private final MchtInfoService mchtInfoService;

    protected abstract boolean isEnabled();

    protected abstract String getTransUrl();

    protected abstract String getSource();

    protected abstract String getMchtListUrl();

    @Override
    public final void syncMchtInfo() {
        if (isEnabled()) {
            LocalDateTime now = LocalDateTime.of(2017, 1, 1, 0, 0, 0);
            while (LocalDateTime.now().isAfter(now)) {
                String endDate = DateUtils.localDateTimeToString(now, DatePattern.YYYYMMDD);
                if (StringUtils.isEmpty(mchtInfoSyncStartDate)) {
                    mchtInfoSyncStartDate = "20191001";
                }
                if (StringUtils.isEmpty(mchtInfoSyncStartTime)) {
                    mchtInfoSyncStartTime = DateUtils.localDateTimeToString(now.with(LocalTime.MIN), DatePattern.HHMMSS);
                }
                String endTime = DateUtils.localDateTimeToString(now, DatePattern.HHMMSS);
                Map<String, Object> param = new HashMap<>(4);
                param.put("startDate", DateUtils.localDateTimeToString(now, DatePattern.YYYYMMDD));
                param.put("endDate", DateUtils.localDateTimeToString(now.plusMonths(1), DatePattern.YYYYMMDD));
                param.put("startTime", "000000");
                param.put("endTime", "235959");
                String mchtListUrl = getMchtListUrl();
                logger.debug("loadMchtInfoList for {},param: {}",
                        mchtListUrl,
                        param.toString());
                String result = HttpClientUtils.execute(HttpClientUtils.createHttpClient(RequestConfig.custom().build()), HttpUtils.newHttpGet(mchtListUrl, param));
                MonitorApplication.JhResult<MchtInfo> mchtResult = JsonUtils.deserialize(result, MonitorApplication.JhResult.class, MchtInfo.class);
                logger.debug("MchtInfo Result: {}", JsonUtils.serialize(mchtResult));
                mchtResult.getData().forEach(item -> {
                    item.setCrateDate(DateUtils.dateToString(DateUtils.stringToDate(item.getCrateDate(), DatePattern.YYYYMMDDHHMMSS)));
                    item.setProvince(AddressUtils.getValue(item.getProvince()));
                    item.setSource(getSource());
                });
                mchtInfoService.saveAll(mchtResult.getData());
                this.mchtInfoSyncStartDate = endDate;
                this.mchtInfoSyncStartTime = endTime;
                now = now.plusMonths(1);
            }
        }
    }

    @Override
    public void syncTransactionInfo() {
        if (isEnabled()) {
            LocalDateTime now = LocalDateTime.of(2019, 7, 20, 0, 0, 0);
            while (LocalDateTime.now().isAfter(now)) {
//            LocalDateTime now = LocalDateTime.now();
                String endDate = DateUtils.localDateTimeToString(now, DatePattern.YYYYMMDD);
//            if (StringUtils.isEmpty(transSyncStartDate)) {
//                transSyncStartDate = mchtInfoSyncStartDate = "20191015";
//            }
//            if (StringUtils.isEmpty(transSyncStartTime)) {
//                transSyncStartTime = DateUtils.localDateTimeToString(now.with(LocalTime.MIN), DatePattern.HHMMSS);
//            }
                String endTime = DateUtils.localDateTimeToString(now, DatePattern.HHMMSS);
                Map<String, Object> param = new HashMap<>(4);
                param.put("startDate", DateUtils.localDateTimeToString(now, DatePattern.YYYYMMDD));
                param.put("endDate", DateUtils.localDateTimeToString(now.plusDays(2), DatePattern.YYYYMMDD));
                param.put("startTime", "000000");
                param.put("endTime", "235959");
                String transUrl = getTransUrl();
                logger.debug("loadMchtInfoList for {},param: {}",
                        transUrl,
                        param.toString());
//            String result = HttpClientUtils.get(transUrl, param);
                String result = HttpClientUtils.execute(HttpClientUtils.createHttpClient(RequestConfig.custom().build()), HttpUtils.newHttpGet(transUrl, param));
//            String result = HttpClientUtils.execute(HttpClientUtils.createHttpClient(RequestConfig.custom().build()), HttpUtils.newHttpGet(transUrl, param));
                MonitorApplication.JhResult<TransactionInformation> mchtResult = JsonUtils.deserialize(result, MonitorApplication.JhResult.class, TransactionInformation.class);
                logger.debug("hTransResult: {}", JsonUtils.serialize(mchtResult));
                mchtResult.getData().forEach(item -> {
                    item.setCreateDate(DateUtils.dateToString(DateUtils.stringToDate(item.getCreateDate(), DatePattern.YYYYMMDD)));
                    item.setProvince(AddressUtils.getValue(item.getProvince()));
                    item.setSource(getSource());
                });
                transactionInformationService.saveAll(mchtResult.getData());
                this.transSyncStartDate = endDate;
                this.transSyncStartTime = endTime;
                now = now.plusDays(2);
            }
        }
    }
}
