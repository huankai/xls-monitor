package io.xls.monitor.controller;

import io.xls.commons.utils.JsonResult;
import io.xls.commons.utils.date.DateUtils;
import io.xls.monitor.consts.TopicName;
import io.xls.monitor.service.MchtInfoService;
import io.xls.monitor.service.TransactionInformationService;
import io.xls.monitor.vo.AmountRangeVo;
import io.xls.monitor.vo.EquipmentTransVo;
import io.xls.monitor.vo.PassagewayTransVo;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

/**
 * @author kevin
 * @date 2019-10-18 15:02
 */
@Controller
@RequiredArgsConstructor
public class WebsocketController {


    private final MchtInfoService mchtInfoService;

    private final TransactionInformationService transactionInformationService;

    @SubscribeMapping(TopicName.QUEUE_MCHT_STATICS)
    public JsonResult<Map<String, Object>> mchtStatics() {
        return JsonResult.success(mchtInfoService.mchtStatics());
    }

    @SubscribeMapping(TopicName.QUEUE_TOP_PROVINCE_MCHT)
    public JsonResult<?> topProvinceMcht() {
        return JsonResult.success(mchtInfoService.top10ProvinceMchtList());
    }

    @SubscribeMapping(TopicName.QUEUE_PROVINCE_AMOUNT)
    public JsonResult<?> provinceAmount() {
        return JsonResult.success(transactionInformationService.findProvinceAmount());
    }

    @SubscribeMapping(TopicName.QUEUE_TOTAL_TRANS_STATICS)
    public JsonResult<?> transStatics() {
        return JsonResult.success(transactionInformationService.transactionInformationStatics());
    }

    @SubscribeMapping(TopicName.QUEUE_SUM_MCHT_STATICS)
    public JsonResult<?> sumMchtStatics() {
        return JsonResult.success(transactionInformationService.top10MchtTranscationList());
    }

    @SubscribeMapping(TopicName.QUEUE_QUARTER_TRANS_STATICS)
    public JsonResult<?> quarterTransStatics() {
        return JsonResult.success(transactionInformationService.quarterTransStatics());
    }

    @SubscribeMapping(TopicName.QUEUE_PASSAGEWAY_TRANS_STATICS)
    public JsonResult<?> passagewayTransStatics() {
        PassagewayTransVo trans = transactionInformationService.passagewayTransStatics();
        return JsonResult.success(trans);
    }

    @SubscribeMapping(TopicName.QUEUE_AMOUNT_RANGE_COUNT_STATICS)
    public JsonResult<?> amountRangeCountStatics() {
        AmountRangeVo trans = transactionInformationService.amountRangeCountStatics();
        return JsonResult.success(trans);
    }

    @SubscribeMapping(TopicName.QUEUE_YEAR_EQUIPMENT_TRANS_STATICS)
    public JsonResult<?> yearEquipmentTransStatics() {
        LocalDateTime now = LocalDateTime.now();
        EquipmentTransVo trans = transactionInformationService.equipmentTransStatics(DateUtils.getYearMinDay(now), now);
        return JsonResult.success(trans);
    }

    @SubscribeMapping(TopicName.QUEUE_TODAY_EQUIPMENT_TRANS_STATICS)
    public JsonResult<?> todayEquipmentTransStatics() {
        LocalDateTime now = LocalDateTime.now();
        EquipmentTransVo trans = transactionInformationService.equipmentTransStatics(now.with(LocalTime.MIN), now);
        return JsonResult.success(trans);
    }


}
