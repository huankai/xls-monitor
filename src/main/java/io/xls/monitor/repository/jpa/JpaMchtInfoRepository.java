package io.xls.monitor.repository.jpa;

import io.xls.core.data.jpa.repository.StringIdJpaRepository;
import io.xls.monitor.domain.MchtInfo;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author kevin
 * @date 2019-10-23 9:02
 */
public interface JpaMchtInfoRepository extends StringIdJpaRepository<MchtInfo> {

    @Query(nativeQuery = true,value = "SELECT * FROM mcht_info WHERE create_date >= ?1 AND create_date < ?2")
    List<MchtInfo> findByCreateDate(LocalDateTime startDate, LocalDateTime endDate);
}
