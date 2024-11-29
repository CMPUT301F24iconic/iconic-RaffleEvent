package com.example.iconic_raffleevent.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.model.Facility;
import java.util.ArrayList;

/**
 * Adapter for displaying facilities in a RecyclerView.
 */
public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.FacilityViewHolder> {

    private final ArrayList<Facility> facilities;
    private OnItemClickListener onItemClickListener;

    public FacilityAdapter(ArrayList<Facility> facilities) {
        this.facilities = facilities;
    }

    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_facility, parent, false);
        return new FacilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        Facility facility = facilities.get(position);

        holder.facilityNameTextView.setText(facility.getFacilityName());
        holder.facilityLocationTextView.setText("Location: " + facility.getFacilityLocation());
        holder.facilityCreatorTextView.setText("Created by: " + facility.getCreator().getName());

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(facility);
            }
        });
    }

    @Override
    public int getItemCount() {
        return facilities.size();
    }

    /**
     * Updates the facilities list and refreshes the RecyclerView.
     *
     * @param newFacilities The new list of facilities.
     */
    public void updateFacilities(ArrayList<Facility> newFacilities) {
        facilities.clear();
        facilities.addAll(newFacilities);
        notifyDataSetChanged();
    }

    /**
     * Sets the item click listener for facilities.
     *
     * @param listener The listener to set.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    /**
     * Interface for handling facility item clicks.
     */
    public interface OnItemClickListener {
        void onItemClick(Facility facility);
    }

    /**
     * ViewHolder class for facility items.
     */
    static class FacilityViewHolder extends RecyclerView.ViewHolder {
        final TextView facilityNameTextView;
        final TextView facilityLocationTextView;
        final TextView facilityCreatorTextView;

        FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            facilityNameTextView = itemView.findViewById(R.id.facilityNameTextView);
            facilityLocationTextView = itemView.findViewById(R.id.facilityLocationTextView);
            facilityCreatorTextView = itemView.findViewById(R.id.facilityCreatorTextView);
        }
    }
}
