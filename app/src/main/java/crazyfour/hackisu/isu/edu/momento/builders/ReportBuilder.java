package crazyfour.hackisu.isu.edu.momento.builders;

import android.content.Context;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import crazyfour.hackisu.isu.edu.momento.daos.EventEntryDAO;
import crazyfour.hackisu.isu.edu.momento.models.Event;
import crazyfour.hackisu.isu.edu.momento.models.EventReport;
import crazyfour.hackisu.isu.edu.momento.models.ReportTuple;
import crazyfour.hackisu.isu.edu.momento.utilities.DatabaseHelper;

/**
 * Created by nishanthsivakumar on 2/21/16.
 */
public class ReportBuilder {

    private Date startDate;
    private Date endDate;
    private DatabaseHelper dbHelper;

    public ReportBuilder(Date startDate, Context applicationContext){
        this.startDate = startDate;
        dbHelper = new DatabaseHelper(applicationContext);
    }

    public EventReport getWeeklyReport(){
        EventReport eventReport = new EventReport();

        List<ReportTuple> callReport = getCallReport();
        eventReport.setCallReport(callReport);

        List<ReportTuple> messageReport = getMessagesReport();
        eventReport.setMessageReport(messageReport);

        List<ReportTuple> locationReport = getLocationReport();
        eventReport.setLocationReport(locationReport);

        return eventReport;
    }

    private List<ReportTuple> getCallReport(){
        EventEntryDAO eventEntryDAO = new EventEntryDAO(dbHelper.getReadableDatabase());
        List<Event> callEvent = null;
        try {
            callEvent = eventEntryDAO.getEventsByTypeAndStartDate(startDate,1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        HashMap<String,ReportTuple> callerMap = new HashMap<>();
        for(Event event: callEvent){
            String[] tokens = event.getDesc().split(" ");
            StringBuilder callerName = new StringBuilder();
            callerName.append(event.getDesc().split("for")[0]);
            int duration = 0;
            for(int i=0;i<tokens.length;i++){
                if(tokens[i].trim() == "mins"){
                    duration = Integer.parseInt(tokens[i-1]);
                }
            }
            if(callerMap.containsKey(callerName.toString().trim())){
                ReportTuple tuple = callerMap.get(callerName.toString().trim());
                tuple.setEventDuration(tuple.getEventDuration()+duration);
                tuple.setEventFrequency(tuple.getEventFrequency()+1);
                callerMap.put(callerName.toString().trim(),tuple);
            }else{
                ReportTuple tuple = new ReportTuple();
                tuple.setEventFrequency(1);
                tuple.setEventDuration(duration);
                tuple.setEventName(callerName.toString());
                callerMap.put(callerName.toString().trim(),tuple);
            }
        }
        ArrayList<ReportTuple> callerReportList = new ArrayList<>();
        for(String callerName: callerMap.keySet()){
            callerReportList.add(callerMap.get(callerName));
        }
        Collections.sort(callerReportList,new ReportTuple());
        if(callerReportList.size() > 3){
            return callerReportList.subList(0,3);
        }else {
            return callerReportList;
        }

    }

    private List<ReportTuple> getMessagesReport(){
        EventEntryDAO eventEntryDAO = new EventEntryDAO(dbHelper.getReadableDatabase());
        List<Event> messageEvent = null;
        try {
            messageEvent = eventEntryDAO.getEventsByTypeAndStartDate(startDate,5);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        HashMap<String,ReportTuple> messagesMap = new HashMap<>();
        for(Event event: messageEvent){
            String[] tokens = event.getName().split("with")[1].split(" ");
            StringBuilder callerName = new StringBuilder();
            int duration = 0;
            for(int i=0;i<tokens.length;i++){
                callerName.append(tokens[i]+" ");
            }
            duration = Integer.parseInt(event.getDesc().split("message")[0].trim());
            if(messagesMap.containsKey(callerName.toString().trim())){
                ReportTuple tuple = messagesMap.get(callerName.toString());
                tuple.setEventDuration(tuple.getEventDuration()+duration);
                tuple.setEventFrequency(tuple.getEventFrequency()+1);
                messagesMap.put(callerName.toString(),tuple);
            }else{
                ReportTuple tuple = new ReportTuple();
                tuple.setEventFrequency(1);
                tuple.setEventDuration(duration);
                tuple.setEventName("Conversations with "+callerName.toString());
                messagesMap.put(callerName.toString().trim(),tuple);
            }
        }
        ArrayList<ReportTuple> messagesReportList = new ArrayList<>();
        for(String callerName: messagesMap.keySet()){
            messagesReportList.add(messagesMap.get(callerName));
        }
        Collections.sort(messagesReportList,new ReportTuple());
        if(messagesReportList.size() >3){
            return messagesReportList.subList(0,3);
        }else{
            return messagesReportList;
        }

    }

    private List<ReportTuple> getLocationReport(){
        EventEntryDAO eventEntryDAO = new EventEntryDAO(dbHelper.getReadableDatabase());
        List<Event> messageEvent = null;
        try {
            messageEvent = eventEntryDAO.getEventsByTypeAndStartDate(startDate,2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        HashMap<String,ReportTuple> locationsMap = new HashMap<>();
        for(Event event: messageEvent){
            String locationName = event.getDesc();
            int duration = (int) ((event.getEndTime().getTime()-event.getStartTime().getTime())/6000);
            if(locationsMap.containsKey(locationName)){
                ReportTuple tuple = locationsMap.get(locationName.toString());
                tuple.setEventDuration(tuple.getEventDuration()+duration);
                tuple.setEventFrequency(tuple.getEventFrequency()+1);
                locationsMap.put(locationName.toString(),tuple);
            }else{
                ReportTuple tuple = new ReportTuple();
                tuple.setEventFrequency(1);
                tuple.setEventDuration(duration);
                tuple.setEventName("Visits to "+locationName.toString());
                locationsMap.put(locationName,tuple);
            }
        }
        ArrayList<ReportTuple> locationReportList = new ArrayList<>();
        for(String locationName: locationsMap.keySet()){
            locationReportList.add(locationsMap.get(locationName));
        }
        Collections.sort(locationReportList,new ReportTuple());
        if(locationReportList.size() > 3){
            return locationReportList.subList(0,3);
        }else{
            return locationReportList;
        }


    }
}
