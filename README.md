# Android：Volley的使用及其工具类的封装
	August 26, 2016 1:10 PM Power By Ericlsd
## 一. Volley简介
    Volley的中文翻译为“齐射、并发”，是在2013年的Google大会上发布的一款Android平台网络通信库，具有网络请求的处理、小图片的异步加载和缓存等功能，能够帮助 Android APP更方便地执行网络操作，而且更快速高效。
    在Google IO的演讲上，其配图是一幅发射火弓箭的图，有点类似流星。这表示，Volley特别适合数据量不大但是通信频繁的场景。见下图：
![](https://github.com/Ericlsd/MyVolley/raw/master/art/img_volley_inc.png "Volley") 
###Volley 有如下的优点：
 - 自动调度网络请求；
 - 高并发网络连接；
 - 通过标准的 HTTP cache coherence（高速缓存一致性）缓存磁盘和内存透明的响应；
 - 支持指定请求的优先级；
 - 网络请求cancel机制。我们可以取消单个请求，或者指定取消请求队列中的一个区域；
 - 框架容易被定制，例如，定制重试或者回退功能；
 - 包含了调试与追踪工具；

> Volley 不适合用来下载大的数据文件。因为 Volley 会保持在解析的过程中所有的响应。对于下载大量的数据操作，请考虑使用DownloadManager。
> 在volley推出之前我们一般会选择比较成熟的第三方网络通信库，如：android-async-http、retrofit、okhttp等。他们各有优劣，可有所斟酌地选择选择更适合项目的类库。
> 附：
Volley的github地址：https://github.com/mcxiaoke/android-volley



## 二、使用

 - Eclipse

        把 Volley 添加到项目中最简便的方法是 Clone 仓库，然后把它设置为一个 library project。
        （1）clone代码：
        git clone https://android.googlesource.com/platform/frameworks/volley
        
        （2）将代码编译成jar包：
        android update project -p . ant jar
        
        如无意外，将获得volley.jar包。
        
        （3）添加volley.jar到你的项目中

 - AndroidStudio using Gradle build add dependent (recommended)

        compile 'com.mcxiaoke.volley:library:1.0.19'


----------

####使用Volley框架实现网络数据请求主要有以下三个步骤：
 1. 创建RequestQueue对象，定义网络请求队列；
 2. 创建XXXRequest对象(XXX代表String,JSON,Image等等)，定义网络数据请求的详细过程；
 3. 把XXXRequest对象添加到RequestQueue中，开始执行网络请求。

###创建RequestQueue对象

一般而言，网络请求队列都是整个APP内使用的全局性对象，因此最好写入Application类中：

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
修改AndroidManifest.xml文件，使APP的Application对象为我们刚定义的MyApplication，并添加INTERNET权限：

    <uses-permission android:name="android.permission.INTERNET" />
    <application
        android:name=".MyApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/AppTheme" >
    </application>
###创建XXXRequest对象并添加到请求队列中
Volley提供了`JsonObjectRequest`、`JsonArrayRequest`、`StringRequest`等Request形式
###把XXXRequest对象添加到RequestQueue中，开始执行网络请求。

        // 设置该请求的标签
        request.setTag("listGet");
        
        // 将请求添加到队列中
        MyApplication.getHttpQueue().add(request);
###关闭请求

####关闭特定标签的网络请求：

        // 网络请求标签为"listGet"
        public void onStop() {
            super.onStop();
            MyApplication.getHttpQueues.cancelAll("listGet");
        }
####取消这个队列里的所有请求：

        在activity的onStop()方法里面，取消所有的包含这个tag的请求任务。
        
        @Override  
        protected void onStop() {  
            super.onStop();  
            mRequestQueue.cancelAll(this);  
        }
对Volley的GET和POST请求进行了封装，见 `VolleyRequestUtil.java` 和 `VolleyListenerInterface.java` 。你在使用过程中也可以添加相应的参数，完成自己所要实现的功能。

## 三、VolleyRequestUtil的使用

**用GET方式请求网络资源：**

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
        
**用POST方式请求网络资源：**

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

Volley也提供了图片的缓存和优化，（ `com.android.volley.toolbox.NetworkImageView`） 自定义图片控件，本人在开发中未使用。该Volley的封装中，暂未考虑到图片和数据缓存。有一些地方封装得仍不够抽象，有待完善。非常欢迎各位能提出修改建议，一起进步！




