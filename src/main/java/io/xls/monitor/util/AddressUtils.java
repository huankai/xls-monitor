package io.xls.monitor.util;

import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kevin
 * @date 2019-10-18 12:45
 */
public class AddressUtils {

    private static Map<String, String> map = new HashMap<>();

    static {
        map.put("110000", "北京");
        map.put("120000", "天津");
        map.put("130000", "河北");
        map.put("140000", "山西");
        map.put("150000", "内蒙古");
        map.put("210000", "辽宁");
        map.put("220000", "吉林");
        map.put("230000", "黑龙江");
        map.put("310000", "上海");
        map.put("320000", "江苏");
        map.put("330000", "浙江");
        map.put("340000", "安徽");
        map.put("350000", "福建");
        map.put("360000", "江西");
        map.put("370000", "山东");
        map.put("410000", "河南");
        map.put("420000", "湖北");
        map.put("430000", "湖南");
        map.put("440000", "广东");
        map.put("450000", "广西");
        map.put("460000", "海南");
        map.put("500000", "重庆");
        map.put("510000", "四川");
        map.put("520000", "贵州");
        map.put("530000", "云南");
        map.put("540000", "西藏");
        map.put("610000", "陕西");
        map.put("620000", "甘肃");
        map.put("630000", "青海");
        map.put("640000", "宁夏");
        map.put("650000", "新疆");
        map.put("710000", "台湾");
        map.put("810000", "香港");
        map.put("820000", "澳门");
    }

    public static String getValue(String key) {
        return map.get(key);
    }


    public static String randomValue() {
        int index = RandomUtils.nextInt(0, map.size());
        List<String> list = new ArrayList<>(map.keySet());
        return map.get(list.get(index));
    }
}
