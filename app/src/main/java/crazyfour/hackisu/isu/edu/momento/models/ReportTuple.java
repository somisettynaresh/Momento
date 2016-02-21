package crazyfour.hackisu.isu.edu.momento.models;

import java.util.Comparator;

/**
 * Created by nishanthsivakumar on 2/21/16.
 */
public class ReportTuple implements Comparator {

    private String eventName;
    private int eventFrequency;
    private int eventDuration;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public int getEventFrequency() {
        return eventFrequency;
    }

    public void setEventFrequency(int eventFrequency) {
        this.eventFrequency = eventFrequency;
    }

    public int getEventDuration() {
        return eventDuration;
    }

    public void setEventDuration(int eventDuration) {
        this.eventDuration = eventDuration;
    }

    @Override
    public int compare(Object lhs, Object rhs) {
        int result = 0;
        ReportTuple r1 = (ReportTuple) lhs;
        ReportTuple r2 = (ReportTuple) rhs;
        if(r1.getEventFrequency() > r2.getEventFrequency()){
            result = 1;
        }else if(r1.getEventFrequency() < r2.getEventFrequency()){
            result = -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return eventName+"\nDuration: "+eventDuration+"\nFrequency: "+eventFrequency+"\n";
    }
}
