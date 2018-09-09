package com.java.hejiaao;

import android.content.Intent;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class RcmdGenerator {
    History history;
    RcmdGenerator() {
        history = History.getInstance("qaq");
    }

    ArrayList<FetchXML.DataItem> datas;
    MainActivity m_main;

    public void setArray(ArrayList e, MainActivity mmain) {
        this.datas = e;
        m_main = mmain;
    }

    public void updateList() {
        if (this.datas.size() > 0) {
            this.datas.remove(this.datas.size() - 1);
        }
        for (int i = 0; i < 5 && this.rcmdList.size() > 0; ++ i) {
            this.datas.add(rcmdList.get(0));
            rcmdList.remove(0);
        }
        if (this.rcmdList.size() > 0) {
            this.datas.add(new FetchXML.DataItem("加载中", "", ""));
        }
        Log.i("rcmd", "upd new sz = " + this.rcmdList.size());
        Intent intent = new Intent("update_rcmd");
        m_main.sendBroadcast(intent);
    }

    public double distance(String a, String b) {
        int n = a.length();
        int m = b.length();
        int [][] f = new int[n + 1][];
        f[0] = new int[m + 1];
        for (int i = 0; i < n; ++ i) {
            f[i + 1] = new int[m + 1];
            for (int j = 0; j < m; ++ j) {
                if (a.charAt(i) == b.charAt(j)) {
                    f[i + 1][j + 1] = f[i][j] + 1;
                } else if (f[i + 1][j] > f[i][j + 1]) {
                    f[i + 1][j + 1] = f[i + 1][j];
                } else {
                    f[i + 1][j + 1] = f[i][j + 1];
                }
            }
        }
        double ans = f[n][m];
        ans = ans * ans / m / n;
        return ans;
    }

    private ArrayList<FetchXML.DataItem> rcmdList = new ArrayList<FetchXML.DataItem>();

    private void threadMain() {
        Log.i("rcmd", "started");
        ArrayList<CategoryItem> categories = history.getCategory("shown_c");
        ArrayList<FetchXML.DataItem> caches = history.getCaches();
        ArrayList<String> likeds = history.getStars();
        for (CategoryItem ci : categories) {
            try {
                URL url = new URL(ci.url);
                URLConnection tc = url.openConnection();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document dom = builder.parse(tc.getInputStream());
                NodeList nl = dom.getElementsByTagName("item");
                Log.i("rcmd", "read done " + ci.title + " " + nl.getLength());
				for (int i = 0; i < nl.getLength(); ++i) {
                    Element it = (Element) nl.item(i);
                    String title = it.getElementsByTagName("title").item(0).getTextContent();
                    String uurl = it.getElementsByTagName("link").item(0).getTextContent();
                    String content = it.getElementsByTagName("description").item(0).getTextContent();
                    FetchXML.DataItem di = (new FetchXML.DataItem(title, uurl, content));
                    di.weight = (new Random()).nextFloat();
                    for (FetchXML.DataItem c : caches) {
                        double vc = 0;
                        if (history.has("like", c.url)) {
                            vc += 10;
                        }
                        if (history.has("history", c.url)) {
                            vc += 1;
                        }
                        if (vc > 0) {
                            di.weight += distance(di.title, c.title) * 10 * vc;
                            di.weight += distance(di.title, c.content) * 1 * vc;
                            di.weight += distance(di.content, c.title) * 1 * vc;
                            di.weight += distance(di.content, c.content) * 0.1 * vc;
                        }
                    }
                    rcmdList.add(di);
                }
            } catch (Exception e) {
				Log.e("rcmd", e.getMessage());
            }
            Collections.sort(rcmdList);
            this.updateList();
        }
        Log.i("rcmd", "list generated " + rcmdList.size());
    }

    public void generate() {
        Thread th = new Thread() {
            @Override
            public void run() {
                threadMain();
            }
        };
        th.start();
    }
}
