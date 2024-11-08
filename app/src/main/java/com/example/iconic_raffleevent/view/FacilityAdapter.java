package com.example.iconic_raffleevent.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.model.Facility;

import java.util.ArrayList;

/**
 * Adapter class for displaying Facility objects in a ListView.
 * This adapter inflates a custom layout for each facility and populates the views with facility details.
 */
public class FacilityAdapter extends ArrayAdapter<Facility> {

    /**
     * Constructor for creating a FacilityAdapter instance.
     *
     * @param context    The current context, used to inflate the layout file.
     * @param facilities The list of Facility objects to display in the ListView.
     */
    public FacilityAdapter(Context context, ArrayList<Facility> facilities) {
        super(context, 0, facilities);
    }

    /**
     * Provides a view for an AdapterView (ListView) to display a facility at the specified position.
     * Reuses views if possible to improve performance.
     *
     * @param position    The position of the Facility in the data set.
     * @param convertView The recycled view to populate, or null if a new view needs to be created.
     * @param parent      The parent view group that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_facility, parent, false);
        }

        Facility facility = getItem(position);

        TextView nameTextView = convertView.findViewById(R.id.facility_name);
        TextView locationTextView = convertView.findViewById(R.id.facility_location);

        nameTextView.setText(facility.getFacilityName());
        locationTextView.setText(facility.getFacilityLocation());

        return convertView;
    }
}
