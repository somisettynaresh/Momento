package crazyfour.hackisu.isu.edu.momento.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Toast;

import com.telerik.android.common.Util;
import com.telerik.widget.calendar.CalendarSelectionMode;
import com.telerik.widget.calendar.RadCalendarView;
import com.telerik.widget.calendar.decorations.SegmentDecorator;

import java.util.Date;

import crazyfour.hackisu.isu.edu.momento.R;

public class SelectDateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_date);
        RadCalendarView calendarView = (RadCalendarView)findViewById(R.id.calendarView);
        calendarView.setSelectionMode(CalendarSelectionMode.Single);
        calendarView.setMaxDate(new Date().getTime());
        SegmentDecorator decorator = new SegmentDecorator(calendarView);
        decorator.setColor(Color.parseColor("#009688"));
        decorator.setStrokeWidth(Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 3));

        calendarView.setCellDecorator(decorator);
        calendarView.setOnSelectedDatesChangedListener(new RadCalendarView.
                OnSelectedDatesChangedListener() {
            @Override
            public void onSelectedDatesChanged(
                    RadCalendarView.SelectionContext context) {
                Intent activityListIntent = new Intent(SelectDateActivity.this, CalendarActivity.class);
                activityListIntent.putExtra("dateSelected",context.newSelection().get(0));
                startActivity(activityListIntent);
            }
        });
    }
}
