package crazyfour.hackisu.isu.edu.momento.activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
import com.telerik.widget.list.RadListView;

import java.io.IOException;

import java.text.DateFormat;
import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import crazyfour.hackisu.isu.edu.momento.FetchAddressIntentService;
import crazyfour.hackisu.isu.edu.momento.R;
import crazyfour.hackisu.isu.edu.momento.adapters.ActivityViewAdapter;
import crazyfour.hackisu.isu.edu.momento.adapters.EventViewAdapter;
import crazyfour.hackisu.isu.edu.momento.builders.EventBuilder;
import crazyfour.hackisu.isu.edu.momento.constants.LocationConstants;
import crazyfour.hackisu.isu.edu.momento.daos.EventBackupTimeDAO;
import crazyfour.hackisu.isu.edu.momento.daos.EventEntryDAO;
import crazyfour.hackisu.isu.edu.momento.filters.CallEntrytFilter;
import crazyfour.hackisu.isu.edu.momento.daos.LocationDataDAO;
import crazyfour.hackisu.isu.edu.momento.filters.MessageEntryFilter;
import crazyfour.hackisu.isu.edu.momento.models.CallEntry;
import crazyfour.hackisu.isu.edu.momento.models.Event;
import crazyfour.hackisu.isu.edu.momento.models.LocationData;
import crazyfour.hackisu.isu.edu.momento.models.TextMessage;
import crazyfour.hackisu.isu.edu.momento.utilities.DatabaseHelper;

