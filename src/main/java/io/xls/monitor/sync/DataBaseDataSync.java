package io.xls.monitor.sync;

import io.xls.commons.utils.CollectionUtils;
import io.xls.monitor.domain.MchtInfo;
import io.xls.monitor.domain.TransactionInformation;
import io.xls.monitor.repository.jpa.JpaMchtInfoRepository;
import io.xls.monitor.repository.jpa.JpaTransactionInformationRepository;
import io.xls.monitor.service.MchtInfoService;
import io.xls.monitor.service.TransactionInformationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kevin
 * @date 2019-10-23 8:50
 */
@Component
@RequiredArgsConstructor
public class DataBaseDataSync implements DataSync {

    private final JpaMchtInfoRepository jpaMchtInfoRepository;

    private final JpaTransactionInformationRepository jpaTransactionInformationRepository;

    private volatile LocalDateTime mchtInfoStartDate = null;

    private volatile LocalDateTime transSyncStartDate = null;

    private final MchtInfoService elasticMchtInfoService;

    private final TransactionInformationService elasticTransactionInformationService;

    private static final String source = "test";

    @Override
    public void syncMchtInfo() {
        LocalDateTime now = LocalDateTime.now();
        if (null == mchtInfoStartDate) {
            mchtInfoStartDate = LocalDateTime.of(2019, 1, 1, 0, 0, 0);
        }
        List<MchtInfo> mchtList = jpaMchtInfoRepository.findByCreateDate(mchtInfoStartDate, now);
        if (CollectionUtils.isNotEmpty(mchtList)) {
            List<io.xls.monitor.domain.es.MchtInfo> mchtInfos = new ArrayList<>(mchtList.size());
            io.xls.monitor.domain.es.MchtInfo item;
            for (MchtInfo info : mchtList) {
                item = new io.xls.monitor.domain.es.MchtInfo();
                item.setId(info.getId());
                item.setSource(source)
                        .setProvince(info.getProvince())
                        .setCrateDate(info.getCreateDate().toString())
                        .setName(info.getName());
                mchtInfos.add(item);
            }
            elasticMchtInfoService.saveAll(mchtInfos);
        }
        this.mchtInfoStartDate = now;

    }

    @Override
    public void syncTransactionInfo() {
        LocalDateTime now = LocalDateTime.now();
        if (null == transSyncStartDate) {
            transSyncStartDate = LocalDateTime.of(2019, 1, 1, 0, 0, 0);
        }
        List<TransactionInformation> transactionInformationList = jpaTransactionInformationRepository.findByCreateDate(transSyncStartDate, now);
        if (CollectionUtils.isNotEmpty(transactionInformationList)) {
            List<io.xls.monitor.domain.es.TransactionInformation> transactionInformations = new ArrayList<>(transactionInformationList.size());
            io.xls.monitor.domain.es.TransactionInformation item;
            for (TransactionInformation info : transactionInformationList) {
                item = new io.xls.monitor.domain.es.TransactionInformation();
                item.setId(info.getId());
                item.setSource(source)
                        .setProvince(info.getProvince())
                        .setAmount(info.getAmount())
                        .setPassageway(info.getPassageway())
                        .setEquipment(info.getEquipment())
                        .setMchtId(info.getMchtId())
                        .setCreateDate(info.getCreateDate().toString())
                        .setUserId(info.getUserId())
                        .setUserName(info.getUserName());
                transactionInformations.add(item);
            }
            elasticTransactionInformationService.saveAll(transactionInformations);
        }
        this.transSyncStartDate = now;
    }
}
