package crazyfour.hackisu.isu.edu.momento.activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import crazyfour.hackisu.isu.edu.momento.R;
import crazyfour.hackisu.isu.edu.momento.adapters.EventViewAdapter;
import crazyfour.hackisu.isu.edu.momento.builders.EventBuilder;
import crazyfour.hackisu.isu.edu.momento.daos.EventEntryDAO;
import crazyfour.hackisu.isu.edu.momento.models.CallEntry;
import crazyfour.hackisu.isu.edu.momento.models.Event;
import crazyfour.hackisu.isu.edu.momento.utilities.DatabaseHelper;

public class CalendarActivity extends AppCompatActivity {
    private com.google.api.services.calendar.Calendar mService = null;
    GoogleAccountCredential mCredential;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY};

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
        createEventsFromCallLogs();
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

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                calendarSync();
                return "";
            }
        };
        //task.execute();
    }

    private ArrayList<CallEntry> getCallLogDetails() {
        ArrayList<CallEntry> callEntries = new ArrayList<>();
        /*Cursor callLogCursor = managedQuery(allCalls, null, null, null, null);

        while (callLogCursor.moveToNext()) {
            String num = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.NUMBER));// for  number
            String name = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.CACHED_NAME));// for name
            int duration = Integer.parseInt(callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.DURATION)));// for duration
            int type = Integer.parseInt(callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.TYPE)));
            Calendar dateOfCall = Calendar.getInstance();
            dateOfCall.setTimeInMillis(Long.parseLong(callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.DATE))));
            callEntries.add(new CallEntry(name, dateOfCall.getTime(), duration, num, type));
            System.out.println("Call to " + name + " number:  " + num + " for " + duration + " mins");
        }
        */


        String[] strFields = {
                android.provider.CallLog.Calls.NUMBER,
                android.provider.CallLog.Calls.TYPE,
                android.provider.CallLog.Calls.CACHED_NAME,
                android.provider.CallLog.Calls.DATE,
                android.provider.CallLog.Calls.DURATION,

        };

        // Defines a string to contain the selection clause
        String mSelectionClause = android.provider.CallLog.Calls.DATE + " >= ?";

        // Initializes an array to contain selection arguments
        String[] mSelectionArgs = {getToday().toString()};

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            //TODO:
            return null;
        }
        Cursor callLogCursor = getContentResolver().query(
                android.provider.CallLog.Calls.CONTENT_URI,
                strFields,
                mSelectionClause,
                mSelectionArgs,
                null
        );
        while (callLogCursor.moveToNext()) {
            String num = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.NUMBER));// for  number
            String name = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.CACHED_NAME));// for name
            int duration = Integer.parseInt(callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.DURATION)));// for duration
            int type = Integer.parseInt(callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.TYPE)));
            Calendar dateOfCall = Calendar.getInstance();
            dateOfCall.setTimeInMillis(Long.parseLong(callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.DATE))));
            callEntries.add(new CallEntry(name, dateOfCall.getTime(), duration, num, type));
            System.out.println("Call to " + name + " number:  " + num + " for " + duration + " mins");
        }
        System.out.println("Size of the call Entries " + callEntries.size());
        return callEntries;
    }

    public static Long getToday() {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.set(today.getYear(), today.getMonth(), today.getDate());
        return calendar.getTimeInMillis();
    }


    private void createEventsFromCallLogs() {
        ArrayList<Event> events = new ArrayList<>();
        ArrayList<CallEntry> callEntries = getCallLogDetails();
        for (CallEntry call : callEntries) {
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            EventEntryDAO eventEntryDAO = new EventEntryDAO(dbHelper.getWritableDatabase());
            eventEntryDAO.insert(EventBuilder.from(call));

        }
    }

    private ArrayList<Event> GetEvents() {
        ArrayList<Event> events = new ArrayList<>();
        events.add(new Event("Test Event", "Test Event Desc", new Date(), new Date()));
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        EventEntryDAO eventEntryDAO = new EventEntryDAO(dbHelper.getWritableDatabase());
        try {
            events.addAll(eventEntryDAO.getEventListByDate(new Date(getToday())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return events;
    }

    private void calendarSync() {
        //calendar API call
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, "nishanth.ralph@gmail.com"));
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();

        try {
            List<String> event_list = getDataFromApi();
            for (String s : event_list) {
                System.out.println(s);
            }
        }
        catch (UserRecoverableAuthIOException e) {
            startActivityForResult(e.getIntent(), 1001);
        }catch (IOException ioe) {
            System.out.println(ioe);
        }

    }

    private List<String> getDataFromApi() throws IOException {
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        List<String> eventStrings = new ArrayList<String>();
        Events events = mService.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<com.google.api.services.calendar.model.Event> items = events.getItems();

        for (com.google.api.services.calendar.model.Event event : items) {
            DateTime start = event.getStart().getDateTime();
            if (start == null) {
                // All-day events don't have start times, so just use
                // the start date.
                start = event.getStart().getDate();
            }
            eventStrings.add(
                    String.format("%s (%s)", event.getSummary(), start));
        }
        return eventStrings;
    }

}
