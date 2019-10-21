package io.xls.monitor;

import io.xls.commons.utils.JsonUtils;
import io.xls.commons.utils.StringUtils;
import io.xls.commons.utils.date.DatePattern;
import io.xls.commons.utils.date.DateUtils;
import io.xls.core.authentication.api.SecurityContext;
import io.xls.core.authentication.api.UnsupportedSecurityContext;
import io.xls.core.httpclient.HttpClientUtils;
import io.xls.monitor.config.JHProperties;
import io.xls.monitor.domain.MchtInfo;
import io.xls.monitor.domain.TransactionInformation;
import io.xls.monitor.service.MchtInfoService;
import io.xls.monitor.service.TransactionInformationService;
import io.xls.monitor.util.AddressUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kevin
 * @date 2019-10-16 17:14
 */
@Slf4j
@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(value = {JHProperties.class})
public class MonitorApplication implements CommandLineRunner {

    @Autowired
    private JHProperties jhProperties;

    public static void main(String[] args) {
        SpringApplication.run(MonitorApplication.class, args);
    }

    /**
     * 加载 交易信息
     * 每 5 秒执行一次
     */
    @Scheduled(fixedRate = 1000 * 5)
    public void loadJhTransList() {
        saveJhTransList();
    }

    /**
     * 加载 商户信息
     * 每 5 秒执行一次
     */
    @Scheduled(fixedRate = 1000 * 5)
    public void loadMchtInfoList() {
        saveJhMchtList();
    }

    @Bean
    public SecurityContext securityContext() {
        return new UnsupportedSecurityContext();
    }

    @Autowired
    private MchtInfoService mchtInfoService;

    @Autowired
    private TransactionInformationService transactionInformationService;

    private volatile String jyTransSyncStartDate = null;
    private volatile String jyTransSyncStartTime = null;

    /**
     * 加载 交易信息
     */
    private void saveJhTransList() {
        LocalDateTime now = LocalDateTime.now();
        String endDate = DateUtils.localDateTimeToString(now, DatePattern.YYYYMMDD);
        if (StringUtils.isEmpty(jyTransSyncStartDate)) {
            jyTransSyncStartDate = DateUtils.localDateTimeToString(now.with(LocalTime.MIN), DatePattern.YYYYMMDD);
        }
        if (StringUtils.isEmpty(jyTransSyncStartTime)) {
            jyTransSyncStartTime = DateUtils.localDateTimeToString(now.with(LocalTime.MIN), DatePattern.HHMMSS);
        }
        String endTime = DateUtils.localDateTimeToString(now, DatePattern.HHMMSS);
        Map<String, Object> param = new HashMap<>(2);
        param.put("startDate", jyTransSyncStartDate);
        param.put("endDate", endDate);
        param.put("startTime", jyTransSyncStartTime);
        param.put("endTime", endTime);
        log.debug("loadJhTransList... : startDate:{},endDate:{}，startTime:{},endTime:{}", jyTransSyncStartDate, endDate, jyTransSyncStartTime, endTime);
        String result = HttpClientUtils.get(jhProperties.getTransListUrl(), param);
        JhResult<TransactionInformation> mchtResult = JsonUtils.deserialize(result, JhResult.class, TransactionInformation.class);
        log.debug("hTransResult: {}", JsonUtils.serialize(mchtResult));
        mchtResult.getData().forEach(item -> {
            item.setCreateDate(DateUtils.dateToString(DateUtils.stringToDate(item.getCreateDate(), DatePattern.YYYYMMDD)));
            item.setProvince(AddressUtils.getValue(item.getProvince()));
        });
        transactionInformationService.saveAll(mchtResult.getData());
        this.jyTransSyncStartDate = endDate;
        this.jyTransSyncStartTime = endTime;
    }

    private volatile String jyMchtInfoSyncStartDate = null;

    private volatile String jyMchtInfoSyncStartTime = null;

    /**
     * 保存聚合商户
     */
    public void saveJhMchtList() {
        LocalDateTime now = LocalDateTime.now();
        String endDate = DateUtils.localDateTimeToString(now, DatePattern.YYYYMMDD);
        if (StringUtils.isEmpty(jyMchtInfoSyncStartDate)) {
            jyMchtInfoSyncStartDate = DateUtils.localDateTimeToString(now.with(LocalTime.MIN), DatePattern.YYYYMMDD);
        }
        if (StringUtils.isEmpty(jyMchtInfoSyncStartTime)) {
            jyMchtInfoSyncStartTime = DateUtils.localDateTimeToString(now.with(LocalTime.MIN), DatePattern.HHMMSS);
        }
        String endTime = DateUtils.localDateTimeToString(now, DatePattern.HHMMSS);
        Map<String, Object> param = new HashMap<>(4);
        param.put("startDate", jyMchtInfoSyncStartDate);
        param.put("endDate", endDate);
        param.put("startTime", jyMchtInfoSyncStartTime);
        param.put("endTime", endTime);
        log.debug("loadMchtInfoList... : startDate:{},endDate:{}，startTime:{},endTime:{}", jyTransSyncStartDate, endDate, jyTransSyncStartTime, endTime);
        String result = HttpClientUtils.get(jhProperties.getMchtListUrl(), param);
        JhResult<MchtInfo> mchtResult = JsonUtils.deserialize(result, JhResult.class, MchtInfo.class);
        log.debug("MchtInfo Result: {}", JsonUtils.serialize(mchtResult));
        mchtResult.getData().forEach(item -> {
            item.setCrateDate(DateUtils.dateToString(DateUtils.stringToDate(item.getCrateDate(), DatePattern.YYYYMMDDHHMMSS)));
            item.setProvince(AddressUtils.getValue(item.getProvince()));
        });
        mchtInfoService.saveAll(mchtResult.getData());
        this.jyMchtInfoSyncStartDate = endDate;
        this.jyMchtInfoSyncStartTime = endTime;
    }


