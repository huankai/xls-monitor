package io.xls.monitor.service.impl;

import io.xls.commons.utils.ArrayUtils;
import io.xls.commons.utils.CollectionUtils;
import io.xls.commons.utils.JsonResult;
import io.xls.commons.utils.date.DatePattern;
import io.xls.commons.utils.date.DateUtils;
import io.xls.monitor.consts.TopicName;
import io.xls.monitor.domain.es.TransactionInformation;
import io.xls.monitor.repository.elasticsearch.TransactionInformationRepository;
import io.xls.monitor.service.MchtInfoService;
import io.xls.monitor.service.TransactionInformationService;
import io.xls.monitor.vo.*;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.filter.InternalFilter;
import org.elasticsearch.search.aggregations.bucket.range.InternalDateRange;
import org.elasticsearch.search.aggregations.bucket.range.InternalRange;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author kevin
 * @date 2019-10-18 15:54
 */
@Service
@RequiredArgsConstructor
public class TransactionInformationServiceImpl implements TransactionInformationService {

    private final ElasticsearchTemplate elasticsearchTemplate;

    private final TransactionInformationRepository transactionInformationRepository;

    private final MchtInfoService mchtInfoService;

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public List<ProvinceTransctioinVo> findProvinceAmount() {
        String provinceGroupName = "group_province", amountSumName = "sum_of_amount";
        NativeSearchQueryBuilder searchBuilder = new NativeSearchQueryBuilder().withIndices(TransactionInformation.INDEX_NAME).withPageable(Pageable.unpaged());
        searchBuilder.addAggregation(AggregationBuilders.terms(provinceGroupName).field("province")
                .subAggregation(AggregationBuilders.sum(amountSumName).field("amount")));
        Aggregations aggregations = elasticsearchTemplate.query(searchBuilder.build(), SearchResponse::getAggregations);
        StringTerms aggregation = aggregations.get(provinceGroupName);
        Iterator<StringTerms.Bucket> iterator = aggregation.getBuckets().iterator();
        List<ProvinceTransctioinVo> result = new ArrayList<>();
        while (iterator.hasNext()) {
            StringTerms.Bucket bucket = iterator.next();
            InternalSum amountAggregation = bucket.getAggregations().get(amountSumName);
            result.add(new ProvinceTransctioinVo().setName(bucket.getKeyAsString())
                    .setSum(format(amountAggregation.getValue()).doubleValue()));
        }
        return result;
    }

    @Override
    public Iterable<TransactionInformation> findAll() {
        Iterable<TransactionInformation> result = transactionInformationRepository.findAll();
        if (CollectionUtils.isNotEmpty(result)) {
            sendWebSocketMessage();
        }
        return result;
    }

    public void sendWebSocketMessage() {
        LocalDateTime now = LocalDateTime.now();
        messagingTemplate.convertAndSend(TopicName.QUEUE_PROVINCE_AMOUNT, JsonResult.success(findProvinceAmount()));
        messagingTemplate.convertAndSend(TopicName.QUEUE_TOTAL_TRANS_STATICS, JsonResult.success(transactionInformationStatics()));
        messagingTemplate.convertAndSend(TopicName.QUEUE_SUM_MCHT_STATICS, JsonResult.success(top10MchtTranscationList()));
        messagingTemplate.convertAndSend(TopicName.QUEUE_QUARTER_TRANS_STATICS, JsonResult.success(quarterTransStatics()));
        messagingTemplate.convertAndSend(TopicName.QUEUE_PASSAGEWAY_TRANS_STATICS, JsonResult.success(passagewayTransStatics()));
        messagingTemplate.convertAndSend(TopicName.QUEUE_AMOUNT_RANGE_COUNT_STATICS, JsonResult.success(amountRangeCountStatics()));
        messagingTemplate.convertAndSend(TopicName.QUEUE_YEAR_EQUIPMENT_TRANS_STATICS, JsonResult.success(equipmentTransStatics(DateUtils.getYearMinDay(now), now)));
        messagingTemplate.convertAndSend(TopicName.QUEUE_TODAY_EQUIPMENT_TRANS_STATICS, JsonResult.success(equipmentTransStatics(now.with(LocalTime.MIN), now)));
    }

