package com.liuh.learn.imitateele;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.liuh.learn.imitateele.baidumap.CoordinateConvertUtil;
import com.liuh.learn.imitateele.base.BaseActivity;
import com.liuh.learn.imitateele.listener.PermissionListener;
import com.liuh.learn.imitateele.utils.UIUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 仿饿了么收货地址
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.bmapView)
    MapView mMapView;
    @BindView(R.id.tv_site_name)
    TextView tvSiteName;
    @BindView(R.id.et_site_address)
    TextView etSiteAddress;
    @BindView(R.id.iv_site_marker)
    ImageView ivSiteMarker;
    @BindView(R.id.iv_site_shadow)
    ImageView ivSiteShadow;
    @BindView(R.id.tv_site_confirm)
    TextView tvSiteConfirm;

    BaiduMap mBaiduMap = null;
    public LocationClient mLocationClient = null;


    private LatLng myLocLatlng;//我的位置的坐标点
    private LatLng mapCenterLatlng;//地图中心点坐标点
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;

    private MyLocationListener locationListener = new MyLocationListener();

    private AlertDialog.Builder builder;

    private MyLocationData locData;

    private GeoCoder mSearch;//逆地理编码（即坐标转地址）

    String[] str = {"正在获取地理位置", "正在获取地理位置.", "正在获取地理位置..", "正在获取地理位置..."};
    int strIndex = 0;

    Timer timer = new Timer();
    TimerTask timerTask;

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mBaiduMap = mMapView.getMap();

        mSearch = GeoCoder.newInstance();

        mBaiduMap.setMaxAndMinZoomLevel(20.0f, 10.0f);
        mBaiduMap.setMyLocationEnabled(true);

        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(locationListener);


        LocationClientOption option = new LocationClientOption();
        //可选，设置定位模式，默认是高精度（三种定位模式：高精度，低功耗，仅使用设备）
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置返回经纬度坐标类型，默认gcj02
        option.setCoorType("gcj02");
        //可选，设置发起定位请求的间隔，单位ms,默认为0，表示仅定位一次
//        option.setScanSpan(0);
        //可选，设置是否使用gps，默认是false，但是使用高精度和仅用设备两种定位模式的时候，参数必须设置为true
        option.setOpenGps(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
//        option.setLocationNotify(true);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)
        //option.setIgnoreKillProcess(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false
        option.SetIgnoreCacheException(false);
        //可选，7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位
        option.setWifiCacheTimeOut(5 * 60 * 1000);

        option.setIsNeedAddress(true);
        option.setIsNeedAltitude(true);
        option.setIsNeedLocationDescribe(true);
        option.setIsNeedLocationPoiList(true);
//        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        mLocationClient.setLocOption(option);
        //发起定位
        mLocationClient.start();

        //进行运行时权限申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestRuntimePermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, new MyRequestPermission());
        }


    }


    @Override
    protected void initListener() {
        super.initListener();

        mSearch.setOnGetGeoCodeResultListener(listener);

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

                Log.e("--------mBaidumap", "onMapStatusChangeStart");

                TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, -30);
                translateAnimation.setDuration(300);
                translateAnimation.setFillAfter(true);
                ivSiteMarker.startAnimation(translateAnimation);

                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 2.0f, 1.0f, 2.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setDuration(300);
                scaleAnimation.setFillAfter(true);
                ivSiteShadow.startAnimation(scaleAnimation);

                tvSiteConfirm.setBackgroundResource(R.color.gray_96);
                tvSiteConfirm.setClickable(false);

                timerTask = new TimerTask() {
                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvSiteName.setText(str[strIndex]);
                            }
                        });

                        if (strIndex == str.length - 1) {
                            strIndex = 0;
                        } else {
                            strIndex++;
                        }
                    }
                };

                timer.scheduleAtFixedRate(timerTask, 0, 250);

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                Log.e("--------mBaidumap", "onMapStatusChangeFinish");

                TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, -30, 0);
                translateAnimation.setDuration(300);
                translateAnimation.setFillAfter(true);
                ivSiteMarker.startAnimation(translateAnimation);

                ScaleAnimation scaleAnimation = new ScaleAnimation(2.0f, 1.0f, 2.0f, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setDuration(300);
                scaleAnimation.setFillAfter(true);
                ivSiteShadow.startAnimation(scaleAnimation);

                tvSiteConfirm.setBackgroundResource(R.color.green_4D);
                tvSiteConfirm.setClickable(true);

//                timer.cancel();
                timerTask.cancel();

                mapCenterLatlng = mapStatus.target;

                mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                        .location(mapCenterLatlng));
            }
        });

    }

    @OnClick({R.id.rl_relocate, R.id.et_site_address, R.id.tv_site_confirm, R.id.rl_site_name_choose})
    void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_relocate:
                //重新定位一次,并将地图标记点移动到定位位置附近
                mLocationClient.restart();
                break;
            case R.id.et_site_address:
                etSiteAddress.setCursorVisible(true);
                break;
            case R.id.tv_site_confirm:
                UIUtils.showToast("确定");
                break;
            case R.id.rl_site_name_choose:
                UIUtils.showToast("跳转至地址搜索界面");
                Intent intent = new Intent(this, MyAddressChooseLocListActivity.class);
                intent.putExtra("curLatlng", mapCenterLatlng);
                startActivity(intent);
                break;
        }
    }

    class MyRequestPermission implements PermissionListener {

        @Override
        public void onGranted() {
            Log.e("----------", "获取到了获取位置信息的权限");
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onDenied(List<String> deniedPermissions) {

            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                //如果用户勾选了不再提醒,shouldShowRequestPermissionRationale(...)会返回false
                //但国内定制的ROM，比如小米是永久返回false的。
                //下次进来的时候在这里做一些处理,一般是引导用户到设置里面去设置
                builder = new AlertDialog.Builder(MainActivity.this, R.style.permissionAlertDialog);

                builder.setMessage("应用需要获取用户位置信息,是否前往设置?");

                builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //这段跳转到应用权限设置界面的代码在不同的手机上需要进行适配(小米5X是可以的)
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.fromParts("package", getPackageName(), null));
                        MainActivity.this.startActivity(intent);
                    }
                });

                builder.setNegativeButton("不了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UIUtils.showToast("应用需要获取地理位置信息,否则定位功能可能无法正常使用");
                    }
                });
                builder.setCancelable(false);

                builder.show();
            } else {
                UIUtils.showToast("应用需要获取地理位置信息,否则定位功能可能无法正常使用");
            }

        }
    }

    class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                Toast.makeText(MainActivity.this, "获取当前位置失败", Toast.LENGTH_SHORT).show();
                Log.e("----------", "获取当前位置失败");
                return;
            }

