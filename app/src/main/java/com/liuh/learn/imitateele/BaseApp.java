package com.liuh.learn.imitateele;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.baidu.mapapi.SDKInitializer;
import com.liuh.learn.imitateele.handler.CrashHandler;

/**
 * Date: 2018/3/16 10:08
 * Description:
 */

public class BaseApp extends Application {

    private static Context mContext;//上下文
    private static Thread mMainThread;//主线程
    private static long mMainThreadId;//主线程id
    private static Looper mMainLooper;//主线程的消息循环队列
    private static Handler mHandler;//主线程的Handler

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mMainThread = Thread.currentThread();
        mMainThreadId = android.os.Process.myTid();
        mHandler = new Handler();

        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());

        SDKInitializer.initialize(this);//百度地图
    }

    public static Context getmContext() {
        return mContext;
    }

    public static Thread getmMainThread() {
        return mMainThread;
    }

    public static long getmMainThreadId() {
        return mMainThreadId;
    }

    public static Looper getmMainLooper() {
        return mMainLooper;
    }

    public static Handler getmHandler() {
        return mHandler;
    }
}
