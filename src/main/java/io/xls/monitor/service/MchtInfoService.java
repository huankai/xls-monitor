package io.xls.monitor.service;

import io.xls.monitor.domain.es.MchtInfo;
import io.xls.monitor.vo.ProvinceMchtVo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kevin
 * @date 2019-10-18 10:01
 */
public interface MchtInfoService {

    MchtInfo findById(String id);

    Iterable<MchtInfo> findAll();

    MchtInfo save(MchtInfo info);

    long count();

    long countByCrateDateAfter(LocalDateTime date);

    /**
     * 统计总商户数据与当日商户数
     */
    default Map<String, Object> mchtStatics() {
        Map<String, Object> data = new HashMap<>(2);
        data.put("totalMchtCount", count());
        data.put("toDayMchtCount", countByCrateDateAfter(LocalDateTime.now()));
        return data;
    }

    /**
     * 查询全国省前 10 名商户数
     */
    List<ProvinceMchtVo> top10ProvinceMchtList();

    Iterable<MchtInfo> saveAll(Iterable<MchtInfo> data);
}
