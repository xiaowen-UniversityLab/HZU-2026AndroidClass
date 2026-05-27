package com.example.news3;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorites")
public class FavoriteArticle {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String author;
    public String date;
    public long timestamp;

    public FavoriteArticle(String title, String author, String date, long timestamp) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.timestamp = timestamp;
    }
}
