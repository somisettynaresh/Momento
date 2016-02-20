package crazyfour.hackisu.isu.edu.momento.models;

import java.util.Date;

/**
 * Created by Naresh on 2/19/2016.
 */

public class CallEntry {
    public String name;
    public Date date;
    public int duration;
    public String number;
    public int type;

    public CallEntry(String name, Date date, int duration, String number, int type) {
        this.name = name;
        this.date = date;
        this.duration = duration;
        this.number = number;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
