package com.fdse.scontroller.fragment.devicefragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fdse.scontroller.R;

/**
 * <pre>
 *     author : shenbiao
 *     e-mail : 1105125966@qq.com
 *     time   : 2018/07/30
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class DeviceWebFragment1 extends Fragment {

    private WebView webView;
    private String mstrLoginUrl = "http://10.131.253.117:8123/floorplan";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_device_web, container, false);

        webView = (WebView) view.findViewById(R.id.wv_home_device);

        webView.getSettings().setJavaScriptEnabled(true);
        //支持屏幕缩放
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        //不显示webview缩放按钮
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);

//todo 暂时先把下面的注销掉，免得每次运行的时候都一直在请求hass服务，影响效率
        webView.loadUrl(mstrLoginUrl);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

}