package com.camers.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.camers.R;
import com.camers.entity.DistanceEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MulMapActivity extends Activity {
    private ArrayList<DistanceEntity> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mulmap);
        list=(ArrayList<DistanceEntity>) getIntent().getSerializableExtra("listobj");
        initView();
    }

    /**
     * 初始化数据信息
     */
    public void initView(){
        ListView listView = (ListView) this.findViewById(R.id.listView);
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
        for(DistanceEntity entity:list){
            HashMap<String, Object> item = new HashMap<String, Object>();
            if(entity.getCameraName().length()>=19){
                item.put("carmerName",entity.getCameraName().substring(0,18));
            }else {
                item.put("carmerName", entity.getCameraName());
            }
            item.put("distance",entity.getDistance()+"千米");
            data.add(item);
        }
        //创建SimpleAdapter适配器将数据绑定到item显示控件上
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.camer_item_location,
                new String[]{"carmerName", "distance"}, new int[]{R.id.carmer_name, R.id.carmer_location});
        //实现列表的显示
        listView.setAdapter(adapter);
    }

    /**
     * 返回菜单页面信息
     */
    public void doReturn(View view) {
        startActivity(new Intent(this,MulListActivity.class));
        finish();//关闭页面
    }
}