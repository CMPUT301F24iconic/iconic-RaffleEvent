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

/**
 * Adapter class to bind a list of notifications to a ListView.
 * This adapter is used to display individual notification messages in a custom list item layout.
 */
public class NotificationAdapter extends ArrayAdapter<Notification> {
    private Context context;
    private List<Notification> notificationList;

    /**
     * Constructor for creating a new instance of NotificationAdapter.
     *
     * @param context The context where the adapter is being created.
     * @param notificationList The list of notifications to be displayed in the ListView.
     */
    public NotificationAdapter(Context context, List<Notification> notificationList) {
        super(context, 0, notificationList);
        this.context = context;
        this.notificationList = notificationList;
    }

    /**
     * Gets a view for a single notification item in the ListView.
     * This method is called for each item in the ListView, inflating a custom layout
     * for each notification.
     *
     * @param position The position of the item within the adapter's data set.
     * @param convertView The recycled view to reuse, if available.
     * @param parent The parent view group that this view will eventually be attached to.
     * @return The view displaying the notification message.
     */
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
