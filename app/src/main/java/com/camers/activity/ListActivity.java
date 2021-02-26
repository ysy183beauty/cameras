package com.camers.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.camers.R;
import com.camers.entity.CameraEntity;
import com.camers.util.Constant;
import com.camers.util.HttpResult;
import com.camers.widget.LoadingDialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class ListActivity extends Activity {
    private LoadingDialog mLoadingDialog; //显示正在加载的对话框
    private List<CameraEntity> list=new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        getCamerasByKey();
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
        listView.setOnItemClickListener(new ItemClickListener());
    }

    //获取点击事件
    private final class ItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView listView = (ListView) parent;
            HashMap<String, Object> data = (HashMap<String, Object>) listView.getItemAtPosition(position);
            String carmer_id= data.get("id").toString();
            String carmerName=data.get("carmerName").toString();
            AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
            builder.setTitle("提示");
            builder.setMessage("您确定要修改"+carmerName+"的信息吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(ListActivity.this,EditActivity.class);
                    intent.putExtra("carmer_id",carmer_id);
                    startActivity(intent);
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
