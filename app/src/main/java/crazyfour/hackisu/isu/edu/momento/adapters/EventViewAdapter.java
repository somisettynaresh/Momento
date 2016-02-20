package crazyfour.hackisu.isu.edu.momento.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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
            convertView = mInflater.inflate(R.layout.event_row_view, null);
            holder = new ViewHolder();
            holder.txtName = (TextView) convertView.findViewById(R.id.activity_name);
            holder.txtDesc = (TextView) convertView
                    .findViewById(R.id.activity_desc);
            holder.txtDate = (TextView) convertView.findViewById(R.id.activity_date);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtName.setText(eventsList.get(position).getName());
        holder.txtDesc.setText(eventsList.get(position)
                .getDesc());
        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
        String startTime=dateFormat.format(eventsList.get(position).getStartTime());
        String endTime=dateFormat.format(eventsList.get(position).getEndTime());
        holder.txtDate.setText(startTime + " - " + endTime);

        return convertView;
    }

    static class ViewHolder {
        TextView txtName;
        TextView txtDesc;
        TextView txtDate;
    }
}
