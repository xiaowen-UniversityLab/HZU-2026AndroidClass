package com.example.newslist.model.bean;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.newslist.model.bean.NewsBean.NewsListBean;

@Entity(tableName = "collection_news")
public class CollectNews {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String ctime;
    private String title;
    private String source;

    public CollectNews() {}

    public CollectNews(String ctime, String title, String source) {
        this.ctime = ctime;
        this.title = title;
        this.source = source;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public NewsListBean toNewsListBean() {
        return new NewsListBean(this.ctime, this.title, this.source);
    }
}
