package com.lambda.assist.Model;

/**
 * Created by asus on 2016/4/18.
 */
public class ChatMessage {
    private int me;
    private String message;

    public ChatMessage() {

    }

    public ChatMessage(int me, String message) {
        this.me = me;
        this.message = message;
    }

    public int getMe() {
        return me;
    }

    public void setMe(int me) {
        this.me = me;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
