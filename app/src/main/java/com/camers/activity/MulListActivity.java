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
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
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
import java.util.Map;
public class MulListActivity extends Activity {
    private AutoCompleteTextView auto_CompleteTextView;
    private String keys=null;
    private LoadingDialog mLoadingDialog; //显示正在加载的对话框
    private List<CameraEntity> list=new ArrayList<>();
    private Map<Integer,CameraEntity> selectMap=new HashMap<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_mul);
        initComplete();
    }

    private void initComplete(){
        //自动完成控件
        auto_CompleteTextView=(AutoCompleteTextView)findViewById(R.id.auto_CompleteTextView);
        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                hideLoading();//隐藏加载框
                JSONObject jsonObject=JSONObject.parseObject(msg.obj+"");
                JSONArray arr=JSONArray.parseArray(jsonObject.get("data").toString());
                list= JSONObject.parseArray(arr.toJSONString(),CameraEntity.class);
                initData();

            }
        };
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                showLoading();//显示加载框
                //调用后台数据信息
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("key",keys);
                new HttpResult(Constant.BASEURL+"getRedisCamers",params,mHandler).getResult();
            }
        };
        //输入完成的事件
        auto_CompleteTextView.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable editable) {
                initData();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                keys= s.toString();
                mHandler.removeCallbacks(mRunnable);
                if(!keys.isEmpty()){
                    //800毫秒没有输入认为输入完毕
                    mHandler.postDelayed(mRunnable, 1000);
                }
            }
        });
        //点击每一项事件
        auto_CompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//点击项
                Map<String,String> itemAtPosition = (Map<String,String>) parent.getItemAtPosition(position);
                auto_CompleteTextView.setText("");//设置文本为空
                CameraEntity entity=new CameraEntity();
                entity.setId(Integer.parseInt(itemAtPosition.get("id")));
                entity.setCameraName(itemAtPosition.get("carmerName"));
                boolean find=isFind(selectMap,entity);
                if(!find){//不存在的情况下
                    selectMap.put(entity.getId(),entity);
                    initView();
                }
            }
        });
    }

    /**
     * 加载数据信息
     */
    private void initData(){
        ArrayList<Map<String, String>> data=new ArrayList<Map<String,String>>();
        HashMap<String,String> item;
        for(CameraEntity cameraEntity:list){
            item = new HashMap<>();
            item.put("id",cameraEntity.getId().toString());
            item.put("carmerName",cameraEntity.getCameraName());
            data.add(item);
        }
        SimpleAdapter adapter= new SimpleAdapter( MulListActivity.this, data,
                R.layout.search_result_item,
                new String[] { "id", "carmerName"},
                new int[] { R.id.id_TextView, R.id.cname_TextView} );
        auto_CompleteTextView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        auto_CompleteTextView.showDropDown();//显示下拉框数据信息
    }

    //判断集合中是否存在
    private boolean isFind(Map<Integer,CameraEntity> selectMap,CameraEntity cameraEntity){
        return selectMap.get(cameraEntity.getId())==null?false:true;
    }

    /**
     * 初始化数据信息
     */
    public void initView(){
        ListView listView = (ListView) this.findViewById(R.id.listView);
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
        for(Integer key:selectMap.keySet()){
            CameraEntity cameraEntity=selectMap.get(key);
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("id",key);
            if(cameraEntity.getCameraName().length()>22){
                item.put("carmerName",cameraEntity.getCameraName().substring(0,22));
            }else {
                item.put("carmerName", cameraEntity.getCameraName());
            }
            data.add(item);
        }
        //创建SimpleAdapter适配器将数据绑定到item显示控件上
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.camer_item,
                new String[]{"id", "carmerName"}, new int[]{R.id.carmer_id, R.id.carmer_name});
        //实现列表的显示
        listView.setAdapter(adapter);
        //条目点击事件
        listView.setOnItemClickListener(new MulListActivity.ItemClickListener());
    }

    //获取点击事件
    private final class ItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView listView = (ListView) parent;
            HashMap<String, Object> data = (HashMap<String, Object>) listView.getItemAtPosition(position);
            String carmer_id= data.get("id").toString();
            String carmerName= data.get("carmerName").toString();
            AlertDialog.Builder builder = new AlertDialog.Builder(MulListActivity.this);
            builder.setTitle("提示");
            builder.setMessage("您确定要删除"+carmerName+"的信息吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    CameraEntity obj = selectMap.remove(Integer.parseInt(carmer_id));
                    if(obj!=null){
                        initView();
                    }
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
     * 点击导航数据信息
     * @param view
     */
    public void doNavigate(View view) {
        if(mLoadingDialog!=null){
            mLoadingDialog.dismiss();
        }
        if(selectMap.isEmpty()){
            Toast.makeText(this,"请选择数据信息！",Toast.LENGTH_LONG).show();
        }else{
            Intent intent = new Intent(MulListActivity.this,MulMapActivity.class);
            List<Integer> ids=new ArrayList<>();
            for(Integer key:selectMap.keySet()){
                ids.add(key);
            }
            intent.putExtra("ids",JSONArray.toJSONString(ids));
            startActivity(intent);
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
}
