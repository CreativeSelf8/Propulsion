package com.apt5.propulsion.object;

/**
 * Created by Van Quyen on 6/14/2017.
 */

public class Message {
    private String content;
    private String senderId;

    public Message() {
    }

    public Message(String content, String senderId) {
        this.content = content;
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
