package com.apt5.propulsion.object;

/**
 * Created by Van Quyen on 6/14/2017.
 */

public class Message {
    private String content;
    private String senderId;
    private String ideaId;
    private String status;

    public Message() {
    }

    public Message(String content, String senderId, String ideaId, String status) {
        this.content = content;
        this.senderId = senderId;
        this.ideaId = ideaId;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdeaId() {
        return ideaId;
    }

    public void setIdeaId(String ideaId) {
        this.ideaId = ideaId;
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
