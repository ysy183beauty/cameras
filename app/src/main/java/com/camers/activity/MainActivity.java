package com.camers.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.camers.R;

/**
 * 主页面
 */
public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void login(View view) {
        startActivity(new Intent(this,LoginActivity.class));
    }

    public void exit(View view) {
        finish();
    }
}
