package com.healthmanager.healthmanager.util;


import android.app.Activity;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class HttpUtil {

    private static DefaultHttpClient httpClient = new DefaultHttpClient();

    public static Object lock=new Object();

    public static JSONObject postJson(String url,JSONObject requestParam){
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "application/json; charset=utf-8");
            if(requestParam!=null) {
                StringEntity stringEntity = new StringEntity(requestParam.toString(),"UTF-8");
                stringEntity.setContentEncoding("UTF-8");
                httpPost.setEntity(stringEntity);
            }
            HttpResponse httpResponse;
            InputStream inputStream;
            String result;
            synchronized (lock) {
                httpResponse = httpClient.execute(httpPost);
                inputStream = httpResponse.getEntity().getContent();
                result = IOUtils.toString(inputStream);
            }
            Log.d("http","result:"+result);
            return new JSONObject(result);
        }
        catch (Exception e){
            Log.e("http","post fail",e);
            Map<String,Object> map=new HashMap<>();
            map.put("status",-1);
            map.put("data",null);
            map.put("message","请求失败");
            return new JSONObject(map);
        }
    }

    public static void postJsonAsync(final String url, final JSONObject requestParam, final HttpUtil.CallBack callBack, final Activity activity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final JSONObject jsonObject = postJson(url, requestParam);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callBack.noThrowCall(jsonObject);
                    }
                });
            }
        }).start();
    }

    public static void clearCookie(){
        httpClient.getCookieStore().clear();
    }

    public static abstract class CallBack{

        public abstract void call(JSONObject result) throws Exception;

        private void noThrowCall(JSONObject result){
            try{
                call(result);
            }
            catch (Exception e){
                Log.e("json","analyse",e);
            }
        }
    }
}