    @Override
    public TransactionInformation save(TransactionInformation transactionInformation) {
        TransactionInformation result = transactionInformationRepository.save(transactionInformation);
        if (null != result) {
            sendWebSocketMessage();
        }
        return result;
    }

    @Override
    public Iterable<TransactionInformation> saveAll(Iterable<TransactionInformation> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            Iterable<TransactionInformation> result = transactionInformationRepository.saveAll(list);
            if (CollectionUtils.isNotEmpty(result)) {
                sendWebSocketMessage();
            }
            return result;
        }
        return Collections.emptyList();
    }

    @Override
    public long count() {
        return transactionInformationRepository.count();
    }

    @Override
    public long todayCount() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withIndices(TransactionInformation.INDEX_NAME).withTypes("transactionInformation");
        LocalDateTime now = LocalDateTime.now();
        String start = DateUtils.localDateTimeToString(DateUtils.getLocalDateTimeStart(now), DatePattern.YYYY_MM_DD);
        queryBuilder.withFilter(QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("createDate").gte(start).lte(start)));
        return elasticsearchTemplate.count(queryBuilder.build());
    }

    @Override
    public BigDecimal todaySumAmount() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withIndices(TransactionInformation.INDEX_NAME);
        LocalDateTime now = LocalDateTime.now();
        String start = DateUtils.localDateTimeToString(DateUtils.getLocalDateTimeStart(now), DatePattern.YYYY_MM_DD);
        String range = "filter_range", totalSumAggName = "today_total_sum_agg";
        queryBuilder.addAggregation(AggregationBuilders.filter(range, QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("createDate").gte(start).lte(start)))
                .subAggregation(AggregationBuilders.sum(totalSumAggName).field("amount")));
        Aggregations aggregations = elasticsearchTemplate.query(queryBuilder.build(), SearchResponse::getAggregations);
        InternalFilter filter = aggregations.get(range);
        InternalSum sum = filter.getAggregations().get(totalSumAggName);
        return format(sum.getValue());
    }

    @Override
    public BigDecimal sumAmount() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        String totalSumAggName = "total_sum_agg";
        queryBuilder.withIndices(TransactionInformation.INDEX_NAME).addAggregation(AggregationBuilders.sum(totalSumAggName).field("amount"));
        Aggregations aggregations = elasticsearchTemplate.query(queryBuilder.build(), SearchResponse::getAggregations);
        InternalSum sum = aggregations.get(totalSumAggName);
        return format(sum.getValue());
    }

    @Override
    public List<MchtTransactionVo> top10MchtTranscationList() {
        String name = "group_mcht_transaction", sumAmount = "sum_amount";
        int pageSize = 10;
        NativeSearchQueryBuilder searchBuilder = new NativeSearchQueryBuilder()
                .withIndices(TransactionInformation.INDEX_NAME)
                .withPageable(PageRequest.of(0, pageSize));
        searchBuilder.addAggregation(AggregationBuilders.terms(name).field("mchtId")
                .subAggregation(AggregationBuilders.sum(sumAmount).field("amount"))
                .order(BucketOrder.aggregation(sumAmount, false)));
        Aggregations aggregations = elasticsearchTemplate.query(searchBuilder.build(), SearchResponse::getAggregations);
        StringTerms stringTerms = (StringTerms) aggregations.asMap().get(name);
        Iterator<StringTerms.Bucket> iterator = stringTerms.getBuckets().iterator();
        List<MchtTransactionVo> result = new ArrayList<>(pageSize);
        while (iterator.hasNext()) {
            StringTerms.Bucket bucket = iterator.next();
            InternalSum sum = bucket.getAggregations().get(sumAmount);
            result.add(new MchtTransactionVo()
                    .setName(mchtInfoService.findById(bucket.getKeyAsString()).getName())
                    .setValue(format(sum.value()).doubleValue()));
        }
        return result;
    }

    /**
     * 一年四个季度名称
     */
    private static final List<String> QUARTER_NAME_LIST = Arrays.asList("一季度", "二季度", "三季度", "四季度");

    @Override
    public QuarterTransVo quarterTransStatics() {
        String dateRangeAgg = "date_range_agg", sumAmount = "range_sum_amount";
        LocalDateTime now = LocalDateTime.now();
        NativeSearchQueryBuilder searchBuilder = new NativeSearchQueryBuilder()
                .withIndices(TransactionInformation.INDEX_NAME)
                .addAggregation(
                        AggregationBuilders.dateRange(dateRangeAgg)
                                .addRange(DateUtils.localDateTimeToString(DateUtils.getMonthMinDate(now.withMonth(1)), DatePattern.YYYY_MM_DD),
                                        DateUtils.localDateTimeToString(DateUtils.getMonthMaxDate(now.withMonth(3)), DatePattern.YYYY_MM_DD))
                                .addRange(DateUtils.localDateTimeToString(DateUtils.getMonthMinDate(now.withMonth(4)), DatePattern.YYYY_MM_DD),
                                        DateUtils.localDateTimeToString(DateUtils.getMonthMaxDate(now.withMonth(6)), DatePattern.YYYY_MM_DD))
                                .addRange(DateUtils.localDateTimeToString(DateUtils.getMonthMinDate(now.withMonth(7)), DatePattern.YYYY_MM_DD),
                                        DateUtils.localDateTimeToString(DateUtils.getMonthMaxDate(now.withMonth(9)), DatePattern.YYYY_MM_DD))
                                .addRange(DateUtils.localDateTimeToString(DateUtils.getMonthMinDate(now.withMonth(10)), DatePattern.YYYY_MM_DD),
                                        DateUtils.localDateTimeToString(DateUtils.getMonthMaxDate(now.withMonth(12)), DatePattern.YYYY_MM_DD))
                                .field("createDate").subAggregation(AggregationBuilders.sum(sumAmount).field("amount")));
        Aggregations aggregations = elasticsearchTemplate.query(searchBuilder.build(), SearchResponse::getAggregations);
        InternalDateRange aggregation = aggregations.get(dateRangeAgg);
        List<InternalDateRange.Bucket> buckets = aggregation.getBuckets();
        List<QuarterTransVo.QuarterTransItem> result = new ArrayList<>(buckets.size());
        for (int i = 0, size = buckets.size(); i < size; i++) {
            InternalSum sum = buckets.get(i).getAggregations().get(sumAmount);
            result.add(new QuarterTransVo.QuarterTransItem().setName(QUARTER_NAME_LIST.get(i)).setValue(format(sum.value()).doubleValue()));
        }
        return new QuarterTransVo().setLegendData(QUARTER_NAME_LIST).setDataList(result);
    }

    @Override
    public PassagewayTransVo passagewayTransStatics() {
        String name = "group_passageway", sumAmount = "passageway_sum_amount";
        int pageSize = 10;
        NativeSearchQueryBuilder searchBuilder = new NativeSearchQueryBuilder()
                .withIndices(TransactionInformation.INDEX_NAME)
                .withPageable(PageRequest.of(0, pageSize));
        searchBuilder.addAggregation(AggregationBuilders.terms(name).field("passageway")
                .subAggregation(AggregationBuilders.sum(sumAmount).field("amount")));
        Aggregations aggregations = elasticsearchTemplate.query(searchBuilder.build(), SearchResponse::getAggregations);
        StringTerms stringTerms = (StringTerms) aggregations.asMap().get(name);
        Iterator<StringTerms.Bucket> iterator = stringTerms.getBuckets().iterator();
        List<PassagewayTransVo.PassagewayTransItem> dataList = new ArrayList<>(pageSize);
        while (iterator.hasNext()) {
            StringTerms.Bucket bucket = iterator.next();
            InternalSum sum = bucket.getAggregations().get(sumAmount);
            dataList.add(new PassagewayTransVo.PassagewayTransItem()
                    .setName(bucket.getKeyAsString())
                    .setValue(format(sum.getValue()).doubleValue()));
        }
        return new PassagewayTransVo()
                .setLegendData(dataList.stream().map(PassagewayTransVo.PassagewayTransItem::getName).collect(Collectors.toList()))
                .setDataList(dataList);
    }

    /**
     * 交易金额范围统计
     */
    private static final List<String> AMOUNT_RANGE_LIST = ArrayUtils.asArrayList("0~100", "100~200", "200~500", "500~1000", "1000~2000", "2000~5000", "5000以上");

    @Override
    public AmountRangeVo amountRangeCountStatics() {
        String rangeFilter = "amount_range_filter", dateRangeAgg = "amount_range_agg";
        LocalDateTime now = LocalDateTime.now();
        String start = DateUtils.localDateTimeToString(DateUtils.getLocalDateTimeStart(now), DatePattern.YYYY_MM_DD);
        NativeSearchQueryBuilder searchBuilder = new NativeSearchQueryBuilder()
                .withIndices(TransactionInformation.INDEX_NAME)
                .addAggregation(AggregationBuilders.filter(rangeFilter, QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("createDate").gte(start)))
                        .subAggregation(AggregationBuilders.range(dateRangeAgg)
                                .addRange(0D, 100D)
                                .addRange(100D, 200D)
                                .addRange(200D, 500D)
                                .addRange(500D, 1000D)
                                .addRange(1000D, 2000D)
                                .addRange(2000D, 5000D)
                                .addUnboundedFrom(5000D)
                                .field("amount")));
        Aggregations aggregations = elasticsearchTemplate.query(searchBuilder.build(), SearchResponse::getAggregations);
        InternalRange<InternalRange.Bucket, ?> aggregation = ((InternalFilter) aggregations.get(rangeFilter)).getAggregations().get(dateRangeAgg);
        Iterator<InternalRange.Bucket> iterator = aggregation.getBuckets().iterator();
        List<Long> dataList = new ArrayList<>();
        while (iterator.hasNext()) {
            InternalRange.Bucket bucket = iterator.next();
            dataList.add(bucket.getDocCount());
        }
        return new AmountRangeVo()
                .setXAxisData(AMOUNT_RANGE_LIST)
                .setDataList(dataList);
    }

    @Override
    public EquipmentTransVo equipmentTransStatics(LocalDateTime start, LocalDateTime end) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withIndices(TransactionInformation.INDEX_NAME);
        String range = "filter_range", equipmentAgg = "equipment_agg", totalSumAggName = "today_total_sum_agg";
        queryBuilder.addAggregation(AggregationBuilders.filter(range, QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("createDate").gte(DateUtils.localDateTimeToString(start, DatePattern.YYYY_MM_DD))
                        .lte(DateUtils.localDateTimeToString(end, DatePattern.YYYY_MM_DD))))
                .subAggregation(AggregationBuilders.terms(equipmentAgg).field("equipment")
                        .subAggregation(AggregationBuilders.sum(totalSumAggName).field("amount"))));
        Aggregations aggregations = elasticsearchTemplate.query(queryBuilder.build(), SearchResponse::getAggregations);
        InternalFilter filter = aggregations.get(range);
        StringTerms terms = filter.getAggregations().get(equipmentAgg);
        List<String> equipmentList = new ArrayList<>();
        List<Double> dataList = new ArrayList<>();
        for (StringTerms.Bucket bucket : terms.getBuckets()) {
            InternalSum sum = bucket.getAggregations().get(totalSumAggName);
            equipmentList.add(bucket.getKeyAsString());
            dataList.add(format(sum.getValue()).doubleValue());
        }
        return new EquipmentTransVo().setDataList(dataList).setXAxisData(equipmentList);
    }

    private BigDecimal format(double yuan) {
        return new BigDecimal(String.valueOf(yuan))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}
