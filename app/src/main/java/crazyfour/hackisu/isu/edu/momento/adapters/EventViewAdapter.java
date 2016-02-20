package crazyfour.hackisu.isu.edu.momento.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.telerik.android.common.Util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import crazyfour.hackisu.isu.edu.momento.R;
import crazyfour.hackisu.isu.edu.momento.models.Event;

/**
 * Created by Naresh on 2/20/2016.
 */
public class EventViewAdapter extends BaseAdapter {
    private static ArrayList<Event> eventsList;

    private LayoutInflater mInflater;
    private TextView startTime;
    private TextView amPmText;
    private TextView activityName;
    private TextView activityDesc;
    private TextView duration;
    private SimpleDateFormat format = new SimpleDateFormat("hh:mm aaa");

    public EventViewAdapter(Context context, ArrayList<Event> events) {
        eventsList = events;
        mInflater = LayoutInflater.from(context);

    }

    public int getCount() {
        return eventsList.size();
    }

    public Object getItem(int position) {
        return eventsList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_list_item, null);
            holder = new ViewHolder();
            holder.amPmText = (TextView) convertView.findViewById(R.id.reservation_list_item_ampm);
            holder.startTime = (TextView) convertView.findViewById(R.id.activity_list_item_time);
            holder.activityName = (TextView) convertView.findViewById(R.id.activity_list_item_name);
            holder.activityDesc = (TextView) convertView.findViewById(R.id.activity_list_item_desc);
            holder.duration = (TextView) convertView.findViewById(R.id.activity_list_item_duration);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.activityName.setText(eventsList.get(position).getName());
        holder.activityDesc.setText(eventsList.get(position)
                .getDesc());
        holder.duration.setText(((eventsList.get(position).getEndTime().getTime() - eventsList.get(position).getStartTime().getTime())/60000)+ " mins");
        String text = format.format(eventsList.get(position).getStartTime().getTime());
        holder.amPmText.setText(text.substring(text.length() - 2, text.length()).toLowerCase());
        holder.startTime.setText(text.substring(0, text.length() - 3));

        return convertView;
    }

    static class ViewHolder {
        public TextView amPmText;
        public TextView startTime;
        public TextView activityName;
        public TextView activityDesc;
        public TextView duration;
    }
}
