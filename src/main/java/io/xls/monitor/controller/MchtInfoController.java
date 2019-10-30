package io.xls.monitor.controller;

import io.xls.commons.utils.IDGenerator;
import io.xls.commons.utils.JsonResult;
import io.xls.commons.utils.date.DatePattern;
import io.xls.commons.utils.date.DateUtils;
import io.xls.monitor.domain.es.MchtInfo;
import io.xls.monitor.service.MchtInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * @author kevin
 * @date 2019-10-21 17:24
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("mcht")
public class MchtInfoController {

    private final MchtInfoService mchtInfoService;

    @RequestMapping("save")
    public JsonResult<MchtInfo> save() {
        MchtInfo info = new MchtInfo()
                .setCrateDate(DateUtils.localDateTimeToString(LocalDateTime.now(), DatePattern.YYYY_MM_DD))
                .setProvince("广东")
                .setSource("test")
                .setName("Test");
        info.setId(IDGenerator.STRING_UUID.generate());
        return JsonResult.success(mchtInfoService.save(info));
    }
}
