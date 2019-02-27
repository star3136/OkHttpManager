# OkHttpManager

> * 介绍
> * 配置
> * 常用方法
> * 拦截处理
> * 自定义线程调度器

### 介绍
OkHttpManager是一个OkHttp的封装类库，旨在方便OkHttp的使用。支持的功能如下：
* GET、POST请求
* 上传、下载
* 异步、同步
* 断点续传
* 解决OkHttp默认只能添加全局拦截器的问题，添加针对单个请求的拦截器
* 随意切换请求线程和回调线程，可以轻松通过定制线程池来控制请求的并发数，比如控制同时最多只有3个线程下载
* 可以绑定Activity或Fragment生命周期，在生命周期到时，自动取消网络请求


### 配置
```java
 //创建OkHttpClient
OkHttpClient.Builder builder = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS);

OkHttpManager.newSettinsBuilder()
        .context(appContext)   //设置context，必须设置
        .okHttpClient(builder.build()) //设置OkHttpClient
        .withCommonHttpHeader("Content-Type", "application/json")  //设置全局header
        .withCommonHttpHeader("Cookie", "XXX") //这样也可以设置Cookie/Session
        .withCommonHttpParam("commonKey1", "commonValue1") //设置公共参数
        .build()
        .init();




```

### 常用方法
#### 普遍遵循的用法
```java
//指定线程
IDisposable disposable = OkHttpManager.xxxx("http://news-at.zhihu.com/api/4/news/latest")  //设置url，xxxx表示post、get、download、upload任意一个
            .withHeader("Agent", "XYZ")  //设置本次请求的header
            .withHeader("H2", "H2")
            .withParam("key1", "value1") //设置本次请求的参数
            .withParam("key2", "value2")
            .saveToFile(PATH_TO_YOUER_FILE)  //本次请求的响应存入指定的文件，可实现在请求的同时下载
            .tag(...)  //设置tag，用于cancel
            .requestOn(OkSchedulers.io())    //指定本次请求的工作线程，如果不指定并且没有使用callSync()方法发起请求，则默认使用OkHttp自己的工作线程
            .responseOn(OkSchedulers.main())  //指定本次请求的回调线程，如果不指定则和工作线程在同一个线程
            .bindUntil(OkLifeCycles.OnDestroy(...))   //绑定生命周期，支持OnPause、OnStop、OnDestroy，参数可以是Activity或者Fragment
            .withHttpProcessor(...)  //请求之前和响应之后的拦截，后面续述
            .call(new OkHttpCallback<byte[]>() { //设置回调，以byte[]类型作为响应结果
                @Override
                public void onResponse(Response response) {
                    ...
                }

                @Override
                public void onSuccess(byte[] result) {
                    ...
                }

                @Override
                public void onFail(int code, String msg) {
                    ...
                }
                @Override
                public void onError(String msg) {
                    ...
                }

                @Override
                public void onCancel() {
                    ...
                }

                @Override
                public void onComplete() {
                    ...
                }
            });


//设置回调时可以使用以下的范型参数
//实体类、byte[]、String、InputStream、File、JSONObject、JSONArray
//比如以上的请求，如果想接受实体类型
OkHttpManager.xxxx("http://news-at.zhihu.com/api/4/news/latest")  //设置url，xxxx表示post、get、download、upload任意一个
            ...    //这部分和上面的请求一样
            .call(new OkHttpCallback<News>() { //这里修改一下范型参数类型
                @Override
                public void onSuccess(News result) {
                    ...
                }

                @Override
                public void onFail(int code, String msg) {
                    ...
                }
                @Override
                public void onError(String msg) {
                    ...
                }
            });

//同步请求，如果使用了requestOn()、responseOn()、bindUntil()中的任一方法，不会有callSync()方法
//直接返回实体的方式
News news = OkHttpManager.xxxx("http://news-at.zhihu.com/api/4/news/latest")  //设置url，xxxx表示post、get、download、upload任意一个
            ....  //中间的方法调用同异步请求
            .callSync(News.class);  //以News类型作为响应结果, 这里同样可以传入实体类、byte[]、String、InputStream、File、JSONObject、JSONArray，不再赘述

//以回调的方式使用同步请求
//这里返回的disposable实际上用不到，因为是同步调用，返回disposable时，请求已经结束了，可以用其他方式取消请求(OkHttpManager.cancelXXX()、触发绑定的生命期自动取消请求)
IDisposable disposable = OkHttpManager.xxxx("http://news-at.zhihu.com/api/4/news/latest")  //设置url，xxxx表示post、get、download、upload任意一个
            ....  //中间的方法调用同异步请求
            .callSync(new OkHttpCallback<News>() {
                @Override
                public void onSuccess(News result) {
                    ...
                }

                @Override
                public void onFail(int code, String msg) {
                    ...
                }
                @Override
                public void onError(String msg) {
                    ...
                }
            });


//取消请求
OkHttpManager.cancel(tag);  //通过tag取消指定的请求
OkHttpManager.cancelAll(); //取消所有的请求
disposable.cancel();   //通过请求返回的disposable取消，注意同步请求不要这样用
disposable.isCanceled();  //请求是否已经取消

```
#### POST方法
以下针对POST方法的一些特别的地方进行说明，没有提到的部分，和**普遍遵循的用法**一样。
```java
//异步请求
//以表单方式POST
OkHttpManager.post("http://news-at.zhihu.com/api/4/news/latest")  //设置url
            .withParam("key1", "value1") //设置本次请求的参数,withParam会使用表单方式POST
            .withParam("key2", "value2")
            .call(...);

//POST一个字符串，如json
OkHttpManager.post("http://news-at.zhihu.com/api/4/news/latest", body)  //body是字符串，可以是json
                .call(...);
```
**注意:** 同时设置withParam()和body时，以body优先。

