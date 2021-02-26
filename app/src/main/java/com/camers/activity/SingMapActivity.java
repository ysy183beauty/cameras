package com.camers.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.camers.R;
import com.camers.entity.AddressInfo;
import com.camers.entity.Camera;
import com.camers.util.Constant;
import com.camers.util.HttpResult;
import com.camers.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SingMapActivity extends Activity {
    private EditText ext_c_name;
    private EditText ext_c_lng;
    private EditText ext_c_lat;
    private Camera camera;
    private LoadingDialog mLoadingDialog; //显示正在加载的对话框
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singlemap);
        intView();//初始化数据信息
        //通主键获取
        String id=getIntent().getStringExtra("carmer_id");
       //调用后台获取数据信息
        initData(id);
    }

    private void intView(){
        ext_c_name=(EditText)findViewById(R.id.camerNameEdit);
        ext_c_name.setInputType(InputType.TYPE_NULL);
        ext_c_lng=(EditText)findViewById(R.id.lngEdit);
        ext_c_lat=(EditText)findViewById(R.id.latEdit);
        ext_c_lng.setInputType(InputType.TYPE_NULL);
        ext_c_lat.setInputType(InputType.TYPE_NULL);
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

    private void initData(String camre_id){
        //登录一般都是请求服务器来判断密码是否正确，要请求网络，要子线程
        showLoading();//显示加载框
        //发起HTTP请求
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id",camre_id);
        Handler mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                hideLoading();//隐藏加载框
                JSONObject jsonObject=JSONObject.parseObject(msg.obj+"");
                camera= (Camera) JSONObject.parseObject(jsonObject.toJSONString(), Camera.class);
                ext_c_name.setText(camera.getCameraName());
                ext_c_lng.setText(camera.getBdLng()+"");
                ext_c_lat.setText(camera.getBdLat()+"");
            }
        };
        Thread loginRunnable = new Thread() {
            @Override
            public void run() {
                super.run();
                new HttpResult(Constant.BASEURL+"selectCarmerById",params,mHandler).getResult();
            }
        };
        loginRunnable.start();
    }

    private void showCoverDialog(String msg){
        AlertDialog.Builder builder  = new AlertDialog.Builder(SingMapActivity.this);
        builder.setTitle("提示信息" ) ;
        builder.setMessage(msg) ;
        builder.setPositiveButton("确定" ,  null );
        builder.show();
    }

    /**
     * 返回菜单页面信息
     */
    public void returnMenu(View view) {
        if(mLoadingDialog!=null){
            mLoadingDialog.dismiss();
        }
        startActivity(new Intent(this,ListSingleActivity.class));
        finish();//关闭页面
    }

    /**
     * 点击导航
     */
    public void doDriving(View view) {
        String bdLng=ext_c_lng.getText().toString();
        if(bdLng.isEmpty()){
            showCoverDialog("经度不能为空！");
            return;
        }
        String bdLat=ext_c_lat.getText().toString();
        if(bdLat.isEmpty()){
            showCoverDialog("纬度不能为空！");
            return;
        }
        AddressInfo addressInfo = new AddressInfo();
        addressInfo.setLng(Double.parseDouble(bdLng));
        addressInfo.setLat(Double.parseDouble(bdLat));
        if(view.getId()==R.id.bdDaoHang){
            if (isAvilible(this, "com.baidu.BaiduMap")) {//传入指定应用包名
                try {
                    Intent intent = Intent.getIntent("intent://map/direction?" +
                            "destination=latlng:" + addressInfo.getLat() + "," + addressInfo.getLng() + "|name:我的目的地" +        //终点
                            "&mode=driving&" +          //导航路线方式
                            "&src=appname#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                    startActivity(intent); //启动调用
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else {//未安装
                //market为路径，id为包名
                //显示手机上所有的market商店
                Toast.makeText(this, "您尚未安装百度地图", Toast.LENGTH_LONG).show();
                Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivity(intent);
                }
            }
        }

    }

    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param context
     * @param packageName：应用包名
     * @return
     */
    public static boolean isAvilible(Context context, String packageName) {
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }
}
