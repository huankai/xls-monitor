package io.xls.monitor.consts;

/**
 * 队列名
 *
 * @author kevin
 * @date 2019-10-21 16:54
 */
public class TopicName {

    /**
     * 总商户数统计与今日商户数统计
     */
    public static final String QUEUE_MCHT_STATICS = "/queue/mchtStatics";

    /**
     * 各省商户数排行（前10名）
     */
    public static final String QUEUE_TOP_PROVINCE_MCHT = "/queue/topProvinceMcht";

    /**
     * 全国各身份交易总额
     */
    public static final String QUEUE_PROVINCE_AMOUNT = "/queue/provinceAmount";

    /**
     * 交易统计
     */
    public static final String QUEUE_TOTAL_TRANS_STATICS = "/queue/transStatics";

    /**
     * 每个商户交易总额
     */
    public static final String QUEUE_SUM_MCHT_STATICS = "/queue/sumMchtStatics";

    /**
     * 季度交易统计
     */
    public static final String QUEUE_QUARTER_TRANS_STATICS = "/queue/quarterTransStatics";

    /**
     * 通道交易统计
     */
    public static final String QUEUE_PASSAGEWAY_TRANS_STATICS = "/queue/passagewayTransStatics";


    /**
     * 价格范围交易统计
     */
    public static final String QUEUE_AMOUNT_RANGE_COUNT_STATICS = "/queue/amountRangeTransStatics";

    /**
     * 年设备交易统计
     */
    public static final String QUEUE_YEAR_EQUIPMENT_TRANS_STATICS = "/queue/yearEquipmentTransStatics";

    /**
     * 当日设备交易统计
     */
    public static final String QUEUE_TODAY_EQUIPMENT_TRANS_STATICS = "/queue/todayEquipmentTransStatics";


}
