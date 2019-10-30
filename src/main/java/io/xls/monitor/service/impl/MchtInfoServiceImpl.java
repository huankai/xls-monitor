package io.xls.monitor.service.impl;

import io.xls.commons.utils.CollectionUtils;
import io.xls.commons.utils.JsonResult;
import io.xls.commons.utils.date.DatePattern;
import io.xls.commons.utils.date.DateUtils;
import io.xls.monitor.consts.TopicName;
import io.xls.monitor.domain.es.MchtInfo;
import io.xls.monitor.repository.elasticsearch.MchtInfoRepository;
import io.xls.monitor.service.MchtInfoService;
import io.xls.monitor.vo.ProvinceMchtVo;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author kevin
 * @date 2019-10-18 10:01
 */
@Service
@RequiredArgsConstructor
public class MchtInfoServiceImpl implements MchtInfoService {

    private final MchtInfoRepository mchtInfoRepository;

    private final ElasticsearchTemplate elasticsearchTemplate;

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public MchtInfo findById(String id) {
        return mchtInfoRepository.findById(id).orElseThrow(() -> new RuntimeException("商户不存在:" + id));
    }

    @Override
    public Iterable<MchtInfo> findAll() {
        return mchtInfoRepository.findAll();
    }

    @Override
    public Iterable<MchtInfo> saveAll(Iterable<MchtInfo> data) {
        if (CollectionUtils.isNotEmpty(data)) {
            Iterable<MchtInfo> result = mchtInfoRepository.saveAll(data);
            if (CollectionUtils.isNotEmpty(result)) {
                sendWebSocketMessage();
                return result;
            }
        }
        return Collections.emptyList();
    }

    @Override
    public MchtInfo save(MchtInfo info) {
        MchtInfo result = mchtInfoRepository.save(info);
        if (null != result) {
            sendWebSocketMessage();
        }
        return result;
    }

    private void sendWebSocketMessage() {
        messagingTemplate.convertAndSend(TopicName.QUEUE_MCHT_STATICS, JsonResult.success(mchtStatics()));
        messagingTemplate.convertAndSend(TopicName.QUEUE_TOP_PROVINCE_MCHT, JsonResult.success(top10ProvinceMchtList()));
    }

    @Override
    public long count() {
        return mchtInfoRepository.count();
    }

    @Override
    public long countByCrateDateAfter(LocalDateTime date) {
        return mchtInfoRepository.countByCrateDateAfter(DateUtils.localDateTimeToString(date, DatePattern.YYYY_MM_DD));
    }

    @Override
    public List<ProvinceMchtVo> top10ProvinceMchtList() {
        String name = "group_province";
        int pageSize = 10;
        NativeSearchQueryBuilder searchBuilder = new NativeSearchQueryBuilder()
                .withIndices(MchtInfo.INDEX_NAME)
                .withPageable(PageRequest.of(0, pageSize));
        searchBuilder.addAggregation(AggregationBuilders.terms(name).field("province")
                .order(BucketOrder.count(false)));
        Aggregations aggregations = elasticsearchTemplate.query(searchBuilder.build(), SearchResponse::getAggregations);
        StringTerms stringTerms = (StringTerms) aggregations.asMap().get(name);
        Iterator<StringTerms.Bucket> iterator = stringTerms.getBuckets().iterator();
        List<ProvinceMchtVo> result = new ArrayList<>(pageSize);
        while (iterator.hasNext()) {
            StringTerms.Bucket bucket = iterator.next();
            result.add(new ProvinceMchtVo().setName(bucket.getKeyAsString()).setValue(bucket.getDocCount()));
        }
        return result;
    }


}
