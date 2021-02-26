package com.camers.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.camers.R;
import com.camers.entity.Camera;
import com.camers.entity.CameraLocation;
import com.camers.util.Constant;
import com.camers.util.HttpResult;
import com.camers.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MulMapActivity extends Activity {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Context mContext;
    private LoadingDialog mLoadingDialog; //显示正在加载的对话框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
        // 注意该方法要再setContentView方法之前实现
        mContext = getApplicationContext();
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.mulmap);
        String ids=getIntent().getStringExtra("ids");
        initData(ids);
        initView();
    }

    /**
     * 初始化数据信息
     * @param ids
     */
    private void initData(String ids){
        //发起HTTP请求
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ids",ids);
        Handler mHandler=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                JSONObject jsonObject = JSONObject.parseObject(msg.obj + "");
                JSONArray arr=JSONArray.parseArray(jsonObject.get("data").toString());
                List<Camera> list = JSONObject.parseArray(arr.toJSONString(), Camera.class);
                initMarker(list);
            }
        };
        //调用后台数据信息
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                new HttpResult(Constant.BASEURL+"selectCarmersByIds",params,mHandler).getResult();
            }
        };
        mHandler.postDelayed(mRunnable, 0);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        // 获取地图控件引用
        mMapView = (MapView) findViewById(R.id.mv_map);
        mBaiduMap = mMapView.getMap();
    }

    /**
     * 点击地图上的标记
     * @param
     */
    private void initMarker(List<Camera> list) {
        mBaiduMap.clear();
        LatLng latLng = null;
        OverlayOptions options;
        BitmapDescriptor bd_temp;
        View v_temp= LayoutInflater.from(getApplicationContext()).inflate(R.layout.mapitem, null);//加载自定义的布局
        for(Camera camera : list) {
            // 经纬度
            latLng = new LatLng(camera.getBdLat(),camera.getBdLng());
            TextView tv_temp = (TextView) v_temp.findViewById(R.id.baidumap_custom_text);//获取自定义布局中的textview
            ImageView img_temp = (ImageView) v_temp.findViewById(R.id.baidumap_custom_img);//获取自定义布局中的imageview
            tv_temp.setText(camera.getCameraName());//设置要显示的文本
            img_temp.setImageResource(R.drawable.icon_openmap_mark);//设置marker的图标
            bd_temp=BitmapDescriptorFactory.fromView(v_temp);
            // 图标
            options = new MarkerOptions().position(latLng).icon(bd_temp).zIndex(5);
            mBaiduMap.addOverlay(options);
        }
        // 定义地图状态(精确到50米)
        //设定中心点坐标
        //LatLng cenpt = new LatLng(25.700801,100.170478);
        LatLng cenpt = new LatLng(25.636756,100.201964);
        MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(13).build();
        // 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        // 移到第四个位置
        mBaiduMap.setMapStatus(mapStatusUpdate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    /**
     * 显示加载的进度款
     */
    public void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this, getString(R.string.loading), false);
        }
        mLoadingDialog.show();
    }


    /**
     * 隐藏加载的进度框
     */
    public void hideLoading() {
        if (mLoadingDialog != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoadingDialog.hide();
                }
            });

        }
    }
}