package com.eric.volley.network;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.eric.volley.application.MyApplication;

import java.util.Map;

public class VolleyRequestUtil {

    public static StringRequest stringRequest;
    public static Context context;
    public static int timeOut = 5000;

    /*
    * 获取GET请求内容
    * 参数：
    * context：当前上下文；
    * url：请求的url地址；
    * tag：当前请求的标签；
    * volleyListenerInterface：VolleyListenerInterface接口；
    * timeOutDefaultFlg：是否使用Volley默认连接超时；
    * */
    public static void RequestGet(Context context, String url, String tag, VolleyListenerInterface volleyListenerInterface, boolean timeOutDefaultFlg) {
        // 清除请求队列中的tag标记请求
        MyApplication.getQueue().cancelAll(tag);
        // 创建当前的请求，获取字符串内容
        stringRequest = new StringRequest(Request.Method.GET, url, volleyListenerInterface.responseListener(), volleyListenerInterface.errorListener());
        // 为当前请求添加标记
        stringRequest.setTag(tag);
        // 默认超时时间以及重连次数
        if (timeOutDefaultFlg) {
            timeOut = DefaultRetryPolicy.DEFAULT_TIMEOUT_MS;
        }
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(timeOut, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // 将当前请求添加到请求队列中
        MyApplication.getQueue().add(stringRequest);
        // 重启当前请求队列
        MyApplication.getQueue().start();
    }

    /*
    * 获取POST请求内容（请求的代码为Map）
    * 参数：
    * context：当前上下文；
    * url：请求的url地址；
    * tag：当前请求的标签；
    * params：POST请求内容；
    * volleyListenerInterface：VolleyListenerInterface接口；
    * */
    public static void RequestPost(Context context, String url, String tag, final Map<String, String> params, VolleyListenerInterface volleyListenerInterface) {
        // 清除请求队列中的tag标记请求
        MyApplication.getQueue().cancelAll(tag);
        // 创建当前的POST请求，并将请求内容写入Map中
        stringRequest = new StringRequest(Request.Method.POST, url, volleyListenerInterface.responseListener(), volleyListenerInterface.errorListener()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        // 为当前请求添加标记
        stringRequest.setTag(tag);
        // 将当前请求添加到请求队列中
        MyApplication.getQueue().add(stringRequest);
        // 重启当前请求队列
        MyApplication.getQueue().start();
    }
}
