package com.camers.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.camers.R;
import com.camers.entity.AddressInfo;
import com.camers.entity.Camera;
import com.camers.entity.CameraEntity;
import com.camers.util.Constant;
import com.camers.util.HttpResult;
import com.camers.widget.LoadingDialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class ListSingleActivity extends Activity {
    private LoadingDialog mLoadingDialog; //显示正在加载的对话框
    private List<CameraEntity> list=new ArrayList<>();
    private Camera camera;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_single_view);
        camera=null;//清空数据信息
        getCamerasByKey();//加载数据信息
    }
    /**
     * 初始化数据信息
     */
    public void initView(){
        ListView listView = (ListView) this.findViewById(R.id.listView);
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
        for(CameraEntity camera:list){
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id",camera.getId());
            if(camera.getCameraName().length()>22){
                item.put("carmerName",camera.getCameraName().substring(0,22));
            }else {
                item.put("carmerName", camera.getCameraName());
            }
            data.add(item);
        }
        //创建SimpleAdapter适配器将数据绑定到item显示控件上
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.camer_item,
                new String[]{"id", "carmerName"}, new int[]{R.id.carmer_id, R.id.carmer_name});
        //实现列表的显示
        listView.setAdapter(adapter);
        //条目点击事件
        listView.setOnItemClickListener(new ListSingleActivity.ItemClickListener());
    }

    //获取点击事件
    private final class ItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView listView = (ListView) parent;
            HashMap<String, Object> data = (HashMap<String, Object>) listView.getItemAtPosition(position);
            String carmer_id= data.get("id").toString();
            String carmerName= data.get("carmerName").toString();
            AlertDialog.Builder builder = new AlertDialog.Builder(ListSingleActivity.this);
            builder.setTitle("提示");
            builder.setMessage("您确定要去"+carmerName+"吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //1、去后台获取数据信息
                    initData(carmer_id);
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.show();
        }
    }

    /**
     * 返回菜单页面信息
     */
    public void returnMenu(View view) {
        if(mLoadingDialog!=null){
            mLoadingDialog.dismiss();
        }
        startActivity(new Intent(this,MenuActivity.class));
        finish();//关闭页面
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

    private void showCoverDialog(String msg){
        AlertDialog.Builder builder  = new AlertDialog.Builder(ListSingleActivity.this);
        builder.setTitle("提示信息" ) ;
        builder.setMessage(msg) ;
        builder.setPositiveButton("确定" ,  null );
        builder.show();
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
                //第二步加载地图信息
                if(camera!=null){
                    //加载map数据信息
                    loadMapInfo();
                }else{
                    showCoverDialog("请选择数据信息！");
                }
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

    private void loadMapInfo(){
        AddressInfo addressInfo = new AddressInfo();
        addressInfo.setLng(camera.getBdLng());
        addressInfo.setLat(camera.getBdLat());
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
    //模糊查询数据信息
    private void getCamerasByKey(){
        //获取输入框对象
        EditText e_t_c_name=(EditText)findViewById(R.id.c_id);
        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                hideLoading();//隐藏加载框
                JSONObject jsonObject=JSONObject.parseObject(msg.obj+"");
                JSONArray arr=JSONArray.parseArray(jsonObject.get("data").toString());
                list= JSONObject.parseArray(arr.toJSONString(),CameraEntity.class);
                initView();
            }
        };
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                showLoading();//显示加载框
                //调用后台数据信息
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("key",e_t_c_name.getText().toString());
                new HttpResult(Constant.BASEURL+"getRedisCamers",params,mHandler).getResult();
            }
        };
        e_t_c_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mHandler.removeCallbacks(mRunnable);
                if(!e_t_c_name.getText().toString().isEmpty()){
                    //800毫秒没有输入认为输入完毕
                    mHandler.postDelayed(mRunnable, 1000);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
