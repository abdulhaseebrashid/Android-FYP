//package com.example.buddypunchclone;
//
//import android.content.Intent;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.bumptech.glide.request.RequestOptions;
//import java.util.List;
//
//public class AttendanceUserAdapter extends RecyclerView.Adapter<AttendanceUserAdapter.AttendanceViewHolder> {
//    private static final String TAG = "AttendanceUserAdapter";
//    private final List<AttendanceUser> userList;
//
//    public AttendanceUserAdapter(List<AttendanceUser> userList) {
//        this.userList = userList;
//    }
//
//    @NonNull
//    @Override
//    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_attendance_user, parent, false);
//        return new AttendanceViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
//        AttendanceUser user = userList.get(position);
//        holder.bind(user);
//
//        holder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(v.getContext(), UserProfileActivity.class);
//            intent.putExtra("user_data", user);
//            v.getContext().startActivity(intent);
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return userList.size();
//    }
//
//    static class AttendanceViewHolder extends RecyclerView.ViewHolder {
//        private final TextView usernameText;
//        private final TextView idText;
//        private final TextView attendanceText;
//        private final TextView majorText;
//        private final ImageView userImage;
//
//        public AttendanceViewHolder(@NonNull View itemView) {
//            super(itemView);
//            usernameText = itemView.findViewById(R.id.text_username);
//            idText = itemView.findViewById(R.id.text_id);
//            attendanceText = itemView.findViewById(R.id.text_attendance);
//            majorText = itemView.findViewById(R.id.text_major);
//            userImage = itemView.findViewById(R.id.image_user);
//        }
//
//        public void bind(AttendanceUser user) {
//            usernameText.setText(user.getUsername());
//            idText.setText("ID: " + user.getId());
//            attendanceText.setText(String.format("Attendance: %d (Last: %s)",
//                    user.getTotalAttendance(), user.getLastAttendanceTime()));
//            majorText.setText(String.format("Major: %s | Year: %d",
//                    user.getMajor(), user.getYear()));
//
//            // Load user image
//            if (user.getImage() != null && !user.getImage().isEmpty()) {
//                Log.d(TAG, "Loading image for user: " + user.getUsername() + " URL: " + user.getImage());
//
//                RequestOptions requestOptions = new RequestOptions()
//                        .placeholder(R.drawable.user)
//                        .error(R.drawable.user)
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .circleCrop();
//
//                try {
//                    Glide.with(itemView.getContext())
//                            .load(user.getImage())
//                            .apply(requestOptions)
//                            .into(userImage);
//                } catch (Exception e) {
//                    Log.e(TAG, "Error loading image for user: " + user.getUsername(), e);
//                    userImage.setImageResource(R.drawable.user);
//                }
//            } else {
//                userImage.setImageResource(R.drawable.user);
//            }
//        }
//    }
//}





// Coloring on the Basis of Attendance
package com.example.buddypunchclone;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AttendanceUserAdapter extends RecyclerView.Adapter<AttendanceUserAdapter.AttendanceViewHolder> {
    private static final String TAG = "AttendanceUserAdapter";
    private final List<AttendanceUser> userList;

    public AttendanceUserAdapter(List<AttendanceUser> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance_user, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        AttendanceUser user = userList.get(position);
        holder.bind(user);

        // Set card color based on attendance status
        updateCardColor(holder, user);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), UserProfileActivity.class);
            intent.putExtra("user_data", user);
            v.getContext().startActivity(intent);
        });
    }

    private void updateCardColor(@NonNull AttendanceViewHolder holder, AttendanceUser user) {
        MaterialCardView cardView = (MaterialCardView) holder.itemView;
        TextView statusText = holder.statusText;

        if (hasMarkedAttendanceToday(user)) {
            // Green color for marked attendance
            cardView.setCardBackgroundColor(holder.itemView.getContext()
                    .getColor(R.color.attendance_marked_green));
            statusText.setText("✓ Today's Attendance Marked");
            statusText.setTextColor(holder.itemView.getContext().getColor(android.R.color.white));
        } else {
            // Red color for pending attendance
            cardView.setCardBackgroundColor(holder.itemView.getContext()
                    .getColor(R.color.attendance_pending_red));
            statusText.setText("✗ Today's Attendance Pending");
            statusText.setTextColor(holder.itemView.getContext().getColor(android.R.color.white));
        }
    }

    private boolean hasMarkedAttendanceToday(AttendanceUser user) {
        if (user.getLastAttendanceTime() == null || user.getLastAttendanceTime().isEmpty()) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());
        String lastAttendance = user.getLastAttendanceTime().split(" ")[0]; // Get just the date part

        return today.equals(lastAttendance);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        private final TextView usernameText;
//        private final TextView idText;
//        private final TextView attendanceText;
        private final TextView majorText;
        private final TextView statusText;
        private final ImageView userImage;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.text_username);
//            idText = itemView.findViewById(R.id.text_id);
//            attendanceText = itemView.findViewById(R.id.text_attendance);
            majorText = itemView.findViewById(R.id.text_major);
            statusText = itemView.findViewById(R.id.text_attendance_status);
            userImage = itemView.findViewById(R.id.image_user);
        }

        // Helper method to format the attendance time
        private String formatAttendanceTime(String timeString) {
            try {
                // Parse the original time string
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = inputFormat.parse(timeString);

                // Format to a more readable format
                if (date != null) {
                    SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault());
                    return outputFormat.format(date);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error formatting date: " + timeString, e);
            }
            return timeString; // Return original if parsing fails
        }

        public void bind(AttendanceUser user) {
            // Set username
            usernameText.setText(user.getUsername());

            // Set ID on separate line
//            idText.setText("ID: " + user.getId());

            // Set total attendance and last attendance time on separate lines
            if (user.getLastAttendanceTime() != null && !user.getLastAttendanceTime().isEmpty()) {
                // Format the last attendance time nicely
//                String formattedTime = formatAttendanceTime(user.getLastAttendanceTime());
//                attendanceText.setText(String.format("Attendance: %d\nLast Checked: %s",
//                        user.getTotalAttendance(), formattedTime));
//            } else {
//                attendanceText.setText(String.format("Attendance: %d\nLast Checked: Never",
//                        user.getTotalAttendance()));
            }

            // Set major and year on separate line
            majorText.setText(String.format("Major: %s\nYear: %d",
                    user.getMajor(), user.getYear()));

            // Load user image
            if (user.getImage() != null && !user.getImage().isEmpty()) {
                Log.d(TAG, "Loading image for user: " + user.getUsername() + " URL: " + user.getImage());

                RequestOptions requestOptions = new RequestOptions()
                        .placeholder(R.drawable.user)
                        .error(R.drawable.user)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .circleCrop();

                try {
                    Glide.with(itemView.getContext())
                            .load(user.getImage())
                            .apply(requestOptions)
                            .into(userImage);
                } catch (Exception e) {
                    Log.e(TAG, "Error loading image for user: " + user.getUsername(), e);
                    userImage.setImageResource(R.drawable.user);
                }
            } else {
                userImage.setImageResource(R.drawable.user);
            }
        }
    }
}