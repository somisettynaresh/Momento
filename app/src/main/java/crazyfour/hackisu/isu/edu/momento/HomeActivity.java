package crazyfour.hackisu.isu.edu.momento;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import crazyfour.hackisu.isu.edu.momento.models.CallEntry;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
        getCallLogDetails();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
