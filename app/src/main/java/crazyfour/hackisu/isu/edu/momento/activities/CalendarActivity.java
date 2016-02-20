package crazyfour.hackisu.isu.edu.momento.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import crazyfour.hackisu.isu.edu.momento.R;
import crazyfour.hackisu.isu.edu.momento.adapters.EventViewAdapter;
import crazyfour.hackisu.isu.edu.momento.builders.EventBuilder;
import crazyfour.hackisu.isu.edu.momento.models.CallEntry;
import crazyfour.hackisu.isu.edu.momento.models.Event;

public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        ArrayList<Event> events = GetEvents();

        final ListView lv = (ListView) findViewById(R.id.srListView);
        lv.setAdapter(new EventViewAdapter(this, events));


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = lv.getItemAtPosition(position);
                Event fullObject = (Event) o;
                Toast.makeText(CalendarActivity.this, "You have chosen: " + " " + fullObject.getName(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private ArrayList<CallEntry> getCallLogDetails() {
        Uri allCalls = Uri.parse("content://call_log/calls");
        ArrayList<CallEntry> callEntries = new ArrayList<>();
        Cursor c = managedQuery(allCalls, null, null, null, null);

        while (c.moveToNext()) {
            String num = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));// for  number
            String name = c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NAME));// for name
            int duration = Integer.parseInt(c.getString(c.getColumnIndex(CallLog.Calls.DURATION)));// for duration
            int type = Integer.parseInt(c.getString(c.getColumnIndex(CallLog.Calls.TYPE)));
            Calendar dateOfCall = Calendar.getInstance();
            dateOfCall.setTimeInMillis(Long.parseLong(c.getString(c.getColumnIndex(CallLog.Calls.DATE))));
            callEntries.add(new CallEntry(name, dateOfCall.getTime(), duration, num, type));
            System.out.println("Call to " + name + " number:  " + num + " for " + duration + " mins");
        }
        System.out.println("Size of the call Entries " + callEntries.size());
        return callEntries;
    }

    private ArrayList<Event> createEventsFromCallLogs() {
        ArrayList<Event> events = new ArrayList<>();
        ArrayList<CallEntry> callEntries = getCallLogDetails();
        for (CallEntry call : callEntries) {
            events.add(EventBuilder.from(call));
        }
        return events;
    }

    private ArrayList<Event> GetEvents() {
        ArrayList<Event> events = new ArrayList<>();
        events.add(new Event("Test Event", "Test Event Desc", new Date(), new Date()));
        events.addAll(createEventsFromCallLogs());
        return events;
    }
}
