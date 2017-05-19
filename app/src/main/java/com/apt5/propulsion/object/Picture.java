package com.apt5.propulsion.object;

import io.realm.RealmObject;

/**
 * Created by Tran Truong on 5/19/2017.
 */

public class Picture extends RealmObject{
    private String createTitletime;
    private String titletime;
    private String Key;
    private byte[] picture;

    public String getCreateTitletime() {
        return createTitletime;
    }

    public void setCreateTitletime(String createTitletime) {
        this.createTitletime = createTitletime;
    }

    public String getTitletime() {
        return titletime;
    }

    public void setTitletime(String titletime) {
        this.titletime = titletime;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }
}
