package com.java.hejiaao;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;

public class NewsView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		final History history = History.getInstance(getApplicationContext().getFilesDir().getAbsolutePath());

		Intent i = new Intent("update_like");
		sendBroadcast(i);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_view);
		Intent intent = getIntent();
		final String url = intent.getStringExtra("url");

		ImageButton likeBtn = ((ImageButton)findViewById(R.id.likeBtn));
		if (history.has("like", url)) {
			likeBtn.setImageResource(android.R.drawable.btn_star_big_on);
		}
		likeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (history.has("like", url)) {
					((ImageButton)v).setImageResource(android.R.drawable.btn_star_big_off);
					history.del("like", url);
				} else {
					((ImageButton)v).setImageResource(android.R.drawable.btn_star_big_on);
					history.add("like", url);
				}
				Intent i = new Intent("update_like");
				sendBroadcast(i);
			}
		});

		ImageButton shareBtn = ((ImageButton)findViewById(R.id.sh))

		Log.i("view", "btn ready");

		WebView wv = (WebView)findViewById(R.id.content_view);
		WebViewClient wc = new WebViewClient() {
		    @Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		};
		Log.i("view", "wc ready");
        WebSettings ws = wv.getSettings();
		ws.setUserAgentString("Mozilla/5.0 (Linux; Android 7.0; MI 5 Build/NRD90M; wv) " +
				"AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/53.0.2785.49 " +
				"Mobile MQQBrowser/6.2 TBS/043128 Safari/537.36 MicroMessenger/6.5.7.1041 " +
				"NetType/WIFI Language/zh_CN");
        ws.setJavaScriptEnabled(true);
		Log.i("view", "ws ready");
        wv.setWebViewClient(wc);
        wv.loadUrl(url);
		Log.i("view", "load ready");
    }
}
