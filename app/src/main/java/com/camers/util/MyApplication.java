package com.camers.util;

import android.app.Application;
import android.app.Service;
import android.os.Vibrator;
import com.baidu.mapapi.SDKInitializer;
/**
 * 作者：陈杰宇
 * 时间： 2016/1/5 12:47
 */
public class MyApplication extends Application {


    public LocationService locationService;
    public Vibrator mVibrator;
    @Override
    public void onCreate() {
        super.onCreate();
        /***
         * 初始化定位sdk，
         */
        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());

    }
}
