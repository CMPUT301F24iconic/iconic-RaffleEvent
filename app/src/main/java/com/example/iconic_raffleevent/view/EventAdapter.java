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

    public EventAdapter(Context context, List<Event> eventList) {
        super(context, 0, eventList);
        this.context = context;
        this.eventList = eventList;
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

        Event event = eventList.get(position);


        Glide.with(context)
                .load(event.getEventImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(eventImageView);

        eventTitleTextView.setText(event.getEventTitle());
        eventDateTextView.setText(event.getEventStartDate());

        return convertView;
    }
}