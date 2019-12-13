package io.xls.monitor;

import io.xls.commons.utils.SpringContextHolder;
import io.xls.core.authentication.api.SecurityContext;
import io.xls.core.authentication.api.UnsupportedSecurityContext;
import io.xls.monitor.config.JHTestProperties;
import io.xls.monitor.config.JHYunfastpayProperties;
import io.xls.monitor.sync.YunfastpayDataSync;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

/**
 * @author kevin
 * @date 2019-10-16 17:14
 */
@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(value = {JHTestProperties.class, JHYunfastpayProperties.class})
public class MonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitorApplication.class, args);
        YunfastpayDataSync dataSync = SpringContextHolder.getBean(YunfastpayDataSync.class);
//        dataSync.syncMchtInfo();
        dataSync.syncTransactionInfo();
//        DeleteQuery deleteQuery = new DeleteQuery();
//        deleteQuery.setIndex(TransactionInformation.INDEX_NAME);
//        deleteQuery.setType("transactionInformation");
//        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
//        queryBuilder.withQuery(QueryBuilders.rangeQuery("amount").gte(100000));
//        deleteQuery.setQuery(queryBuilder.build().getQuery());
//        SpringContextHolder.getBean(ElasticsearchTemplate.class).delete(deleteQuery);

    }

//    @Autowired
//    private List<DataSync> dataSyncs;
//
//    /**
//     * 加载 交易信息
//     * 每 5 秒执行一次
//     */
//    @Async
//    @Scheduled(fixedRate = 1000 * 5)
//    public void loadJhTransList() {
//        if (CollectionUtils.isNotEmpty(dataSyncs)) {
//            for (DataSync dataSync : dataSyncs) {
//                dataSync.syncTransactionInfo();
//            }
//        }
//    }
//
//    /**
//     * 加载 商户信息
//     * 每 8 秒执行一次
//     */
//    @Async
//    @Scheduled(fixedRate = 1000 * 8)
//    public void loadMchtInfoList() {
//        if (CollectionUtils.isNotEmpty(dataSyncs)) {
//            for (DataSync dataSync : dataSyncs) {
//                dataSync.syncMchtInfo();
//            }
//        }
//    }

    @Bean
    public SecurityContext securityContext() {
        return new UnsupportedSecurityContext();
    }

    @Data
    public static class JhResult<T> {

        private String msg;

        private String code;

        private List<T> data;
    }
}
