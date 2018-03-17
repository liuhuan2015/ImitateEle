package com.liuh.learn.imitateele.base;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.liuh.learn.imitateele.MainActivity;
import com.liuh.learn.imitateele.listener.PermissionListener;
import com.liuh.learn.imitateele.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Author:liuh
 * Date: 2017/11/29:16:52
 * Description:
 */

public abstract class BaseActivity extends AppCompatActivity {
    private static long mPreTime;
    private static Activity mCurrentActivity;//对所有的Activity进行管理
    public static List<Activity> mActivities = new LinkedList<Activity>();
    protected Bundle saveInstanceState;
    public PermissionListener mPermissionListener;
    protected boolean isLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.saveInstanceState = savedInstanceState;
        //初始化的时候将其添加到集合中
        synchronized (mActivities) {
            mActivities.add(this);
        }

        setContentView(provideContentViewId());
        ButterKnife.bind(this);

        initView();

        initData();

        initListener();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mCurrentActivity = this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCurrentActivity = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //销毁的时候从集合中移除
        synchronized (mActivities) {
            mActivities.remove(this);
        }

    }

    public static Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    @Override
    public void onBackPressed() {
        if (mCurrentActivity instanceof MainActivity) {
            if (System.currentTimeMillis() - mPreTime > 2000) {
                UIUtils.showToast("再按一次，退出应用");
                mPreTime = System.currentTimeMillis();
                return;
            }
        }
        super.onBackPressed();
    }

    public boolean isEventBusRegisted(Object subscriber) {
        return EventBus.getDefault().isRegistered(subscriber);
    }

    public void registEventBus(Object subscriber) {
        if (!isEventBusRegisted(subscriber)) {
            EventBus.getDefault().register(subscriber);
        }
    }

    public void unregistEventBus(Object subscriber) {
        if (isEventBusRegisted(subscriber)) {
            EventBus.getDefault().unregister(subscriber);
        }
    }


    /**
     * 申请运行时权限
     *
     * @param permissions
     * @param permissionListener
     */
    public void requestRuntimePermission(String[] permissions, PermissionListener permissionListener) {

        mPermissionListener = permissionListener;
        List<String> permissionList = new ArrayList<>();


        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), 1);
        } else {
            permissionListener.onGranted();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    List<String> deniedPermission = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult == PackageManager.PERMISSION_DENIED) {
                            deniedPermission.add(permission);
                        }
                    }

                    if (deniedPermission.isEmpty()) {
                        mPermissionListener.onGranted();
                    } else {
                        mPermissionListener.onDenied(deniedPermission);
                    }
                }
                break;

        }

    }

    protected void initView() {

    }


    protected void initData() {

    }

    protected void initListener() {

    }

    //提供当前界面的布局文件id，由子类实现
    protected abstract int provideContentViewId();


}
