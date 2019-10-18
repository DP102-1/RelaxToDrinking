package com.example.relaxtodrinking;

import java.util.Date;

public class News {
    private String news_id;
    private Date news_date;
    private String news_picture;

    public String getNews_id() {
        return news_id;
    }

    public void setNews_id(String news_id) {
        this.news_id = news_id;
    }

    public Date getNews_date() {
        return news_date;
    }

    public void setNews_date(Date news_date) {
        this.news_date = news_date;
    }

    public String getNews_picture() {
        return news_picture;
    }

    public void setNews_picture(String news_picture) {
        this.news_picture = news_picture;
    }

    public News(String news_id, Date news_date, String news_picture) {
        this.news_id = news_id;
        this.news_date = news_date;
        this.news_picture = news_picture;
    }

    public News() { }
}
