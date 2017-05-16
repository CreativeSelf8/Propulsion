package com.apt5.propulsion.object;

import java.util.List;

/**
 * Created by Van Quyen on 5/16/2017.
 */

public class IdeaFb {
    private String title;
    private String description;
    private String author;
    private String tag;
    private long date;
    private String id;
    private List<String> encodedImageList;
    private List<String> likelist;
    private List<Comment> commentList;
    private String authorId;

    public IdeaFb() {
    }

    public String getAuthorId() {
        return authorId;
    }

    public List<String> getLikeList() {
        return likelist;
    }

    public void setLikeList(List<String> likelist) {
        this.likelist = likelist;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public List<String> getEncodedImageList() {
        return encodedImageList;
    }

    public void setEncodedImageList(List<String> encodedImageList) {
        this.encodedImageList = encodedImageList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
