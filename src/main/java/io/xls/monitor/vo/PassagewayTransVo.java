package io.xls.monitor.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author kevin
 * @date 2019-10-21 14:06
 */
@Data
@Accessors(chain = true)
public class PassagewayTransVo {

    private List<String> legendData;

    private List<PassagewayTransItem> dataList;

    @Data
    @Accessors(chain = true)
    public static class PassagewayTransItem {

        /**
         * 通道名称
         */
        private String name;

        /**
         * 总交易额
         */
        private double value;
    }
}
