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

public class FacilityAdapter extends ArrayAdapter<Facility> {

    public FacilityAdapter(Context context, ArrayList<Facility> facilities) {
        super(context, 0, facilities);
    }

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
