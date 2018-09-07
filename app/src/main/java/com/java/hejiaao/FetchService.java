package com.java.hejiaao;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Binder;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URL;
import java.net.URLConnection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashSet;
import java.util.ArrayList;

public class FetchService extends Service {
	private History history;
	public boolean isActive;


    public class LocalBinder extends Binder {
        FetchService getService() {
            return FetchService.this;
        }
    }
    private LocalBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    ArrayList<CategoryItem> shown = new ArrayList<>();

	ArrayList<ArrayAdapter> adapters = new ArrayList();

	Thread th;

    public FetchService() {
    	history = History.getInstance("qaq");
    	if (history.getCategory("shown_c").size() + history.getCategory("hidden_c").size() == 0) {
			history.addCategory("shown_c", "http://news.qq.com/newsgn/rss_newsgn.xml", "新闻频道");
			history.addCategory("shown_c", "http://ent.qq.com/movie/rss_movie.xml", "娱乐频道");
			history.addCategory("shown_c", "http://stock1.finance.qq.com/stock/dpfx/rss_dpfx.xml", "证券频道");
			history.addCategory("shown_c", "http://finance.qq.com/scroll/rss_scroll.xml", "财经频道");
			history.addCategory("shown_c", "http://tech.qq.com/web/rss_web.xml", "科技频道");
			history.addCategory("shown_c", "http://auto.qq.com/comment/zjpc/rss_zjpc.xml", "汽车频道");
			history.addCategory("shown_c", "http://sports.qq.com/rss_newssports.xml", "体育频道");
			history.addCategory("shown_c", "http://games.qq.com/ntgame/rss_ntgame.xml", "游戏频道");
			history.addCategory("shown_c", "http://edu.qq.com/gaokao/rss_gaokao.xml", "教育频道");
			history.addCategory("shown_c", "http://bb.qq.com/original/rss_original.xml", "视频频道");
			history.addCategory("shown_c", "http://book.qq.com/origin/rss_origin.xml", "读书频道");
			history.addCategory("shown_c", "http://lady.qq.com/qqstar/rss_qqstart.xml", "女性频道");
			history.addCategory("hidden_c", "http://baby.qq.com/bbs/rss_babybbs.xml", "育儿频道");
			history.addCategory("hidden_c", "http://astro.qq.com/12star/rss_12star.xml", "星座频道");
			history.addCategory("hidden_c", "http://xian.qq.com/xanews/rss_news.xml", "西安地方站");
			history.addCategory("hidden_c", "http://digi.qq.com/mobile/manufacturer/rss_manufacturer.xml", "手机频道");
			history.addCategory("hidden_c", "http://comic.qq.com/cosplay/rss_cosplay.xml", "动漫频道");
			history.addCategory("hidden_c", "http://luxury.qq.com/staff/rss_staff.xml", "时尚频道");
			history.addCategory("hidden_c", "http://joke.qq.com/jokeflash/rss_flash.xml", "笑话频道");
			history.addCategory("hidden_c", "http://kid.qq.com/youxi/rss_game.xml", "儿童频道");
			history.addCategory("hidden_c", "http://weather.qq.com/zixun/rss_fyzx.xml", "天气频道");
		}
    }

	public void update() {
    	this.shown = history.getCategory("shown_c");
		// Log.w("Fetchu", "Size " + this.adapters.size() + " " + this.shown.size());
		for (ArrayAdapter l : this.adapters) {
			l.clear();
			for (CategoryItem d : this.shown) {
				// Log.w("update", d.title);
				l.add(d);
			}
		}
}

    synchronized private void fetchCategory(String urlString) {
	}


	public void addUpdateList(ArrayAdapter l) {
		adapters.add(l);
	}

	@Override
	public void onDestroy() {
		this.isActive = false;
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
}
