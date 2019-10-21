package io.xls.monitor.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.xls.monitor.util.GeoGraphicalUtils;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 全国省交易金额
 *
 * @author kevin
 * @date 2019-10-18 15:40
 */
@Data
@Accessors(chain = true)
public class ProvinceTransctioinVo {

    private String name;

    @JsonIgnore
    private Double sum;

    /**
     * 有三个元素，分别为 精度、纬度、值
     */
    public Double[] getValue() {
        GeoGraphicalUtils.GeoGraphical geoGraphical = GeoGraphicalUtils.get(name);
        if (null != geoGraphical) {
            return new Double[]{geoGraphical.getLongitude(), geoGraphical.getLatitude(), sum};
        }
        return new Double[]{0D, 0D, 0D};
    }
}
