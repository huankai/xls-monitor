package io.xls.monitor.domain;

import io.xls.core.data.jpa.domain.AbstractUUIDPersistable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易
 *
 * @author kevin
 * @date 2019-10-17 17:51
 */
@Data
@Entity
@Table(name = "transaction_information")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TransactionInformation extends AbstractUUIDPersistable {

    /**
     * 商户id
     */
    @Column(name = "mcht_id")
    private String mchtId;

    /**
     * 交易时间
     */
    @Column(name = "create_date")
    private LocalDateTime createDate;

    /**
     * 交易发生地:所在省
     */
    @Column(name = "province")
    private String province;

    /**
     * 交易用户Id
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 交易用户名
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 交易金额
     */
    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "passageway")
    private String passageway;

    @Column(name = "equipment")
    private String equipment;
}
