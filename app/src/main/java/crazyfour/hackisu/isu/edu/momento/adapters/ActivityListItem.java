package crazyfour.hackisu.isu.edu.momento.adapters;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.telerik.android.common.Util;
import com.telerik.widget.dataform.engine.PropertyChangedListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import crazyfour.hackisu.isu.edu.momento.R;
import crazyfour.hackisu.isu.edu.momento.models.Event;

public class ActivityListItem extends FrameLayout implements PropertyChangedListener {
    private Event event;
    private TextView startTime;
    private TextView amPmText;
    private TextView activityName;
    private TextView activityDesc;
    private TextView duration;
    private SimpleDateFormat format = new SimpleDateFormat("hh:mm aaa");

    public ActivityListItem(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.activity_list_item, this);

        startTime = Util.getLayoutPart(this, R.id.activity_list_item_time, TextView.class);
        amPmText = Util.getLayoutPart(this, R.id.reservation_list_item_ampm, TextView.class);
        activityName = Util.getLayoutPart(this, R.id.activity_list_item_name, TextView.class);
        activityDesc = Util.getLayoutPart(this, R.id.activity_list_item_desc, TextView.class);
        duration = Util.getLayoutPart(this, R.id.activity_list_item_duration, TextView.class);

        this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setEvent(Event value) {
        this.event = value;
        setActivityStartTimeText(value.getStartTime().getTime());
        activityName.setText(event.getName());
        activityDesc.setText(event.getDesc());
        setDurationText(((event.getEndTime().getTime() - event.getStartTime().getTime())/60000)+ " mins");
    }

    public Event getActivity() {
        return this.event;
    }

    private void setDurationText(String text) {
        if(text == null) {
            return;
        }
        SpannableString underlinedText = new SpannableString(text);
        underlinedText.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        duration.setText(underlinedText);
    }

    @Override
    public void onPropertyChanged(String propertyName, Object value) {
        if(propertyName.equals("ReservationTime")) {
            this.setActivityStartTimeText((Long) value);
        } else if(propertyName.equals("Name")) {
            activityName.setText(value.toString());
        } else if(propertyName.equals("Desc") || propertyName.equals("NumberOfGuests")) {
            activityDesc.setText(value.toString());
        } else if(propertyName.equals("Duration")) {
            duration.setText(value.toString());
        }
    }

    public void setActivityStartTimeText(Long value) {
        String text = format.format(new Date(value));
        startTime.setText(text.substring(0, text.length() - 3));

        amPmText.setText(text.substring(text.length() - 2, text.length()).toLowerCase());
    }
}
