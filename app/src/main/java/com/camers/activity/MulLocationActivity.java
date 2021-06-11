package com.camers.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.camers.R;
import com.camers.util.GetDistanceUtils;

import java.math.BigDecimal;

public class MulLocationActivity extends Activity {
   private final String TAG="MulLocationActivity";
   private LocationClient locationClient;
    private BDLocationListener bdLocationListener;
    private TextView editText;
    private double currentLongitude=0;
    private double currentLatitude=0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintest);
        SDKInitializer.initialize(getApplicationContext());
        initView();
        // 声明LocationClient类  
        locationClient=new LocationClient(getApplicationContext());
        bdLocationListener=new MyBDLocationListener();
        // 注册监听  
        locationClient.registerLocationListener(bdLocationListener);
        getLocation();
        jisuanStance();
    }

    public void initView(){
        editText=(TextView)this.findViewById(R.id.addressId);
    }

    public void jisuanStance(){

    }
    /**
     * 获取当前位置信息
     */
    public void getLocation(){
        // 声明定位参数
        LocationClientOption option=new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式 高精度  
        option.setCoorType("bd09ll");// 设置返回定位结果是百度经纬度 默认gcj02  
        option.setScanSpan(5000);// 设置发起定位请求的时间间隔 单位ms  
        option.setIsNeedAddress(true);// 设置定位结果包含地址信息  
        option.setNeedDeviceDirect(true);// 设置定位结果包含手机机头 的方向  
        // 设置定位参数  
        locationClient.setLocOption(option);
        // 启动定位  
        locationClient.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消监听函数
        if(locationClient!=null){
            locationClient.unRegisterLocationListener(bdLocationListener);
        }
    }
    private class MyBDLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation location) {
           if(location!=null){
               // 根据BDLocation 对象获得经纬度以及详细地址信息
               currentLatitude=location.getLatitude();
               currentLongitude=location.getLongitude();
               if(locationClient.isStarted()){
                   // 获得位置之后停止定位  
                   locationClient.stop();
                }
            }
        }
    }
}
