package com.lambda.assist.Item;

import java.io.Serializable;

/**
 * Created by asus on 2016/2/5.
 */
public class MissionData implements Serializable{
    private int missionid;
    private int msessionid;
    private String posttime;
    private String onlinelimittime;
    private String runlimittime;
    private double locationx;
    private double locationy;
    private int locationtypeid;
    private String title;
    private String content;
    private String image;
    private String gettime;

    public int getMsessionid() {
        return msessionid;
    }

    public void setMsessionid(int msessionid) {
        this.msessionid = msessionid;
    }

    public String getPosttime() {
        return posttime;
    }

    public void setPosttime(String posttime) {
        this.posttime = posttime;
    }

    public String getOnlinelimittime() {
        return onlinelimittime;
    }

    public void setOnlinelimittime(String onlinelimittime) {
        this.onlinelimittime = onlinelimittime;
    }

    public String getRunlimittime() {
        return runlimittime;
    }

    public void setRunlimittime(String runlimittime) {
        this.runlimittime = runlimittime;
    }

    public double getLocationx() {
        return locationx;
    }

    public void setLocationx(double locationx) {
        this.locationx = locationx;
    }

    public double getLocationy() {
        return locationy;
    }

    public void setLocationy(double locationy) {
        this.locationy = locationy;
    }

    public int getLocationtypeid() {
        return locationtypeid;
    }

    public void setLocationtypeid(int locationtypeid) {
        this.locationtypeid = locationtypeid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        if (!image.equals("null") && !image.equals("NULL")) {
            this.image = image;
        }
    }

    public String getGettime() {
        return gettime;
    }

    public void setGettime(String gettime) {
        this.gettime = gettime;
    }

    public int getMissionid() {
        return missionid;
    }

    public void setMissionid(int missionid) {
        this.missionid = missionid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
