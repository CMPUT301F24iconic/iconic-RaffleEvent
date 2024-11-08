package com.example.iconic_raffleevent.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.model.User;
import java.util.List;

/**
 * Adapter class for displaying a list of User objects in a RecyclerView.
 * Utilizes ViewHolder pattern for efficient memory usage.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;

    /**
     * Constructs a new UserAdapter with the provided list of users.
     *
     * @param userList the list of User objects to be displayed in the RecyclerView.
     */
    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    /**
     * Adds a new User to the list and notifies the adapter to refresh.
     *
     * @param user the User object to be added to the list.
     */
    public void addUser(User user) {
        userList.add(user);
    }

    /**
     * Creates a new ViewHolder to represent an individual list item.
     *
     * @param parent   the parent ViewGroup into which the new view will be added.
     * @param viewType the view type of the new view.
     * @return a new UserViewHolder that holds a view for each user item.
     */
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    /**
     * Binds data to the ViewHolder for the given position.
     * Loads the user's profile image using Glide if available; otherwise, loads a default image.
     *
     * @param holder   the ViewHolder in which data should be bound.
     * @param position the position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userNameTextView.setText(user.getName());
        holder.userEmailTextView.setText(user.getEmail());

        // Load profile image using Glide
        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(user.getProfileImageUrl())
                    .into(holder.profileImageView);
        } else {
            holder.profileImageView.setImageResource(R.drawable.default_profile);
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return the total number of users in the list.
     */
    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * ViewHolder class for holding the views for each item in the RecyclerView.
     */
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView userNameTextView;
        TextView userEmailTextView;

        /**
         * Constructs a new UserViewHolder with the specified itemView, initializing
         * references to the profile image and text views for the user's name and email.
         *
         * @param itemView the view of the item in the RecyclerView.
         */
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            userEmailTextView = itemView.findViewById(R.id.userEmailTextView);
        }
    }
}
