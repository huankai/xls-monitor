package io.xls.monitor.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author kevin
 * @date 2019-10-21 14:27
 */
@Data
@Accessors(chain = true)
public class AmountRangeVo {

    /**
     * 范围
     */
    private List<String> xAxisData;

    /**
     * 总交易次数
     */
    private List<Long> dataList;
}