    @Override
    public void run(String... args) {
//        saveJhMchtList();
//        saveJhTransList();

//        this.save();
//        System.out.println(JsonUtils.serialize(mchtInfoService.top10ProvinceMchtList(), true));


//        saveTransactionInformation();
//        List<TransactionInformation> list = StreamSupport.stream(transactionInformationService.findAll().spliterator(), false).filter(item -> StringUtils.isNotEmpty(item.getProvince())).collect(Collectors.toList());
//
//        Map<String, List<TransactionInformation>> province = list.stream()
//                .collect(Collectors.groupingBy(TransactionInformation::getProvince));
//        for (Map.Entry<String, List<TransactionInformation>> entry : province.entrySet()) {
//            List<TransactionInformation> nullAmountList = entry.getValue().stream().filter(item -> null == item.getAmount()).collect(Collectors.toList());
//            BigDecimal sum = entry.getValue().stream().filter(item -> null != item.getAmount())
//                    .map(TransactionInformation::getAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
//            System.out.println(entry.getKey() + ",value:" + sum);
//        }
//        System.out.println(JsonUtils.serialize(transactionInformationService.findProvinceAmount(), true));
    }

    @Data
    public static class JhResult<T> {

        private String msg;

        private String code;

        private List<T> data;
    }
//
//    public void saveTransactionInformation() {
//        AggregatedPageImpl<MchtInfo> data = (AggregatedPageImpl<MchtInfo>) mchtInfoService.findAll();
//        List<MchtInfo> mchtInfos = data.get().collect(Collectors.toList());
//        List<TransactionInformation> list = new ArrayList<>();
//        TransactionInformation transactionInformation;
//        for (int i = 0; i < 30; i++) {
//            transactionInformation = new TransactionInformation();
//            transactionInformation.setId(IDGenerator.STRING_UUID.generate());
//            transactionInformation.setMchtId(mchtInfos.get(RandomUtils.nextInt(0, mchtInfos.size())).getId())
//                    .setAmount(new BigDecimal(RandomUtils.nextInt(0, 5000)))
//                    .setCreateDate(DateUtils.localDateTimeToString(LocalDateTime.now(), DatePattern.YYYYMMDD))
//                    .setProvince(AddressUtils.randomValue())
//                    .setPassageway(randomPassageway())
//                    .setEquipment(randomEquipment())
//                    .setSource("kys");
//            list.add(transactionInformation);
//        }
//        transactionInformationService.saveAll(list);
//    }
//
//
//    private static final String[] passagewayList = {"乐刷", "中付", "通联", "合利宝", "汇卡", "支付宝", "微信"};
//
//    private static final String[] equipmentList = {"扫码", "POS机", "刷卡"};
//
//    private String randomEquipment() {
//        return equipmentList[RandomUtils.nextInt(0, equipmentList.length)];
//    }
//
//    private String randomPassageway() {
//        return passagewayList[RandomUtils.nextInt(0, passagewayList.length)];
//    }
//
//    public void save() {
//        SelectArguments arguments = new SelectArguments();
//        arguments.setFrom("tbl_mcht_inf");
//        arguments.setFields(Arrays.asList("mcht_code as id", "mcht_name as name", "create_date as crateDate", "province_code as province"));
//        arguments.getConditions().addCondition(new SimpleCondition("province_code", Operator.ISNOTNULL, null));
//        List<MchtInfo> result = jdbcSession.queryForList(arguments, false, MchtInfo.class).getResult();
//        result.forEach(item -> {
//            item.setCrateDate(DateUtils.dateToString(DateUtils.stringToDate(item.getCrateDate(), DatePattern.YYYYMMDD)));
//            item.setProvince(AddressUtils.getValue(item.getProvince()));
//            item.setSource(SourceEnum.test.name());
//        });
//        System.out.println(JsonUtils.serialize(result, true));
//        System.out.println(result.size());
//        mchtInfoService.saveAll(result);
//    }
}
