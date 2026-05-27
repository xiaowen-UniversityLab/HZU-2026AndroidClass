package com.example.anew;
import java.util.List;

public class News {
    public int error_code;
    public String reason;
    public Result result;

    public static class Result { public List<Article> data; }

    public static class Article {
        public String title;
        public String author_name;
        public String date;
        public String url;
    }
}
