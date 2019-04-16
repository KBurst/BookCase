package com.example.bookcase;

import org.json.JSONException;
import org.json.JSONObject;

public class Book {

    private int id;
    private String title;
    private String author;
    private int yearPublished;
    private String coverURL;
    private int duration;

    public Book(JSONObject jsonBook) throws JSONException {
        this.id = jsonBook.getInt("book_id");
        this.title = jsonBook.getString("title");
        this.author = jsonBook.getString("author");
        this.yearPublished = jsonBook.getInt("published");
        this.coverURL = jsonBook.getString("cover_url");
        this.duration = jsonBook.getInt("duration");
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYearPublished() {
        return yearPublished;
    }

    public void setYearPublished(int yearPublished) {
        this.yearPublished = yearPublished;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
