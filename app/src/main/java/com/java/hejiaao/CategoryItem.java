package com.java.hejiaao;

public class CategoryItem {
    public String title;
    public String url;
    CategoryItem(String title_, String url_) {
        title = title_;
        url = url_;
    }
    public int compareTo(CategoryItem other) {
        return this.url.compareTo(other.url);
    }
    public int hashCode() {
        return title.hashCode() ^ url.hashCode();
    }
    public boolean equals(CategoryItem other) {
        return this.url.equals(other.url);
    }
}