package com.java.hejiaao;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class NewsView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_view);
		Intent intent = getIntent();
		String url = intent.getStringExtra("url");
		WebView wv = (WebView)findViewById(R.id.content_view);
        WebSettings ws = wv.getSettings();
		ws.setUserAgentString("Mozilla/5.0 (Linux; Android 7.0; MI 5 Build/NRD90M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/53.0.2785.49 Mobile MQQBrowser/6.2 TBS/043128 Safari/537.36 MicroMessenger/6.5.7.1041 NetType/WIFI Language/zh_CN");
        ws.setJavaScriptEnabled(false);
        wv.loadUrl(url);
    }
}