public class CalendarActivity extends AppCompatActivity {
    private com.google.api.services.calendar.Calendar mService = null;
    GoogleAccountCredential mCredential;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY};
    private AddressResultReceiver mResultReceiver = new AddressResultReceiver(new Handler());
    private Date selectedDate = new Date();

    protected void startIntentService(Location mLastLocation) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(LocationConstants.RECEIVER, mResultReceiver);
        intent.putExtra(LocationConstants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        createEventsFromCallLogs();
        addMessageEvents(getSMSDetails());
        refreshActivity(this.selectedDate);
    }

    private void updateActivityCount(int count, String date) {
        final TextView countHeader = (TextView) findViewById(R.id.activities_count_header);
        countHeader.setText(count + " activities on " + date);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Moments");
        setSupportActionBar(toolbar);

        ImageView calendarIcon = (ImageView) findViewById(R.id.calendarView);
        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calendarIntent = new Intent(CalendarActivity.this, SelectDateActivity.class);
                startActivity(calendarIntent);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(CalendarActivity.this, CreateEventActivity.class);
               startActivity(intent);
            }
        });
        Intent intent = getIntent();
        selectedDate = parse(intent.getLongExtra("dateSelected",0));
        createEventsFromCallLogs();
        addMessageEvents(getSMSDetails());
        refreshActivity(selectedDate);
        getLocationAndAddress();
    }

    private Date parse(long date) {
        if(date == 0) {
            return new Date(getToday());
        }
        return new Date(date);
    }

    private void refreshActivity(Date selectedDate) {
        ArrayList<Event> events = GetEvents(selectedDate);

        final ListView lv = (ListView) findViewById(R.id.srListView);
        lv.setAdapter(new EventViewAdapter(this, events));


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Object o = lv.getItemAtPosition(position);
                Event activity = (Event) o;
                Intent intent = new Intent(CalendarActivity.this, EventDetailsActivity.class);
                intent.putExtra("activity", activity);
                startActivity(intent);
                Toast.makeText(CalendarActivity.this, "You have chosen: " + " " + activity.getName(), Toast.LENGTH_LONG).show();
            }
        });

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                calendarSync();
                return "";
            }
        };
        getSMSDetails();
        task.execute();
        //task.execute();

    }

    private ArrayList<CallEntry> getCallLogDetails() {
        ArrayList<CallEntry> callEntries = new ArrayList<>();
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
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        while (callLogCursor.moveToNext()) {
            String num = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.NUMBER));// for  number
            String name = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            if(name==null || name.equals("")){
                name = num;
            }// for name
            int duration = Integer.parseInt(callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.DURATION)));// for duration
            int type = Integer.parseInt(callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.TYPE)));
            Calendar dateOfCall = Calendar.getInstance();
            dateOfCall.setTimeInMillis(Long.parseLong(callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.DATE))));
            //filter call logs based on last backup time stamp

            EventBackupTimeDAO eventBackupTimeDAO = new EventBackupTimeDAO(dbHelper.getReadableDatabase());
            Date lastBackupTime = new Date(getToday());
            try {
                lastBackupTime = eventBackupTimeDAO.getLastBackupTime(type);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (dateOfCall.getTime().compareTo(lastBackupTime) >= 0) {
                callEntries.add(new CallEntry(name, dateOfCall.getTime(), duration, num, type));
                System.out.println("Call to " + name + " number:  " + num + " for " + duration + " mins");
            }

        }
        callLogCursor.close();
        dbHelper.close();
        System.out.println("Size of the call Entries " + callEntries.size());
        return callEntries;
    }

    public static Long getToday() {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    private String formatDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        return sdf.format(calendar.getTime());
    }

    private void createEventsFromCallLogs() {
        ArrayList<CallEntry> callEntries = CallEntrytFilter.filterByDuration(getCallLogDetails());
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        EventEntryDAO eventEntryDAO = new EventEntryDAO(dbHelper.getWritableDatabase());
        EventBackupTimeDAO eventBackupTimeDAO = new EventBackupTimeDAO(dbHelper.getReadableDatabase());
        if (callEntries != null && !callEntries.isEmpty()) {
            for (CallEntry call : callEntries) {
                eventEntryDAO.insert(EventBuilder.from(call));
                eventBackupTimeDAO.insert(call.getType(), new Date(System.currentTimeMillis()));
            }
        }
        dbHelper.close();

    }

    private ArrayList<Event> GetEvents(Date forDate) {
        ArrayList<Event> events = new ArrayList<>();
        //events.add(new Event("Test Event", "Test Event Desc", new Date(), new Date(),1));
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        EventEntryDAO eventEntryDAO = new EventEntryDAO(dbHelper.getReadableDatabase());
        try {
            events.addAll(eventEntryDAO.getEventListByDate(forDate));
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            dbHelper.close();
        }
        updateActivityCount(events.size(), formatDate(forDate));
        return events;
    }

    private void getLocationAndAddress() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                System.out.println("Starting Intent Service");
                // Called when a new location is found by the network location provider
                startIntentService(location);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };
        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ) {
            System.out.println("Insufficient permissions");
        }
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                LocationConstants.MIN_LOCATION_UPDATE_TIME,
                LocationConstants.MIN_LOCATION_UPDATE_DISTANCE, locationListener);
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
            getDataFromApi();
        }
        catch (UserRecoverableAuthIOException e) {
            startActivityForResult(e.getIntent(), 1001);
        } catch (IOException ioe) {
            System.out.println(ioe);
        }

    }

    private void getDataFromApi() throws IOException {
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        DateTime startDate = getMidnightDateTime(0);
        DateTime endDate = new DateTime(System.currentTimeMillis());
        List<String> eventStrings = new ArrayList<String>();
        EventBackupTimeDAO eventBackupTimeDAO = new EventBackupTimeDAO(dbHelper.getReadableDatabase());
        Date lastBackupTime = null;
        try {
            lastBackupTime = eventBackupTimeDAO.getLastBackupTime(3);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(lastBackupTime != null) {
            startDate = new DateTime(lastBackupTime);
        }
        Events events = mService.events().list("primary")
                .setTimeMin(startDate)
                .setTimeMax(endDate)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        for (com.google.api.services.calendar.model.Event event : events.getItems()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Date eventStartDate = null;
            Date eventEndDate = null;
            try {
                eventStartDate = dateFormat.parse(event.getStart().getDateTime().toString());
                eventEndDate = dateFormat.parse(event.getEnd().getDateTime().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Event event1 = EventBuilder.buildCalendarEvent(event.getSummary(), eventStartDate, eventEndDate);
            EventEntryDAO eventEntryDAO = new EventEntryDAO(new DatabaseHelper(getApplicationContext()).getWritableDatabase());
            System.out.println("Event : " + eventEntryDAO.insert(event1));
        }
        eventBackupTimeDAO.insert(3,new Date());
    }

    private DateTime getMidnightDateTime(int daysAfterToday) {
        java.util.Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.set(java.util.Calendar.DAY_OF_YEAR, calendar.get(java.util.Calendar.DAY_OF_YEAR) + daysAfterToday);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        Date midnightYesterday = calendar.getTime();
        return new DateTime(midnightYesterday);
    }
    private String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri,
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }

    private List<TextMessage> getSMSDetails() {

        List<TextMessage> messageList = new ArrayList<TextMessage>();
        Uri uri = Uri.parse("content://sms");
        String selection = "date";
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        EventBackupTimeDAO eventBackupTimeDAO = new EventBackupTimeDAO(dbHelper.getReadableDatabase());
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                String body = cursor.getString(cursor.getColumnIndex("body"));
                String number = cursor.getString(cursor.getColumnIndex("address"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String contactName = getContactName(getApplicationContext(), number);
                if(contactName == null){
                    contactName = number;
                }
                Date smsDayTime = new Date(Long.valueOf(date));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                String typeOfSMS = null;
                switch (Integer.parseInt(type)) {
                    case 1:
                        typeOfSMS = "INBOX";
                        break;

                    case 2:
                        typeOfSMS = "SENT";
                        break;

                    case 3:
                        typeOfSMS = "DRAFT";
                        break;
                }

                Date lastBackupTime = null;
                try {
                    lastBackupTime = eventBackupTimeDAO.getLastBackupTime(5);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                System.out.println("SMSTime - "+smsDayTime+" last backup time - "+lastBackupTime);
                if((typeOfSMS == "INBOX" || typeOfSMS == "SENT") && lastBackupTime!= null && smsDayTime.getTime() > lastBackupTime.getTime()){
                    TextMessage message = new TextMessage(contactName,number,body,smsDayTime);
                    messageList.add(message);
                }
                cursor.moveToNext();
            }
        }
        dbHelper.close();
        cursor.close();
        System.out.println("Size of messages - "+messageList.size());
        return messageList;
    }

    private void addMessageEvents(List<TextMessage> messageList){
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase writableDB = dbHelper.getWritableDatabase();
        EventEntryDAO eventDAO = new EventEntryDAO(writableDB);
        List<Event> eventList = MessageEntryFilter.filterMessagesByDay(messageList, getApplicationContext());

        for(Event event : eventList){
            eventDAO.insert(event);
        }
        if(eventList.size() >0){
            EventBackupTimeDAO eventBackupTimeDAO = new EventBackupTimeDAO(writableDB);
            eventBackupTimeDAO.insert(5, new Date(System.currentTimeMillis()));
        }
        dbHelper.close();

    }


    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String message = resultData.getString(LocationConstants.RESULT_DATA_KEY);
            if(resultCode == LocationConstants.SUCCESS_RESULT) {
                DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                LocationDataDAO locationDataDAO = new LocationDataDAO(dbHelper.getWritableDatabase());
                LocationData locationData = locationDataDAO.getLastLocation();
                if(locationData != null) {
                    Event event = EventBuilder.from(locationData,new Date(System.currentTimeMillis()));
                    EventEntryDAO eventEntryDAO = new EventEntryDAO(dbHelper.getWritableDatabase());
                    System.out.println("Event : " + eventEntryDAO.insert(event));
                }
                System.out.println("Location : " + locationDataDAO.insert(
                        new LocationData(message, new Date(System.currentTimeMillis()))));
                dbHelper.close();
            } else {
                System.out.println(message);
            }
        }
    }
}
