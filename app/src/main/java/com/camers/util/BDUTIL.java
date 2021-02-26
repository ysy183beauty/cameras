package com.camers.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import com.camers.R;

import java.util.ArrayList;
import java.util.List;

public class BDUTIL {
    /**
     * 百度导航
     * @param context
     */
    public static void baiduGuide(Context context,double srcLat,double srcLng,double desLat,double desLng) {
        //double[] baiduLoc = GpsUtils.gcj02_To_Bd09(location[0], location[1]);
        String srcLatLng=srcLat+","+srcLng;
        String desLatLng=desLat+","+desLng;
        if (isAvilible(context, "com.baidu.BaiduMap")) {//传入指定应用包名
            try {
                Intent intent = Intent.getIntent("intent://map/direction?" +
                        "origin=latlng:"+srcLatLng+"&" +   //起点  此处不传值默认选择当前位置
                        "destination=latlng:" +desLatLng+ "|name:目的地" +        //终点
                        "&mode=driving" +          //导航路线方式
                        "&region=" +           //
                        "&src=" +
                        context.getResources().getString(R.string.app_name) +
                        "#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                context.startActivity(intent); //启动调用
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {//未安装
            //market为路径，id为包名
            //显示手机上所有的market商店
            Toast.makeText(context, "您尚未安装百度地图", Toast.LENGTH_LONG).show();
            Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
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
