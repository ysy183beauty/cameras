package com.camers.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.camers.R;
import com.camers.entity.Camera;
import com.camers.util.Constant;
import com.camers.util.HttpResult;
import com.camers.widget.LoadingDialog;

import java.util.HashMap;

public class EditActivity extends Activity {
    private LoadingDialog mLoadingDialog; //显示正在加载的对话框
    private EditText ext_c_name;
    private EditText ext_c_lng;
    private EditText ext_c_lat;
    private EditText tv_c_id;
    private Camera camera;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eidtcarmer);
        intView();//初始化数据信息
        String camre_id=getIntent().getStringExtra("carmer_id");
        initData(camre_id);//初始化数据信息
    }

    private void intView(){
        ext_c_name=(EditText)findViewById(R.id.camerNameEdit);
        ext_c_name.setInputType(InputType.TYPE_NULL);
        ext_c_lng=(EditText)findViewById(R.id.lngEdit);
        ext_c_lat=(EditText)findViewById(R.id.latEdit);
        tv_c_id=(EditText)findViewById(R.id.editId);
        tv_c_id.setInputType(InputType.TYPE_NULL);
    }

    /**
     * 返回菜单页面信息
     */
    public void returnMenu(View view) {
        if(mLoadingDialog!=null){
            mLoadingDialog.dismiss();
        }
        startActivity(new Intent(this,ListActivity.class));
        finish();//关闭页面
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
               ext_c_lng.setText(camera.getLng()+"");
               ext_c_lat.setText(camera.getLat()+"");
               tv_c_id.setText(camera.getId()+"");
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

   public void doReset(View view){
        //清空数据信息
       ext_c_lng.setText("");
       ext_c_lat.setText("");
   }

    /**
     * 保存数据信息
     * @param view
     */
   public void doSave(View view){
       //登录一般都是请求服务器来判断密码是否正确，要请求网络，要子线程
       showLoading();//显示加载框
       //发起HTTP请求
       HashMap<String, String> params = new HashMap<String, String>();
       params.put("id",tv_c_id.getText().toString().trim());
       params.put("cameraName",ext_c_name.getText().toString().trim());
       params.put("lng",ext_c_lng.getText().toString().trim());
       params.put("lat",ext_c_lat.getText().toString().trim());
       Handler mHandler=new Handler(){
           @Override
           public void handleMessage(Message msg) {
               hideLoading();//隐藏加载框
               JSONObject jsonObject=JSONObject.parseObject(msg.obj+"");
               Boolean flag=Boolean.parseBoolean(jsonObject.get("result").toString());
               if(flag){
                   returnMenu(view);
               }else{
                   showCoverDialog("保存失败！");
               }
           }
       };
       Thread loginRunnable = new Thread() {
           @Override
           public void run() {
               super.run();
               new HttpResult(Constant.BASEURL+"updateCameraInfo",params,mHandler).getResult();
           }
       };
       loginRunnable.start();
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
        AlertDialog.Builder builder  = new AlertDialog.Builder(EditActivity.this);
        builder.setTitle("提示信息" ) ;
        builder.setMessage(msg) ;
        builder.setPositiveButton("确定" ,  null );
        builder.show();
    }
}
