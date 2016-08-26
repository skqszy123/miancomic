package com.hiroshi.cimoc.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ZoomButtonsController;

import com.hiroshi.cimoc.R;

/**
 * Project：Cimoc-master
 * Author: sunkeqiang
 * Version: 1.0.0
 * Description：
 * Date：2016/8/26 17:24
 * Modification  History:
 * Why & What is modified:
 */
public class AYAboutWebView extends Activity{
    protected WebView mWebView;
    protected WebSettings webseting;
    protected String url;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aywebview);

        initWebView();


    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        mWebView = (WebView) findViewById(R.id.wv_main);
        // 设置滚动条样式，去掉滚动条白边可使滚动条不占位
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webseting = mWebView.getSettings();

        //设置加载进来的页面自适应手机屏幕
        webseting.setJavaScriptEnabled(true);
        //让webview读取网页设置的viewport
        //设置webview推荐使用的窗口
        webseting.setUseWideViewPort(true);
        //设置一个默认的viewport=800，如果网页自己没有设置viewport，就用800
        //设置webview加载的页面的模式
        webseting.setLoadWithOverviewMode(true);
        // 可以读取文件缓存(manifest生效)
        webseting.setAllowFileAccess(true);
        webseting.setSupportZoom(true);

        webseting.setBuiltInZoomControls(true);

        // int sysVersion = VERSION.SDK_INT;
        if (Build.VERSION.SDK_INT >= 11) {
            webseting.setDisplayZoomControls(false);
        } else {
            ZoomButtonsController zbc = new ZoomButtonsController(mWebView);
            zbc.getZoomControls().setVisibility(View.GONE);
        }

        url = "http://121.42.200.39/comic/index.html";


        mWebView.loadUrl(url);

        // 不弹出系统浏览器
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });



    }
}
