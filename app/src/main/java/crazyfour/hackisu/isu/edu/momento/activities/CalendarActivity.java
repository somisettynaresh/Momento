package crazyfour.hackisu.isu.edu.momento.activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
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

import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
import crazyfour.hackisu.isu.edu.momento.models.CallEntry;
import crazyfour.hackisu.isu.edu.momento.models.Event;
import crazyfour.hackisu.isu.edu.momento.models.LocationData;
import crazyfour.hackisu.isu.edu.momento.utilities.DatabaseHelper;

public class CalendarActivity extends AppCompatActivity {
    private com.google.api.services.calendar.Calendar mService = null;
    GoogleAccountCredential mCredential;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY};
    private AddressResultReceiver mResultReceiver = new AddressResultReceiver(new Handler());

    protected void startIntentService(Location mLastLocation) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(LocationConstants.RECEIVER, mResultReceiver);
        intent.putExtra(LocationConstants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
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

    @Override
    protected void onResume() {
        super.onResume();
        refreshActivity();
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        refreshActivity();
        getLocationAndAddress();
    }

    private void refreshActivity() {
        createEventsFromCallLogs();
        ArrayList<Event> events = GetEvents(new Date(getToday()));

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
        while (callLogCursor.moveToNext()) {
            String num = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.NUMBER));// for  number
            String name = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.CACHED_NAME));// for name
            int duration = Integer.parseInt(callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.DURATION)));// for duration
            int type = Integer.parseInt(callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.TYPE)));
            Calendar dateOfCall = Calendar.getInstance();
            dateOfCall.setTimeInMillis(Long.parseLong(callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.DATE))));
            //filter call logs based on last backup time stamp
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
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
            dbHelper.close();
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

    private String formatDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        return sdf.format(calendar.getTime());
    }

    private void createEventsFromCallLogs() {
        ArrayList<Event> events = new ArrayList<>();
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
        } catch (UserRecoverableAuthIOException e) {
            startActivityForResult(e.getIntent(), 1001);
        } catch (IOException ioe) {
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

    private void getSMSDetails() {
        StringBuffer stringBuffer = new StringBuffer();
        //stringBuffer.append("*********SMS History*************** :");
        Uri uri = Uri.parse("content://sms");
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                String body = cursor.getString(cursor.getColumnIndex("body"));
                String number = cursor.getString(cursor.getColumnIndex("address"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String contactName = getContactName(getApplicationContext(),number);
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

                stringBuffer.append("\nPhone Number:--- " + number + " \nContact Name -"+contactName+" \nMessage Type:--- "
                        + typeOfSMS + " \nMessage Date:--- " + smsDayTime
                        + " \nMessage Body:--- " + body);
                System.out.println(stringBuffer);
                stringBuffer.append("\n----------------------------------");
                cursor.moveToNext();
            }
        }
        cursor.close();
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

}
