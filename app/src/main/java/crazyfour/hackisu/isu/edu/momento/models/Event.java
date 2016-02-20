package crazyfour.hackisu.isu.edu.momento.models;

import java.util.Date;

/**
 * Created by Naresh on 2/20/2016.
 */
public class Event {
    private String name;
    private String desc;
    private Date startTime;
    private Date endTime;

    public Event(String name, String desc, Date startTime, Date endTime) {
        this.name = name;
        this.desc = desc;
        this.startTime = startTime;
        this.endTime = endTime;
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