#### GET方法
和**普遍遵循的用法**一样。

#### 下载
```java
OkHttpManager.download(url, PATH_TO_SAVE, enablePartial)  //设置下载文件的url和存到本地的路径，PATH_TO_SAVE的效果同saveToFile(xxx)，enablePartial为true时启用断点续传功能
            ....  //设置参数
            .call(new OkHttpProgressCallback<File>() {  //设置下载进度监听，如果不需要监听进度直接设置OkHttpCallback即可
                @Override
                public void onProgress(int progress, long current, long total) { //进度 0-100
                    Log.d(TAG, progress + "");
                }

                @Override
                public void onSuccess(File result) {
                    ...
                }

                @Override
                public void onFail(int code, String msg) {
                    ...
                }
                @Override
                public void onError(String msg) {
                    ...
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "下载被取消");
                }
            });

//监听进度的回调,范型参数同样支持 实体类、byte[]、String、InputStream、File，实体类估计用不到，不过提供这个功能

```

其实前面的GET、POST就可以实现下载，这里的下载对比前面的，主要提供了下载进度的监听，还有可读性强，如果本次请求就是为了下载，那么请使用这个方法！

#### 上传
```java
OkHttpManager.upload(url, PATH_TO_YOUR_FILE)  //设置上传的url，文件路径
            .... //设置参数
            .call(new OkHttpProgressCallback<String>() {
                @Override
                public void onProgress(int progress, long current, long total) { //上传进度 0-100
                    ...
                }

                @Override
                public void onSuccess(News result) {
                    ...
                }

                @Override
                public void onFail(int code, String msg) {
                    ...
                }
                @Override
                public void onError(String msg) {
                    ...
                }

                @Override
                public void onCancel() {
                    ...
                }
            });
```

#### 拦截处理
OkHttpManager支持对每个请求、响应单独设置拦截器。
OkHttp自带拦截器，那么为什么还需要自己定义拦截器呢？
OkHttp自带的拦截器是全局的，不能针对每一个请求，比如我们有的请求需要加密，有的不需要，这样使用全局的拦截器容易乱，不好管理。

```java
OkHttpManager.xxxx(...)
            .withHttpProcessor(new IHttpProcessor() {
                @Override
                public Request preRequest(Request request) {
                    return request.newBuilder().post(new DesRequestBody(request.body())).build();  //设置Des加密的RequestBody
                }

                @Override
                public Response postResponse(Response response) {
                    return response.newBuilder().body(new DesResponseBody(response.body())).build(); //设置Des解密的ResponseBody
                }
            })

```

### 使用方法
项目根目录build.gradle添加如下:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

添加依赖:
````
dependencies {
    implementation 'com.github.star3136:OkHttpManager:version'
}
```
