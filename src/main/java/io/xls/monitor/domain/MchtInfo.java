package io.xls.monitor.domain;

import io.xls.core.data.jpa.domain.AbstractUUIDPersistable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * 商户
 *
 * @author kevin
 * @date 2019-10-17 17:44
 */
@Data
@Entity
@Table(name = "mcht_info")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class MchtInfo extends AbstractUUIDPersistable {

    /**
     * 商户名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 所在省
     */
    @Column(name = "province")
    private String province;

    /**
     * 入驻时间
     */
    @Column(name = "create_date")
    private LocalDateTime createDate;
}
