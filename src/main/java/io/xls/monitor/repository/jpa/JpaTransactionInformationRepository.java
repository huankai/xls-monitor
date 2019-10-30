package io.xls.monitor.repository.jpa;

import io.xls.core.data.jpa.repository.StringIdJpaRepository;
import io.xls.monitor.domain.TransactionInformation;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author kevin
 * @date 2019-10-23 9:02
 */
public interface JpaTransactionInformationRepository extends StringIdJpaRepository<TransactionInformation> {

    @Query(nativeQuery = true, value = "SELECT * FROM transaction_information WHERE create_date >= ?1 AND create_date< ?2")
    List<TransactionInformation> findByCreateDate(LocalDateTime transSyncStartDate, LocalDateTime endDate);
}
