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
            LocalDateTime now = LocalDateTime.now();
            String endDate = DateUtils.localDateTimeToString(now, DatePattern.YYYYMMDD);
            if (StringUtils.isEmpty(mchtInfoSyncStartDate)) {
                mchtInfoSyncStartDate = DateUtils.localDateTimeToString(now.with(LocalTime.MIN), DatePattern.YYYYMMDD);
            }
            if (StringUtils.isEmpty(mchtInfoSyncStartTime)) {
                mchtInfoSyncStartTime = DateUtils.localDateTimeToString(now.with(LocalTime.MIN), DatePattern.HHMMSS);
            }
            String endTime = DateUtils.localDateTimeToString(now, DatePattern.HHMMSS);
            Map<String, Object> param = new HashMap<>(4);
            param.put("startDate", mchtInfoSyncStartDate);
            param.put("endDate", endDate);
            param.put("startTime", mchtInfoSyncStartTime);
            param.put("endTime", endTime);
            String mchtListUrl = getMchtListUrl();
            logger.debug("loadMchtInfoList for {} ... : startDate:{},endDate:{}，startTime:{},endTime:{}",
                    mchtListUrl,
                    mchtInfoSyncStartDate, endDate, mchtInfoSyncStartTime, endTime);
            String result = HttpClientUtils.get(mchtListUrl, param);
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
        }
    }

    @Override
    public void syncTransactionInfo() {
        if (isEnabled()) {
            LocalDateTime now = LocalDateTime.now();
            String endDate = DateUtils.localDateTimeToString(now, DatePattern.YYYYMMDD);
            if (StringUtils.isEmpty(transSyncStartDate)) {
                transSyncStartDate = DateUtils.localDateTimeToString(now.with(LocalTime.MIN), DatePattern.YYYYMMDD);
            }
            if (StringUtils.isEmpty(transSyncStartTime)) {
                transSyncStartTime = DateUtils.localDateTimeToString(now.with(LocalTime.MIN), DatePattern.HHMMSS);
            }
            String endTime = DateUtils.localDateTimeToString(now, DatePattern.HHMMSS);
            Map<String, Object> param = new HashMap<>(4);
            param.put("startDate", transSyncStartDate);
            param.put("endDate", endDate);
            param.put("startTime", transSyncStartTime);
            param.put("endTime", endTime);
            String transUrl = getTransUrl();
            logger.debug("loadJhTrans for {},... : startDate:{},endDate:{}，startTime:{},endTime:{}",
                    transUrl, transSyncStartDate, endDate, transSyncStartTime, endTime);
//            String result = HttpClientUtils.get(transUrl, param);
            String result = HttpClientUtils.execute(HttpClientUtils.createHttpClient(RequestConfig.custom().build()), HttpUtils.newHttpGet(transUrl, param));
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
        }
    }
}
