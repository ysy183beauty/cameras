package com.camers.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.camers.R;
import com.camers.entity.AddressInfo;
import com.camers.util.BDLocationUtils;
import com.camers.util.Constant;
import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends Activity implements View.OnClickListener {
    private Button mBaiduBtn;
   private AddressInfo addressInfo = new AddressInfo();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.location);
        //设置你目的地的经纬度
        addressInfo.setLat(25.76127753458261);
        addressInfo.setLng(100.13022949827635);
        initView();
    }

    private void initView() {
        mBaiduBtn =(Button)findViewById(R.id.baiduBtn);
        mBaiduBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.baiduBtn){
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

    private void showCoverDialog(String msg){
        AlertDialog.Builder builder  = new AlertDialog.Builder(LocationActivity.this);
        builder.setTitle("提示信息" ) ;
        builder.setMessage(msg) ;
        builder.setPositiveButton("确定" ,  null );
        builder.show();
    }
}
