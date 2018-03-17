package com.liuh.learn.imitateele.utils;

import android.util.Log;

import com.liuh.learn.imitateele.BuildConfig;

/**
 * Author:liuh
 * Date: 2017/11/30 10:26
 * Description:
 */

public class LogUtil {

    public static void e(String tag,String msg){
        if(BuildConfig.DEBUG){
            Log.e(tag,msg);
        }
    }

}
