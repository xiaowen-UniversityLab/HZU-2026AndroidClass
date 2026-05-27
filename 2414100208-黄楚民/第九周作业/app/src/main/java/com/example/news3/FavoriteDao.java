package com.example.news3;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FavoriteDao {

    @Insert
    void insert(FavoriteArticle article);

    @Delete
    void delete(FavoriteArticle article);

    @Query("SELECT * FROM favorites ORDER BY timestamp DESC")
    LiveData<List<FavoriteArticle>> getAll();

    @Query("SELECT * FROM favorites WHERE title = :title LIMIT 1")
    FavoriteArticle findByTitle(String title);
}
