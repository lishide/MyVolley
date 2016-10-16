package com.eric.volley.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.eric.volley.application.MyApplication;
import com.eric.volley.network.PostUploadRequest;
import com.eric.volley.network.VolleyListenerInterface;
import com.eric.volley.network.VolleyRequestUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private String cutnameString;
    private String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        volleyGetList();
    }

    private void volleyGetList() {
        String url = "";
        VolleyRequestUtil.RequestGet(this, url, "listGet",
                new VolleyListenerInterface(this, VolleyListenerInterface.mListener,
                        VolleyListenerInterface.mErrorListener) {
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

    /**
     * 上传文件分两步：
     * 1.调用此工具类，将文件传输到服务器（从本地选择文件的过程未列出）
     * 2.调用修改文件名的接口，修改数据库中相应的字段，完成上传文件操作
     */
    private void uploadFile() {
        String upLoadServerUri = "";
        HashMap<String, String[]> map = new HashMap<>();
        map.put("uploadedfile", new String[]{filename, cutnameString});
        MyApplication.getQueue().add(new PostUploadRequest(upLoadServerUri,
                map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                updateShopPic();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(mContext, "文件上传失败", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
//                    map.put("token", "");
                return map;
            }
        });
    }

    private void updateShopPic() {
        Map<String, String> params = new HashMap<>();
        params.put("id", "1");
        params.put("pic", cutnameString);
        VolleyRequestUtil.RequestPost(this, "updateshoppic.php", "updateShopPic", params,
                new VolleyListenerInterface(this, VolleyListenerInterface.mListener,
                        VolleyListenerInterface.mErrorListener) {
                    // Volley请求成功时调用的函数
                    @Override
                    public void onMySuccess(String response) {

                    }

                    // Volley请求失败时调用的函数
                    @Override
                    public void onMyError(VolleyError error) {

                    }
                });
    }
}
