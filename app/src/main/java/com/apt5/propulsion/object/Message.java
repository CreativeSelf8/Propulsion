package com.apt5.propulsion.object;

/**
 * Created by Van Quyen on 6/14/2017.
 */

public class Message {
    private String content;
    private String senderId;
    private String ideaId;

    public Message() {
    }

    public Message(String content, String senderId, String ideaId) {
        this.content = content;
        this.senderId = senderId;
        this.ideaId = ideaId;
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
