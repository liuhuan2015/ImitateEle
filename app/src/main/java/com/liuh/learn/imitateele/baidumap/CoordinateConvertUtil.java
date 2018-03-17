package com.liuh.learn.imitateele.baidumap;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

/**
 * Date: 2018/2/4 14:01
 * Description:百度官方提供的坐标转换方法
 * <p>
 * http://lbsyun.baidu.com/index.php?title=androidsdk/guide/coordtrans
 * <p>
 * 只提供了gcj坐标系,gps坐标系转换成bd坐标系的方法,没有提供反向转换的方法
 * <p>
 * 后台方面返回坐标位置时建议返回gcj类型的坐标点.
 */

public class CoordinateConvertUtil {

    /**
     * 把WGS84,GCJ02坐标转换为BD09坐标
     *
     * @param sourceLatLng
     * @return
     */
    public static LatLng LatLngConvertFromGcjToBd(LatLng sourceLatLng) {
        // 将google地图、soso地图、aliyun地图、mapabc地图和amap地图// 所用坐标转换成百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.COMMON);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }

    /**
     * 把gps坐标转换成BD09坐标
     *
     * @param sourceLatLng
     * @return
     */
    public static LatLng LatLngConvertFromGpsToBd(LatLng sourceLatLng) {
        // 将GPS设备采集的原始GPS坐标转换成百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }

}
