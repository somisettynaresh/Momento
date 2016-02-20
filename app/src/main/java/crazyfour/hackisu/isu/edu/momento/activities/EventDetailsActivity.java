package crazyfour.hackisu.isu.edu.momento.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import crazyfour.hackisu.isu.edu.momento.R;
import crazyfour.hackisu.isu.edu.momento.models.Event;

public class EventDetailsActivity extends AppCompatActivity {
    TextView activity_name_text_view;
    TextView activity_desc_text_view;
    TextView activity_date_text_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
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
        populateData();
    }

    private void populateData() {
        Intent i = getIntent();
        Event activity = (Event)i.getSerializableExtra("activity");
        activity_name_text_view = (TextView) findViewById(R.id.activity_details_name);
        activity_desc_text_view = (TextView) findViewById(R.id.activity_details_desc);
        activity_date_text_view = (TextView) findViewById(R.id.activity_details_date);
        activity_name_text_view.setText(activity.getName());
        activity_desc_text_view.setText(activity.getDesc());
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        String startTime=dateFormat.format(activity.getStartTime());
        String endTime=dateFormat.format(activity.getEndTime());
        activity_date_text_view.setText(startTime + " - " + endTime);
    }

}
