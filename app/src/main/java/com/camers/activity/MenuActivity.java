package com.camers.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.camers.R;
import com.camers.util.SharedPreferencesUtils;

/**
 * 菜单页面信息
 */
public class MenuActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
    }
    /**
     * 返回登录页面
     */
    public void returnLogin(View view) {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this, "setting");
        //取消保留的数据信息
        helper.putValues(new SharedPreferencesUtils.ContentValue("autoLogin", false));
        startActivity(new Intent(this,LoginActivity.class));
        finish();//关闭页面
    }
    public void addInfo(View view){
        startActivity(new Intent(this,AddActivity.class));
        finish();//关闭页面
    }
    public void eidtInfo(View view){
        startActivity(new Intent(this,ListActivity.class));
        finish();//关闭页面
    }

    public void singleMap(View view){
        startActivity(new Intent(this,ListSingleActivity.class));
        finish();//关闭页面
    }

    public void mulMap(View view){
        startActivity(new Intent(this,MulListActivity.class));
        finish();//关闭页面
    }

}
