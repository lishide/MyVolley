# Android：Volley 的使用及其工具类的封装
	August 26, 2016 1:10 PM Power By lishide
## 一. Volley 简介

Volley 的中文翻译为“齐射、并发”，是在 2013 年的 Google 大会上发布的一款 Android 平台网络通信库，具有网络请求的处理、小图片的异步加载和缓存等功能，能够帮助 Android App 更方便地执行网络操作，而且更快速高效。

在Google IO 的演讲上，其配图是一幅发射火弓箭的图，有点类似流星。这表示，Volley 特别适合数据量不大但是通信频繁的场景。见下图：

![](https://github.com/lishide/MyVolley/raw/master/art/img_volley_inc.png "Volley") 

### Volley 有如下的优点：
 - 自动调度网络请求；
 - 高并发网络连接；
 - 通过标准的 HTTP cache coherence（高速缓存一致性）缓存磁盘和内存透明的响应；
 - 支持指定请求的优先级；
 - 网络请求 cancel 机制。我们可以取消单个请求，或者指定取消请求队列中的一个区域；
 - 框架容易被定制，例如，定制重试或者回退功能；
 - 包含了调试与追踪工具；

> * Volley 不适合用来下载大的数据文件。因为 Volley 会保持在解析的过程中所有的响应。对于下载大量的数据操作，请考虑使用 DownloadManager 。
> * 在 volley 推出之前我们一般会选择比较成熟的第三方网络通信库，如：android-async-http、retrofit、okhttp 等。他们各有优劣，可有所斟酌地选择选择更适合项目的类库。
> * 附：
Volley 的 GitHub 地址：https://github.com/mcxiaoke/android-volley

## 二、使用

 - Eclipse

        把 Volley 添加到项目中最简便的方法是 Clone 仓库，然后把它设置为一个 library project。
        （1）clone 代码：
        git clone https://android.googlesource.com/platform/frameworks/volley
        
        （2）将代码编译成 jar 包：
        android update project -p . ant jar
        
        如无意外，将获得 volley.jar 包。
        
        （3）添加 volley.jar 到你的项目中

 - AndroidStudio using Gradle build add dependent (recommended)

        compile 'com.mcxiaoke.volley:library:1.0.19'

----------

#### 使用 Volley 框架实现网络数据请求主要有以下三个步骤：
 1. 创建 RequestQueue 对象，定义网络请求队列；
 2. 创建 XXXRequest 对象( XXX 代表 String，JSON，Image 等等)，定义网络数据请求的详细过程；
 3. 把 XXXRequest 对象添加到 RequestQueue 中，开始执行网络请求。

### 2.1 创建 RequestQueue 对象

一般而言，网络请求队列都是整个 App 内使用的全局性对象，因此最好写入 Application 类中：

    public class MyApplication extends Application{
        // 建立请求队列
        public static RequestQueue queue;

       @Override
       public void onCreate() {
           super.onCreate();
           queue = Volley.newRequestQueue(getApplicationContext());
       }
    
       public static RequestQueue getHttpQueue() {
          return queue;
       }
    }

修改 AndroidManifest.xml 文件，使 App 的 Application 对象为我们刚定义的 MyApplication，并添加 INTERNET 权限：

    <uses-permission android:name="android.permission.INTERNET" />
    <application
        android:name=".MyApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/AppTheme" >
    </application>

### 2.2 创建 XXXRequest 对象并添加到请求队列中

Volley 提供了`JsonObjectRequest`、`JsonArrayRequest`、`StringRequest`等 Request 形式

### 2.3 把 XXXRequest 对象添加到 RequestQueue 中，开始执行网络请求。

        // 设置该请求的标签
        request.setTag("listGet");
        
        // 将请求添加到队列中
        MyApplication.getHttpQueue().add(request);

### 2.4 关闭请求

#### 关闭特定标签的网络请求：

        // 网络请求标签为"listGet"
        public void onStop() {
            super.onStop();
            MyApplication.getHttpQueues.cancelAll("listGet");
        }

#### 取消这个队列里的所有请求：

在 activity 的 onStop() 方法里面，取消所有的包含这个 tag 的请求任务。
        
        @Override  
        protected void onStop() {  
            super.onStop();  
            mRequestQueue.cancelAll(this);  
        }

对 Volley 的 GET 和 POST 请求进行了封装，见 `VolleyRequestUtil.java` 和 `VolleyListenerInterface.java` 。你在使用过程中也可以添加相应的参数，完成自己所要实现的功能。

## 三、VolleyRequestUtil 的使用

 - **用 GET 方式请求网络资源：**

        VolleyRequestUtil.RequestGet(this, url, "tag", 
            new VolleyListenerInterface(this, VolleyListenerInterface.mListener,
                    VolleyListenerInterface.mErrorListener) {
            // Volley请求成功时调用的函数
            @Override
            public void onMySuccess(String result) {
                Toast.makeText(this, s, Toast.LENGTH_LONG).show();
            }
        
            // Volley请求失败时调用的函数
            @Override
            public void onMyError(VolleyError error) {
                // ...
            }
        });
        
 - **用 POST 方式请求网络资源：**

        VolleyRequestUtil.RequestPOST(this, url, "tag", 
            new VolleyListenerInterface(this, VolleyListenerInterface.mListener,
                    VolleyListenerInterface.mErrorListener) {
            // Volley请求成功时调用的函数
            @Override
            public void onMySuccess(String result) {
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
            }
        
            // Volley请求失败时调用的函数
            @Override
            public void onMyError(VolleyError error) {
                // ...
            }
        });

## 四、PostUploadRequest 的使用

用于上传文件的框架，封装于 Volley。

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
                    updatePic();
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
                        //map.put("token", "");
                    return map;
                }
            });
        }
        
        private void updatePic() {
            Map<String, String> params = new HashMap<>();
            params.put("id", "1");
            params.put("pic", cutnameString);
            VolleyRequestUtil.RequestPost(this, url, "tag", params,
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

**Volley 也提供了图片的缓存和优化，（ `com.android.volley.toolbox.NetworkImageView`） 自定义图片控件，本人在开发中未使用。该 Volley 的封装中，暂未考虑到图片和数据缓存。有一些地方封装得仍不够抽象，有待完善。非常欢迎各位能提出修改建议，一起进步！**
