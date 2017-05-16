package com.apt5.propulsion.object;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Tran Truong on 4/24/2017.
 */

public class Idea extends RealmObject {
    @PrimaryKey
    @Required
    private String titletime;
    private String title;
    private String description;
    private String category;
    private int categoryId;
    private String time;
    private Date date;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitletime() {
        return titletime;
    }

    public void setTitletime(String titletime) {
        this.titletime = titletime;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}