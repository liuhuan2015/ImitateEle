package com.liuh.learn.imitateele;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Date: 2018/3/16 10:08
 * Description:
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SDKInitializer.initialize(this);//百度地图
    }
}