//            String locationDescribe = location.getLocationDescribe();    //获取位置描述信息
//
//            Log.e("--------------", "location.getAddrStr() : " + location.getAddrStr() + ",location.getLocationDescribe() : " + location.getLocationDescribe()
//                    + ",location.getBuildingName() : " + location.getBuildingName());

//            Log.e("--------------", " location.getPoiList().size():" + location.getPoiList().size());

            List<Poi> poiList = location.getPoiList();
//            if (poiList != null && poiList.size() > 0) {
//                for (Poi poi : poiList) {
//                    Log.e("-----------", "poi.getName() : " + poi.getName() + ",poi.describeContents() : " + poi.describeContents());
//                }
//            }

            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
//            Log.e("----------", "location.getLocType():" + location.getLocType());
//            Log.e("----------", "mCurrentLat:" + mCurrentLat + "------mCurrentLon:" + mCurrentLon);
            switch (location.getLocType()) {
                case 61:
                    //GPS定位结果，GPS定位成功
                    mLocationClient.stop();
                    break;
                case 62:
                    //无法获取有效定位依据，定位失败，请检查运营商网络或者WiFi网络是否正常开启，尝试重新请求定位
                    Toast.makeText(MainActivity.this, "定位失败,错误码:" + 62, Toast.LENGTH_SHORT).show();
                    mLocationClient.restart();
                    break;
                case 63:
                    //网络异常，没有成功向服务器发起请求，请确认当前测试手机网络是否通畅，尝试重新请求定位
                    Toast.makeText(MainActivity.this, "定位失败,错误码:" + 63, Toast.LENGTH_SHORT).show();
                    UIUtils.showToast("请检查网络连接");
                    break;
                case 66:
                    //离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果
                    break;
                case 68:
                    //网络连接失败时，查找本地离线定位时对应的返回结果
                    Toast.makeText(MainActivity.this, "定位失败,错误码:" + 68, Toast.LENGTH_SHORT).show();
                    break;
                case 161:
                    //网络定位结果，网络定位成功
                    mLocationClient.stop();
                    break;
                case 162:
                    //请求串密文解析失败，一般是由于客户端SO文件加载失败造成，请严格参照开发指南或demo开发，放入对应SO文件
                    break;
                case 167:
                    //服务端定位失败，请您检查是否禁用获取位置信息权限，尝试重新请求定位
                    break;
                case 505:
                    //AK不存在或者非法，请按照说明文档重新申请AK
                    break;

            }

            myLocLatlng = CoordinateConvertUtil.LatLngConvertFromGcjToBd(new LatLng(mCurrentLat, mCurrentLon));


            locData = new MyLocationData.Builder()
//                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(myLocLatlng.latitude)
                    .longitude(myLocLatlng.longitude).build();
            mBaiduMap.setMyLocationData(locData);
//            if (isFirstLoc) {
//                isFirstLoc = false;
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(myLocLatlng).zoom(20.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
//            }

            mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(myLocLatlng));
        }
    }

    OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {

        public void onGetGeoCodeResult(GeoCodeResult result) {

            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有检索到结果
            }

            //获取地理编码结果
        }

        @Override

        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有找到检索结果
            }

            //获取反向地理编码结果

            if (result.getPoiList() != null && result.getPoiList().size() > 0) {
                //取逆地理编码第一个地址
                PoiInfo poiInfo = result.getPoiList().get(0);

                tvSiteName.setText(poiInfo.name);
                etSiteAddress.setText(poiInfo.address);

//                for (PoiInfo poiInfo1 : result.getPoiList()) {
//                    Log.e("--------------", " poiInfo1.name : " + poiInfo1.name + "poiInfo1.address : " + poiInfo1.address);
//                }
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getAction() == MessageEvent.ACTION_SITE_CHOOSED) {
            Log.e("-----------------", "onMessageReceived...ACTION_SITE_CHOOSED");
            PoiInfo poiInfo = (PoiInfo) messageEvent.getMessage();

            mapCenterLatlng = poiInfo.location;

            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(mapCenterLatlng).zoom(20.0f);

            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            tvSiteName.setText(poiInfo.name);
            etSiteAddress.setText(poiInfo.address);

//            mBaidumap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registEventBus(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregistEventBus(this);
        mLocationClient.stop();
        mSearch.destroy();
        timer.cancel();
    }
}
