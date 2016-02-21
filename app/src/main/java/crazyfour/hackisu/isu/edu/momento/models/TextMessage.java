package crazyfour.hackisu.isu.edu.momento.models;

import java.util.Date;

/**
 * Created by nishanthsivakumar on 2/21/16.
 */
public class TextMessage {

    private String name;
    private String phoneNumber;
    private String body;
    private Date date;

    public TextMessage(){

    }

    public TextMessage(String name, String phoneNumber, String body, Date date) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.body = body;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return name+" "+phoneNumber+" "+body+" "+date;
    }
}
