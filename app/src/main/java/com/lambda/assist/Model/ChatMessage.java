package com.lambda.assist.Model;

/**
 * Created by asus on 2016/4/18.
 */
public class ChatMessage {
    private int ispostuser;
    private int me;
    private String reply;
    private String replytime;


    public ChatMessage() {

    }

    public String getReplytime() {
        return replytime;
    }

    public void setReplytime(String replytime) {
        this.replytime = replytime;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public int getMe() {
        return me;
    }

    public void setMe(int me) {
        this.me = me;
    }

    public int getIspostuser() {
        return ispostuser;
    }

    public void setIspostuser(int ispostuser) {
        this.ispostuser = ispostuser;
    }
}
