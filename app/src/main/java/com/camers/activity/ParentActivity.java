package com.camers.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class ParentActivity extends Activity {
    public void showCoverDialog(int type,String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(ParentActivity.this);
        builder.setTitle("提示");
        builder.setMessage("已经存在了今天的数据，确定覆盖吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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
