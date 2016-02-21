package crazyfour.hackisu.isu.edu.momento.filters;

import java.util.ArrayList;
import java.util.List;

import crazyfour.hackisu.isu.edu.momento.models.CallEntry;
import crazyfour.hackisu.isu.edu.momento.models.Event;

/**
 * Created by nishanthsivakumar on 2/20/16.
 */
public class CallEntrytFilter {

    public static ArrayList<CallEntry> filterByDuration(List<CallEntry> entryList){
        ArrayList<CallEntry> filteredList = new ArrayList<CallEntry>();
        for(CallEntry entry : entryList){
            if(entry.getDuration() >= 0 ){
                filteredList.add(entry);
            }
        }
        return filteredList;
    }
}
