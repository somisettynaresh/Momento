package crazyfour.hackisu.isu.edu.momento.builders;

import crazyfour.hackisu.isu.edu.momento.models.CallEntry;
import crazyfour.hackisu.isu.edu.momento.models.Event;

/**
 * Created by Naresh on 2/20/2016.
 */
public class EventBuilder {

    public static Event from(CallEntry callEntry) {
        return new Event("Phone Call", "Talked to " + callEntry.getName() + " for " +
                callEntry.getDuration() + " mins", callEntry.getDate(), callEntry.getDate());
    }
}