package crazyfour.hackisu.isu.edu.momento.adapters;

import android.view.ViewGroup;

import com.telerik.widget.list.ListViewAdapter;
import com.telerik.widget.list.ListViewHolder;

import java.util.List;

import crazyfour.hackisu.isu.edu.momento.models.Event;

/**
 * Created by Naresh on 2/20/2016.
 */
public class ActivityViewAdapter extends ListViewAdapter

{
    /**
     * Creates an instance of the {@link ListViewAdapter} class.
     *
     * @param items a list of items that will be handled by this adapter
     */
    public ActivityViewAdapter(List<Event> items) {
        super(items);
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListViewHolder(new ActivityListItem(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        ActivityListItem itemView = (ActivityListItem) holder.itemView;
        itemView.setEvent((Event) getItems().get(position));
    }
}
