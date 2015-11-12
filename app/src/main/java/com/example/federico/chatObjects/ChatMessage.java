package com.example.federico.chatObjects;

/**
 * Created by federico on 09/11/2015.
 */
public class ChatMessage {

    private boolean isMe;
    private String message;

    public boolean getIsme() {
        return isMe;
    }
    public void setMe(boolean isMe) {
        this.isMe = isMe;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}