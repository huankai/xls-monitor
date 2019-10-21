package io.xls.monitor.service;

import io.xls.monitor.domain.TransactionInformation;
import io.xls.monitor.vo.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kevin
 * @date 2019-10-18 15:54
 */
public interface TransactionInformationService {

    /**
     * 全国各身份交易总额
     */
    List<ProvinceTransctioinVo> findProvinceAmount();

    Iterable<TransactionInformation> findAll();

    Iterable<TransactionInformation> saveAll(Iterable<TransactionInformation> list);

    TransactionInformation save(TransactionInformation transactionInformation);

    long count();

    default Map<String, Object> transactionInformationStatics() {
        Map<String, Object> data = new HashMap<>(4);
        data.put("totalTransCount", count());
        data.put("totalTransAmount", sumAmount());
        data.put("todayTransCount", todayCount());
        data.put("todayTransAmount", todaySumAmount());
        return data;
    }

    /**
     * 今日交易次数
     */
    long todayCount();

    /**
     * 今日交易总额
     */
    BigDecimal todaySumAmount();

    /**
     * 总交易额
     */
    BigDecimal sumAmount();

    /**
     * 商户交易总额(前 10 名)
     */
    List<MchtTransactionVo> top10MchtTranscationList();

    /**
     * 季度交易统计
     */
    QuarterTransVo quarterTransStatics();

    /**
     * 通道统计
     */
    PassagewayTransVo passagewayTransStatics();

    /**
     * 价格范围交易统计
     */
    AmountRangeVo amountRangeCountStatics();

    /**
     * 获取指定时间内的设备交易总额
     */
    EquipmentTransVo equipmentTransStatics(LocalDateTime start, LocalDateTime end);
}
