package com.example.iconic_raffleevent.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.model.Event;

import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> {

    private Context context;
    private List<Event> eventList;
    private String currentUserId;

    public EventAdapter(Context context, List<Event> eventList, String currentUserId) {
        super(context, 0, eventList);
        this.context = context;
        this.eventList = eventList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        }

        ImageView eventImageView = convertView.findViewById(R.id.event_image);
        TextView eventTitleTextView = convertView.findViewById(R.id.event_title);
        TextView eventDateTextView = convertView.findViewById(R.id.event_date);
        ImageView manageEventIcon = convertView.findViewById(R.id.manage_event_icon); // Add the icon reference

        Event event = eventList.get(position);

        // Load event image
        Glide.with(context)
                .load(event.getEventImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(eventImageView);

        // Set event title and date
        eventTitleTextView.setText(event.getEventTitle());
        eventDateTextView.setText(event.getEventStartDate());

        // Show the manage event icon only if the current user is the organizer
        if (event.getOrganizerID().equals(currentUserId)) {
            manageEventIcon.setVisibility(View.VISIBLE);
        } else {
            manageEventIcon.setVisibility(View.GONE);
        }

        return convertView;
    }

}
