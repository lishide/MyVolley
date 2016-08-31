package com.eric.volley.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.eric.volley.network.VolleyListenerInterface;
import com.eric.volley.network.VolleyRequestUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        volleyGetList();
    }

    private void volleyGetList() {
        String url = "";
        VolleyRequestUtil.RequestGet(this, url, "listGet",
                new VolleyListenerInterface(this, VolleyListenerInterface.mListener, VolleyListenerInterface.mErrorListener) {
                    // Volley请求成功时调用的函数
                    @Override
                    public void onMySuccess(String response) {

                    }

                    // Volley请求失败时调用的函数
                    @Override
                    public void onMyError(VolleyError error) {
                    }
                }, false);
    }
}
