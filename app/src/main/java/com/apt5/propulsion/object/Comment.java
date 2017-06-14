package com.apt5.propulsion.object;

/**
 * Created by Van Quyen on 5/16/2017.
 */

public class Comment {
    private String content;
    private String author;
    private long date;
    private String id;
    private String status;

    public Comment() {
    }

    public Comment(String content, String author, long date, String id, String status) {
        this.content = content;
        this.author = author;
        this.date = date;
        this.id = id;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
