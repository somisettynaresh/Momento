package crazyfour.hackisu.isu.edu.momento.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Naresh on 2/20/2016.
 */
public class Event implements Serializable{
    private String name;
    private String desc;
    private Date startTime;
    private Date endTime;
    private int eventType;
    private String eventComment;
    private String eventAttachment;

    public Event(String name, String desc, Date startTime, Date endTime, int eventType) {
        this.name = name;
        this.desc = desc;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventType = eventType;
    }

    public Event(){

    }

    public String getEventAttachment() {
        return eventAttachment;
    }

    public void setEventAttachment(String eventAttachment) {
        this.eventAttachment = eventAttachment;
    }

    public String getEventComment() {
        return eventComment;
    }

    public void setEventComment(String eventComment) {
        this.eventComment = eventComment;
    }

    public int getEventType() {
        return eventType;
    }
    public void setEventType(int eventType) {
        this.eventType = eventType;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
