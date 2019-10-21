package io.xls.monitor.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 季度交易统计
 *
 * @author kevin
 * @date 2019-10-21 13:38
 */
@Data
@Accessors(chain = true)
public class QuarterTransVo {

    private List<String> legendData;

    private List<QuarterTransVo.QuarterTransItem> dataList;

    @Data
    @Accessors(chain = true)
    public static class QuarterTransItem {

        /**
         * 季度名称
         */
        private String name;

        /**
         * 总交易额
         */
        private double value;
    }
}
