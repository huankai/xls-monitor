package io.xls.monitor.controller;

import io.xls.commons.utils.IDGenerator;
import io.xls.commons.utils.JsonResult;
import io.xls.commons.utils.date.DatePattern;
import io.xls.commons.utils.date.DateUtils;
import io.xls.monitor.domain.es.TransactionInformation;
import io.xls.monitor.service.TransactionInformationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author kevin
 * @date 2019-10-22 14:16
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("trans")
public class TransactionInformationController {

    private final TransactionInformationService transactionInformationService;

    @RequestMapping("save")
    public JsonResult<TransactionInformation> save() {
        TransactionInformation info = new TransactionInformation()
                .setCreateDate(DateUtils.localDateTimeToString(LocalDateTime.now(), DatePattern.YYYY_MM_DD))
                .setMchtId("MCHT100013214")
                .setEquipment("扫码")
                .setPassageway("支付宝")
                .setAmount(BigDecimal.ONE)
                .setProvince("浙江")
                .setSource("test");
        info.setId(IDGenerator.STRING_UUID.generate());
        return JsonResult.success(transactionInformationService.save(info));
    }
}
