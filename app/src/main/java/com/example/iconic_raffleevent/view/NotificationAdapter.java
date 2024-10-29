package com.example.iconic_raffleevent.view;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.model.Notification;

import java.util.List;

public class NotificationAdapter extends ArrayAdapter<Notification> {

    private Context context;
    private List<Notification> notificationList;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        super(context, 0, notificationList);
        this.context = context;
        this.notificationList = notificationList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        }

        TextView notificationMessageTextView = convertView.findViewById(R.id.notification_message);

        Notification notification = notificationList.get(position);
        notificationMessageTextView.setText(notification.getMessage());

        return convertView;
    }
}
