package crazyfour.hackisu.isu.edu.momento.filters;


import android.content.Context;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import crazyfour.hackisu.isu.edu.momento.daos.EventBackupTimeDAO;
import crazyfour.hackisu.isu.edu.momento.daos.EventEntryDAO;
import crazyfour.hackisu.isu.edu.momento.models.Event;
import crazyfour.hackisu.isu.edu.momento.models.TextMessage;
import crazyfour.hackisu.isu.edu.momento.utilities.DatabaseHelper;

/**
 * Created by nishanthsivakumar on 2/21/16.
 */
public class MessageEntryFilter {

    private static Long getStartOfDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTimeInMillis();
    }

    public static List<Event> filterMessagesByDay(List<TextMessage> messageList, Context applicationContext){
        DatabaseHelper dbHelper = new DatabaseHelper(applicationContext);
        EventEntryDAO eventEntryDAO = new EventEntryDAO(dbHelper.getWritableDatabase());
        HashMap<Long, ArrayList<TextMessage>> dateMap = new HashMap<Long, ArrayList<TextMessage>>();
        for(TextMessage message : messageList){
            System.out.println("Message time - "+getStartOfDate(message.getDate()));
            if(dateMap.containsKey(getStartOfDate(message.getDate()))){
                dateMap.get(getStartOfDate(message.getDate())).add(message);
            }else{
                dateMap.put(getStartOfDate(message.getDate()), new ArrayList<TextMessage>());
                dateMap.get(getStartOfDate(message.getDate())).add(message);
            }
        }
        List<Event> eventList = new ArrayList<>();
        Iterator<Long> dateIterator = dateMap.keySet().iterator();
        System.out.println("Size of date map - "+dateMap.entrySet().size());
        while(dateIterator.hasNext()){
            HashMap<String,Integer> messageMap = new HashMap<>();
            Long currentDate = dateIterator.next();
            for(TextMessage msg : dateMap.get(currentDate)){
                if(messageMap.containsKey(msg.getName())){
                    messageMap.put(msg.getName(),messageMap.get(msg.getName())+1);
                }else{
                    messageMap.put(msg.getName(),1);
                }
            }
            //create event for all threads greater than size = 3

            Iterator<String> nameSetIterator = messageMap.keySet().iterator();
            while(nameSetIterator.hasNext()){
                String currentName = nameSetIterator.next();
                if(messageMap.get(currentName) >= 3){
                    //search for event with given name and date - if found update the description alone
                    List<Event> existingEvents = new ArrayList<>();
                    try {
                        existingEvents= eventEntryDAO.getEventListByNameAndDate("Conversation with "+currentName,new Date(currentDate));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(existingEvents.size() > 0){
                        System.out.println("Found existing event");
                        Event existingEvent = existingEvents.get(0);
                        String oldDesc = existingEvent.getDesc();
                        int count = Integer.parseInt(oldDesc.split(" ")[0]);
                        count+=messageMap.get(currentName);
                        existingEvent.setDesc(count + " message(s)");
                        eventEntryDAO.update(existingEvent);
                        EventBackupTimeDAO eventBackupTimeDAO = new EventBackupTimeDAO(dbHelper.getWritableDatabase());
                        eventBackupTimeDAO.insert(5,new Date(System.currentTimeMillis()));
                    }else{
                        Event event = new Event();
                        event.setEventType(5);
                        event.setStartTime(new Date(currentDate));
                        event.setEndTime(new Date(currentDate));
                        event.setName("Conversation with " + currentName);
                        event.setDesc(messageMap.get(currentName)+" messages");
                        eventList.add(event);
                    }

                }
            }
        }
        dbHelper.close();
        return eventList;
    }
}
