package com.camers.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.camers.R;
import com.camers.util.Constant;
import com.camers.util.HttpResult;
import com.camers.widget.LoadingDialog;

import java.util.HashMap;

public class AddActivity extends Activity{
    private LoadingDialog mLoadingDialog; //显示正在加载的对话框
    //布局文件信息
    private EditText et_c_num;
    private EditText et_c_name;
    private EditText et_c_lng;
    private EditText et_c_lat;
    private Spinner sn_c_region;
    //正则表达式
    private String regexNum = "\\d{6}([-]\\d+)?";
    private String regexLng="\\d+(\\.\\d{6,10})";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addinfo);
        initViews();
        loadRegionInfo();
    }

    private void loadRegionInfo(){
        Spinner spinner = (Spinner) findViewById(R.id.region);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item); //系统sdk里面的R文件
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add("--请选择--");
        adapter.add("大理镇");
        adapter.add("银桥镇");
        adapter.add("湾桥镇");
        adapter.add("喜洲镇");
        adapter.add("上关镇");
        adapter.add("双廊镇");
        spinner.setAdapter(adapter);
    }

    private void initViews(){
        et_c_num= (EditText) findViewById(R.id.camerNum);
        et_c_name= (EditText) findViewById(R.id.camerName);
        et_c_lng=(EditText) findViewById(R.id.lng);
        et_c_lat=(EditText) findViewById(R.id.lat);
        sn_c_region=(Spinner)findViewById(R.id.region);
    }

    /**
     * 返回菜单页面信息
     */
    public void returnMenu(View view) {
        startActivity(new Intent(this,MenuActivity.class));
        finish();//关闭页面
    }

    /**
     * 点击新增信息按钮
     * @param view
     */
   public void add(View view){
       String carmerNum=et_c_num.getText().toString().trim();
       if(carmerNum.length()==0){
           showCoverDialog("摄像头编号不能为空");
           return;
       }
       if(!carmerNum.matches(regexNum)){
           showCoverDialog("摄像头编号只能为六位数字！");
           return;
       }
       String carmerName=et_c_name.getText().toString().trim();
       //获取经度
       String lng=et_c_lng.getText().toString().trim();
       if(lng.length()==0){
           showCoverDialog("经度不能为空");
           return;
       }
       if(!lng.matches(regexLng)){
           showCoverDialog("经度格式不正确！");
           return;
       }
       //获取纬度
       String lat=et_c_lat.getText().toString().trim();
       if(lat.length()==0){
           showCoverDialog("纬度不能为空");
           return;
       }
       if(!lat.matches(regexLng)){
           showCoverDialog("纬度格式不正确！");
           return;
       }
       String regionName=sn_c_region.getSelectedItem().toString().trim();
       if(regionName.length()==0||"--请选择--".equals(regionName)){
           showCoverDialog("区域名称不能为空！");
           return;
       }
       //登录一般都是请求服务器来判断密码是否正确，要请求网络，要子线程
       showLoading();//显示加载
       //发起HTTP请求
       HashMap<String, String> params = new HashMap<String, String>();
       params.put("carmerNum",carmerNum);
       params.put("cameraName",carmerName);
       params.put("lng",lng);
       params.put("lat",lat);
       params.put("regionName",regionName);
       Handler mHandler=new Handler() {
           @Override
           public void handleMessage(Message msg) {
               JSONObject jsonObject = JSONObject.parseObject(msg.obj + "");
               Boolean flag=Boolean.parseBoolean(jsonObject.get("flag").toString());
               hideLoading();//隐藏加载框
               if(flag){
                   showCoverDialog(jsonObject.get("msg").toString());
                   returnMenu(view);
               }else{
                   showCoverDialog(jsonObject.get("msg").toString());
               }

           }
       };
       Thread save = new Thread() {
           @Override
           public void run() {
               super.run();
               new HttpResult(Constant.BASEURL+"saveCameraInfo",params,mHandler).getResult();
           }
       };
       save.start();
      }

    /**
     * 重置信息
     * @param view
     */
      public void reset(View view){
          et_c_num.setText("");
          et_c_name.setText("");
          et_c_lng.setText("");
          et_c_lat.setText("");
          sn_c_region.setSelection(0);
      }

    /**
     * 显示加载的进度款
     */
    public void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this, getString(R.string.dealData), false);
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
        AlertDialog.Builder builder  = new AlertDialog.Builder(AddActivity.this);
        builder.setTitle("提示信息" ) ;
        builder.setMessage(msg) ;
        builder.setPositiveButton("确定" ,  null );
        builder.show();
    }
}
