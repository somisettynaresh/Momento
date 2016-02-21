package crazyfour.hackisu.isu.edu.momento.daos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import crazyfour.hackisu.isu.edu.momento.models.CallEntry;
import crazyfour.hackisu.isu.edu.momento.models.Event;
import crazyfour.hackisu.isu.edu.momento.utilities.DatabaseHelper;

/**
 * Created by nishanthsivakumar on 2/20/16.
 */
public class EventEntryDAO {

    private SQLiteDatabase db;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public EventEntryDAO(SQLiteDatabase db){
        this.db = db;

    }

    public long insert(Event event){
        ContentValues values = new ContentValues();
        values.put("EVENT_NAME",event.getName());
        values.put("EVENT_DESC", event.getDesc());
        values.put("START_TIME",dateFormat.format(event.getStartTime()));
        values.put("END_TIME",dateFormat.format(event.getEndTime()));
        values.put("EVENT_TYPE", event.getEventType());
        values.put("EVENT_COMMENT",event.getEventComment());
        values.put("EVENT_ATTACHMENT",event.getEventAttachment());


        long newRowId;
        newRowId = db.insert("EVENT_ENTRY", null, values);

        return newRowId;
    }

    public List<Event> getEventListByDate(java.util.Date startDate) throws ParseException {
        List<Event> eventList = new ArrayList<Event>();

        String[] projection = {"EVENT_NAME","EVENT_DESC","START_TIME","END_TIME","EVENT_TYPE","EVENT_COMMENT","EVENT_ATTACHMENT","EVENT_ID"};

        String selection = "START_TIME >= ? AND START_TIME < ?";

        java.util.Date endDate = new Date(startDate.getTime()+(24*3600*1000));

        Cursor c = db.query("EVENT_ENTRY",projection,selection,new String[]{dateFormat.format(startDate),dateFormat.format(endDate)},null,null,"START_TIME DESC");

        boolean stat = c.moveToFirst();

        while(stat) {
            Event event = new Event();
            event.setName(c.getString(0));
            event.setDesc(c.getString(1));
            event.setStartTime(dateFormat.parse(c.getString(2)));
            event.setEndTime(dateFormat.parse(c.getString(3)));
            event.setEventType(c.getInt(4));
            event.setEventComment(c.getString(5));
            event.setEventAttachment(c.getString(6));
            event.setEventID(c.getInt(7));
            eventList.add(event);

            stat = c.moveToNext();
        }
        c.close();
        return eventList;
    }

    public List<Event> getEventsByTypeAndStartDate(java.util.Date startDate, int eventType) throws ParseException {
        List<Event> eventList = new ArrayList<Event>();

        String[] projection = {"EVENT_NAME","EVENT_DESC","START_TIME","END_TIME","EVENT_TYPE","EVENT_COMMENT","EVENT_ATTACHMENT","EVENT_ID"};

        String selection = "START_TIME >= ? AND EVENT_TYPE = ?";

        java.util.Date endDate = new Date(startDate.getTime()+(24*3600*1000));

        Cursor c = db.query("EVENT_ENTRY",projection,selection,new String[]{dateFormat.format(startDate),""+eventType},null,null,"START_TIME DESC");

        boolean stat = c.moveToFirst();

        while(stat) {
            Event event = new Event();
            event.setName(c.getString(0));
            event.setDesc(c.getString(1));
            event.setStartTime(dateFormat.parse(c.getString(2)));
            event.setEndTime(dateFormat.parse(c.getString(3)));
            event.setEventType(c.getInt(4));
            event.setEventComment(c.getString(5));
            event.setEventAttachment(c.getString(6));
            event.setEventID(c.getInt(7));
            eventList.add(event);

            stat = c.moveToNext();
        }
        c.close();
        return eventList;
    }

    public List<Event> getEventListByNameAndDate(String name, java.util.Date startDate) throws ParseException {
        List<Event> eventList = new ArrayList<Event>();

        String[] projection = {"EVENT_NAME","EVENT_DESC","START_TIME","END_TIME","EVENT_TYPE","EVENT_COMMENT","EVENT_ATTACHMENT","EVENT_ID"};

        String selection = "START_TIME = ? AND EVENT_NAME = ?";

        Cursor c = db.query("EVENT_ENTRY",projection,selection,new String[]{dateFormat.format(startDate),name},null,null,"START_TIME DESC");

        boolean stat = c.moveToFirst();

        while(stat) {
            Event event = new Event();
            event.setName(c.getString(0));
            event.setDesc(c.getString(1));
            event.setStartTime(dateFormat.parse(c.getString(2)));
            event.setEndTime(dateFormat.parse(c.getString(3)));
            event.setEventType(c.getInt(4));
            event.setEventComment(c.getString(5));
            event.setEventAttachment(c.getString(6));
            event.setEventID(c.getInt(7));
            eventList.add(event);

            stat = c.moveToNext();
        }

        return eventList;
    }

    public long update(Event event){
        ContentValues values = new ContentValues();
        values.put("EVENT_NAME",event.getName());
        values.put("EVENT_DESC", event.getDesc());
        values.put("START_TIME",dateFormat.format(event.getStartTime()));
        values.put("END_TIME",dateFormat.format(event.getEndTime()));
        values.put("EVENT_TYPE", event.getEventType());
        values.put("EVENT_COMMENT",event.getEventComment());
        values.put("EVENT_ATTACHMENT",event.getEventAttachment());


        long newRowId;
        newRowId = db.update("EVENT_ENTRY",values,"EVENT_ID = ?",new String[]{""+event.getEventID()});

        return newRowId;
    }
}
