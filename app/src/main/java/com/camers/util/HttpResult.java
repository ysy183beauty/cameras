package com.camers.util;

import android.os.Handler;
import android.os.Message;
import java.util.HashMap;

public class HttpResult {
    private  String url;
    private  HashMap<String,String> paramters;
    private Handler mHandler;
    public  HttpResult(String url, HashMap<String,String> map,Handler mHandler){
        this.url=url;
        this.paramters=map;
        this.mHandler=mHandler;
    }

    public void getResult(){
        try {
            Message message = new Message();
            String compeletedURL = HttpUtil.getURLWithParams(url,paramters); //originAddress请求地址， params请求参数
            HttpUtil.sendHttpRequest(compeletedURL, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {		//请求回调函数，response是响应的数据
                    message.obj = response;
                    mHandler.sendMessage(message);    //给Handlder传递数据
                }
                @Override
                public void onError(Exception e) {
                    message.obj = e.toString();
                    mHandler.sendMessage(message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
