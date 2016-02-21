package crazyfour.hackisu.isu.edu.momento.builders;

import java.util.Date;

import crazyfour.hackisu.isu.edu.momento.models.CallEntry;
import crazyfour.hackisu.isu.edu.momento.models.Event;
import crazyfour.hackisu.isu.edu.momento.models.LocationData;

/**
 * Created by Naresh on 2/20/2016.
 */
public class EventBuilder {

    public static Event from(CallEntry callEntry) {
        return new Event("Phone Call", "Talked to " + callEntry.getName() + " for " +
                callEntry.getDuration() + " mins", callEntry.getDate(), new Date(callEntry.getDate().getTime()+(callEntry.getDuration()*60000)),1);
    }

    public static Event from(LocationData locationData, Date endDate) {
        return new Event("Location Event",locationData.getFeatureName(), locationData.getLocationDate(), endDate, 2);
    }

    public static Event buildCalendarEvent(String description, Date startDate, Date endDate ) {
        return new Event("Calendar Event",description, startDate, endDate, 3);
    }
}
