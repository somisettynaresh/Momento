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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import crazyfour.hackisu.isu.edu.momento.R;
import crazyfour.hackisu.isu.edu.momento.models.Event;

public class EventDetailsActivity extends AppCompatActivity {
    TextView activity_desc_text_view;
    TextView activity_date_text_view;
    Toolbar toolbar;

    TableLayout tb1;
    TableRow tr1,tr2;

    Button record, save, stop, play,dispPlay;
    TextView dispVPName;
    EditText notes;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    private String outputFile1 = null;

    File filename;
    File filename1;

    int counter = 0;
    int id;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //populateData();
        Event activity = getEventData();
        setToolbarTitle(activity);
        setSupportActionBar(toolbar);
        setupAudioRecorder(activity);

    }

    private void setupAudioRecorder(Event activity) {
        record = (Button) findViewById(R.id.button);
        stop = (Button) findViewById(R.id.button3);
        save = (Button) findViewById(R.id.button2);
        play = (Button) findViewById(R.id.button4);
        dispPlay = (Button)findViewById(R.id.dispPlay);

        save.setEnabled(false);

        notes = (EditText) findViewById(R.id.activity_details_notes);
        //System.out.println(notes);

        tb1 = (TableLayout)findViewById(R.id.tb1);
        tr1 = (TableRow)findViewById(R.id.tr1);
        dispVPName = (TextView)findViewById(R.id.dispVPName);
        populateData(activity);

        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);



        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                record.setVisibility(View.GONE);
                stop.setVisibility(View.VISIBLE);
                save.setEnabled(true);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myAudioRecorder.stop();
                myAudioRecorder.release();
                myAudioRecorder = null;

                Toast.makeText(getApplicationContext(), "Audio recorded successfully", Toast.LENGTH_LONG).show();
                stop.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MediaPlayer m = new MediaPlayer();

                try {
                    m.setDataSource(outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    m.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                m.start();
                Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
            }
        });

       dispPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MediaPlayer m = new MediaPlayer();

                try {
                    m.setDataSource(outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    m.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                m.start();
                Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
            }
        });

        notes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                save.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FileOutputStream outputStream;
                try {
                    outputStream = new FileOutputStream(filename);
                    outputStream.write(notes.getText().toString().getBytes());
                    outputStream.close();
                    System.out.println("Successful save");

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                tb1.setVisibility(View.VISIBLE);
                tr1.setVisibility(View.VISIBLE);
                dispVPName.setVisibility(View.VISIBLE);
                dispVPName.setText("Audio Notes");
                dispPlay.setVisibility(View.VISIBLE);

            }
        });

    }

    private void setToolbarTitle(Event activity) {
       toolbar.setTitle(activity.getName());
    }

    private Event getEventData() {
        Intent i = getIntent();
        Event activity = (Event) i.getSerializableExtra("activity");
        return activity;
    }
    private void populateData(Event activity) {
        id = activity.getEventID();
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording_" + id + ".3gp";
        outputFile1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/message_" + id + ".txt";
        filename = new File(outputFile1);
        filename1 = new File(outputFile);

        activity_desc_text_view = (TextView) findViewById(R.id.activity_details_desc);
        activity_date_text_view = (TextView) findViewById(R.id.activity_details_date);
        //activity_name_text_view.setText(activity.getName());
        activity_desc_text_view.setText(activity.getDesc());
        toolbar.setTitle(activity.getName());
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        String startTime = dateFormat.format(activity.getStartTime());
        String endTime = dateFormat.format(activity.getEndTime());
        activity_date_text_view.setText(startTime + " - " + endTime);

        if(!filename.exists()){
            System.out.println("File does not exist");
        }
        else{
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(filename));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
           System.out.println("String - " + text.toString());
            String tex = text.toString();
            notes.setText(tex);
        }

        if(!filename1.exists()){
            System.out.println("file does not exist");
        }
        else{
            tb1.setVisibility(View.VISIBLE);
            tr1.setVisibility(View.VISIBLE);
            dispVPName.setVisibility(View.VISIBLE);
            dispVPName.setText("Audio Notes");
            dispPlay.setVisibility(View.VISIBLE);
        }

    }

}
