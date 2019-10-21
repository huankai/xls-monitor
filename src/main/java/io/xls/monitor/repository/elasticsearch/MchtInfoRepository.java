package io.xls.monitor.repository.elasticsearch;

import io.xls.core.data.elasticsearch.repository.BaseElasticsearchRepository;
import io.xls.monitor.domain.MchtInfo;

/**
 * @author kevin
 * @date 2019-10-17 17:46
 */
public interface MchtInfoRepository extends BaseElasticsearchRepository<MchtInfo> {

    /**
     * 查询指定时间之后创建的商户
     */
    long countByCrateDateAfter(String date);
}
