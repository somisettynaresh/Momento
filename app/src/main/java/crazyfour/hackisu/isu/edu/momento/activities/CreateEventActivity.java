package crazyfour.hackisu.isu.edu.momento.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import crazyfour.hackisu.isu.edu.momento.R;
import crazyfour.hackisu.isu.edu.momento.builders.EventBuilder;
import crazyfour.hackisu.isu.edu.momento.daos.EventBackupTimeDAO;
import crazyfour.hackisu.isu.edu.momento.daos.EventEntryDAO;
import crazyfour.hackisu.isu.edu.momento.models.CallEntry;
import crazyfour.hackisu.isu.edu.momento.models.Event;
import crazyfour.hackisu.isu.edu.momento.utilities.DatabaseHelper;

import static java.lang.Integer.parseInt;

public class CreateEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        setTitle("Create Event");
        Button setTimeBtn = (Button) findViewById(R.id.setTime);
        setTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), "TimePicker");
            }
        });
        Button saveBtn = (Button) findViewById(R.id.save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                Intent intent= new Intent(CreateEventActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveData() {
        EditText eventNameView = (EditText) findViewById(R.id.event_name);
        EditText eventDescView = (EditText) findViewById(R.id.event_desc);
        TextView eventTimeView = (TextView) findViewById(R.id.event_time);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        String[] timeParts = String.valueOf(eventTimeView.getText()).split(":");
        cal.set(Calendar.HOUR, parseInt(timeParts[0].trim()));
        cal.set(Calendar.MINUTE, parseInt(timeParts[1].trim()));
        cal.set(Calendar.AM_PM, timeParts[2].trim() == "AM" ? 0 : 1);
        Event event = new Event(eventNameView.getText().toString(), eventDescView.getText().toString(), cal.getTime(), cal.getTime(), 4);
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        EventEntryDAO eventEntryDAO = new EventEntryDAO(dbHelper.getWritableDatabase());
        EventBackupTimeDAO eventBackupTimeDAO = new EventBackupTimeDAO(dbHelper.getReadableDatabase());
        eventEntryDAO.insert(event);
        eventBackupTimeDAO.insert(4, new Date(System.currentTimeMillis()));
        dbHelper.close();
    }
}
