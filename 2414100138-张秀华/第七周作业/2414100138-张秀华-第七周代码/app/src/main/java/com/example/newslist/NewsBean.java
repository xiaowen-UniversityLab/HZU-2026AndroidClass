package com.example.newslist;

import java.util.List;

public class NewsBean {
    private int code;
    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public static class ResultBean {
        private List<NewsListBean> newslist;

        public List<NewsListBean> getNewslist() {
            return newslist;
        }
    }

    public static class NewsListBean {
        private String ctime;       // 时间
        private String title;       // 标题
        private String source;      // 来源

        public String getCtime() {
            return ctime;
        }

        public String getTitle() {
            return title;
        }

        public String getSource() {
            return source;
        }

    }
}

