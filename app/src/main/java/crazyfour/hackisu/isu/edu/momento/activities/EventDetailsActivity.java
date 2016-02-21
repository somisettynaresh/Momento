package crazyfour.hackisu.isu.edu.momento.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import crazyfour.hackisu.isu.edu.momento.R;
import crazyfour.hackisu.isu.edu.momento.models.Event;

public class EventDetailsActivity extends AppCompatActivity {
    TextView activity_name_text_view;
    TextView activity_desc_text_view;
    TextView activity_date_text_view;

    Button record,save;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;

    int counter = 0;

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

        record = (Button)findViewById(R.id.button);
        save = (Button)findViewById(R.id.button2);
        save.setEnabled(false);
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording_.3gp";

        myAudioRecorder=new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (counter == 0) {

                    try {
                        myAudioRecorder.prepare();
                        myAudioRecorder.start();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
                    counter++;
                }
                else if(counter==1){
                    myAudioRecorder.stop();
                    myAudioRecorder.release();
                    myAudioRecorder  = null;

                    Toast.makeText(getApplicationContext(), "Audio recorded successfully",Toast.LENGTH_LONG).show();
                    counter++;
                }
                else if(counter==2){
                    MediaPlayer m = new MediaPlayer();

                    try {
                        m.setDataSource(outputFile);
                    }

                    catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        m.prepare();
                    }

                    catch (IOException e) {
                        e.printStackTrace();
                    }

                    m.start();
                    Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
                }
            }
        });

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
