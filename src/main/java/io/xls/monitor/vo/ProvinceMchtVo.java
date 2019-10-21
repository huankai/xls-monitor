package io.xls.monitor.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 每个省商户数
 *
 * @author kevin
 * @date 2019-10-18 10:09
 */
@Data
@Accessors(chain = true)
public class ProvinceMchtVo {

    /**
     * 省名
     */
    private String name;

    /**
     * 值
     */
    private Long value;

}
