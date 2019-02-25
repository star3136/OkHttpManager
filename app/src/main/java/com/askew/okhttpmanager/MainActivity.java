package com.askew.okhttpmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.askew.net.OkException;
import com.askew.net.OkHttpManager;
import com.askew.net.callback.OkHttpCallback;
import com.askew.net.callback.OkHttpCallbackAdapter;
import com.askew.net.lifecycle.OkLifeCycles;
import com.askew.net.schedulers.OkSchedulers;
import com.uitips.UiTips;

public class MainActivity extends AppCompatActivity {
    ViewGroup rootView;
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootView = findViewById(R.id.root);
        tvResult = findViewById(R.id.tv_result);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_get:
                UiTips.with(this).loadingDialog().show();
                OkHttpManager.get("http://wanandroid.com/wxarticle/chapters/json")
                        .bindUntil(OkLifeCycles.OnDestroy(this))
                        .responseOn(OkSchedulers.main())
                        .call(new OkHttpCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                UiTips.with(MainActivity.this).loadingDialog().hide();
                                tvResult.setText(result);
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                UiTips.with(MainActivity.this).loadingDialog().hide();
                                tvResult.setText("code: " + code + ", message: " + msg);
                            }

                            @Override
                            public void onError(Exception e) {
                                UiTips.with(MainActivity.this).loadingDialog().hide();
                                tvResult.setText(e.getMessage());
                            }
                        });
                break;
            case R.id.btn_post:
                UiTips.with(this).loadingDialog().show();
                OkHttpManager.post("http://www.wanandroid.com/user/login")
                        .withParam("xxx", "xxx")
                        .withParam("xxx", "xxx")
                        .responseOn(OkSchedulers.main())
                        .call(new OkHttpCallbackAdapter<String>(){
                            @Override
                            public void onSuccess(String result) {
                                tvResult.setText(result);
                            }

                            @Override
                            public void onComplete() {
                                UiTips.with(MainActivity.this).loadingDialog().hide();
                            }
                        });
                break;
            case R.id.btn_get_sync:
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            final String result = OkHttpManager.get("http://wanandroid.com/wxarticle/chapters/json")
                                    .callSync(String.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText(result);
                                }
                            });
                        } catch (OkException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;
            case R.id.btn_post_sync:
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            final String result = OkHttpManager.post("http://www.wanandroid.com/user/login")
                                    .withParam("xxx", "xxx")
                                    .withParam("xxx", "xxx")
                                    .callSync(String.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvResult.setText(result);
                                }
                            });
                        } catch (OkException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;
        }
    }
}
