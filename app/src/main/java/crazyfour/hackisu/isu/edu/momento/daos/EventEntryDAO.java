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

        long newRowId;
        newRowId = db.insert("EVENT_ENTRY", null, values);

        return newRowId;
    }

    public List<Event> getEventListByDate(java.util.Date startDate) throws ParseException {
        List<Event> eventList = new ArrayList<Event>();

        String[] projection = {"EVENT_NAME","EVENT_DESC","START_TIME","END_TIME"};

        String selection = "START_TIME > ?";

        Cursor c = db.query("EVENT_ENTRY",projection,selection,new String[]{dateFormat.format(startDate)},null,null,null);

        boolean stat = c.moveToFirst();

        while(stat) {
            Event event = new Event();
            event.setName(c.getString(0));
            event.setDesc(c.getString(1));
            event.setStartTime(dateFormat.parse(c.getString(2)));
            event.setEndTime(dateFormat.parse(c.getString(3)));
            eventList.add(event);

            stat = c.moveToNext();
        }

        return eventList;
    }
}