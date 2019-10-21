package io.xls.monitor.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author kevin
 * @date 2019-10-21 11:56
 */
@Data
@Accessors(chain = true)
public class MchtTransactionVo {

    /**
     * 商户名
     */
    private String name;

    /**
     * 值
     */
    private double value;
}
