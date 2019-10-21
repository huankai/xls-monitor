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
public class EquipmentTransVo {

    /**
     * X 轴数据,设备名称
     */
    private List<String> xAxisData;

    /**
     * 交易值
     */
    private List<Double> dataList;
}
