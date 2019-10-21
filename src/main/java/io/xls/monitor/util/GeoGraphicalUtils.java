package io.xls.monitor.util;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kevin
 * @date 2019-10-18 9:28
 */
public class GeoGraphicalUtils {

    private static final Map<String, GeoGraphical> geoGraphicals = new HashMap<>(31);

    public static GeoGraphical get(String provinceName) {
        return geoGraphicals.get(provinceName);
    }

    static {
        geoGraphicals.put("北京", new GeoGraphical().setId(1).setName("北京").setLongitude(116.405289).setLatitude(39.904987));
        geoGraphicals.put("天津", new GeoGraphical().setId(2).setName("天津").setLongitude(117.190186).setLatitude(39.125595));
        geoGraphicals.put("河北", new GeoGraphical().setId(3).setName("河北").setLongitude(114.502464).setLatitude(38.045475));
        geoGraphicals.put("山西", new GeoGraphical().setId(4).setName("山西").setLongitude(112.549248).setLatitude(37.857014));
        geoGraphicals.put("内蒙古", new GeoGraphical().setId(5).setName("内蒙古").setLongitude(111.751990).setLatitude(40.841490));
        geoGraphicals.put("辽宁", new GeoGraphical().setId(6).setName("辽宁").setLongitude(123.429092).setLatitude(41.796768));
        geoGraphicals.put("吉林", new GeoGraphical().setId(7).setName("吉林").setLongitude(125.324501).setLatitude(43.886841));
        geoGraphicals.put("黑龙江", new GeoGraphical().setId(8).setName("黑龙江").setLongitude(126.642464).setLatitude(45.756966));
        geoGraphicals.put("上海", new GeoGraphical().setId(9).setName("上海").setLongitude(121.472641).setLatitude(31.231707));
        geoGraphicals.put("江苏", new GeoGraphical().setId(10).setName("江苏").setLongitude(118.76741).setLatitude(32.041546));
        geoGraphicals.put("浙江", new GeoGraphical().setId(11).setName("浙江").setLongitude(120.15358).setLatitude(30.287458));
        geoGraphicals.put("安徽", new GeoGraphical().setId(12).setName("安徽").setLongitude(117.283043).setLatitude(31.861191));
        geoGraphicals.put("福建", new GeoGraphical().setId(13).setName("福建").setLongitude(119.306236).setLatitude(26.075302));
        geoGraphicals.put("江西", new GeoGraphical().setId(14).setName("江西").setLongitude(115.892151).setLatitude(28.676493));
        geoGraphicals.put("山东", new GeoGraphical().setId(15).setName("山东").setLongitude(117.000923).setLatitude(36.675808));
        geoGraphicals.put("河南", new GeoGraphical().setId(16).setName("河南").setLongitude(113.665413).setLatitude(34.757977));
        geoGraphicals.put("湖北", new GeoGraphical().setId(17).setName("湖北").setLongitude(114.298569).setLatitude(30.584354));
        geoGraphicals.put("湖南", new GeoGraphical().setId(18).setName("湖南").setLongitude(112.982277).setLatitude(28.19409));
        geoGraphicals.put("广东", new GeoGraphical().setId(19).setName("广东").setLongitude(113.28064).setLatitude(23.125177));
        geoGraphicals.put("广西", new GeoGraphical().setId(20).setName("广西").setLongitude(108.320007).setLatitude(22.82402));
        geoGraphicals.put("海南", new GeoGraphical().setId(21).setName("海南").setLongitude(110.199890).setLatitude(20.044220));
        geoGraphicals.put("重庆", new GeoGraphical().setId(22).setName("重庆").setLongitude(106.504959).setLatitude(29.533155));
        geoGraphicals.put("四川", new GeoGraphical().setId(23).setName("四川").setLongitude(104.065735).setLatitude(30.659462));
        geoGraphicals.put("贵州", new GeoGraphical().setId(24).setName("贵州").setLongitude(106.713478).setLatitude(26.578342));
        geoGraphicals.put("云南", new GeoGraphical().setId(25).setName("云南").setLongitude(102.71225).setLatitude(25.040609));
        geoGraphicals.put("西藏", new GeoGraphical().setId(26).setName("西藏").setLongitude(91.11450).setLatitude(29.644150));
        geoGraphicals.put("陕西", new GeoGraphical().setId(28).setName("陕西").setLongitude(108.948021).setLatitude(34.263161));
        geoGraphicals.put("甘肃", new GeoGraphical().setId(27).setName("甘肃").setLongitude(103.834170).setLatitude(36.061380));
        geoGraphicals.put("青海", new GeoGraphical().setId(29).setName("青海").setLongitude(101.777820).setLatitude(36.617290));
        geoGraphicals.put("宁夏", new GeoGraphical().setId(30).setName("宁夏").setLongitude(106.232480).setLatitude(38.486440));
        geoGraphicals.put("新疆", new GeoGraphical().setId(31).setName("新疆").setLongitude(87.616880).setLatitude(43.826630));
    }

    @Data
    @Accessors(chain = true)
    public static class GeoGraphical {

        private Integer id;

        private String name;

        /**
         * 经度
         */
        private Double longitude;

        /**
         * 纬度
         */
        private Double latitude;
    }
}
