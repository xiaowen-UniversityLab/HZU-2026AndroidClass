package com.example.newslist.model.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.newslist.model.bean.CollectNews;

import java.util.List;

@Dao
public interface NewsDao {

    // 插入收藏
    @Insert
    void insertNews(CollectNews news);

    // 删除收藏
    @Delete
    void deleteNews(CollectNews news);

    // 查询所有收藏
    @Query("SELECT * FROM collection_news ORDER BY id DESC")
    LiveData<List<CollectNews>> getAllCollectionNews();


}
