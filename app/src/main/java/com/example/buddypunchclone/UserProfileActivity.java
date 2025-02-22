package com.example.buddypunchclone;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserProfileActivity extends AppCompatActivity {
    private AttendanceUser currentUser;
    private ApiService apiService;
    private Button updateButton;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("User Profile");
        }

        // Initialize all views
        ImageView profileImage = findViewById(R.id.profile_image);
        TextView usernameText = findViewById(R.id.profile_username);
        TextView majorText = findViewById(R.id.profile_major);
        TextView yearText = findViewById(R.id.profile_year);
        TextView standingText = findViewById(R.id.profile_standing);
        TextView attendanceText = findViewById(R.id.profile_attendance);
        TextView lastAttendanceText = findViewById(R.id.profile_last_attendance);
        TextView locationText = findViewById(R.id.profile_location);
        TextView totalTimeText = findViewById(R.id.profile_total_time);
        TextView exitTimeText = findViewById(R.id.profile_exit_time);
        TextView attendanceType = findViewById(R.id.profile_attendance_type);
        updateButton = findViewById(R.id.btn_update);
        deleteButton = findViewById(R.id.btn_delete);

        // Check user role and set button visibility
        checkRoleAndSetButtonVisibility();

        currentUser = (AttendanceUser) getIntent().getSerializableExtra("user_data");

        if (currentUser != null) {
            // Load profile image
            if (currentUser.getImage() != null && !currentUser.getImage().isEmpty()) {
                Glide.with(this)
                        .load(currentUser.getImage())
                        .circleCrop()
                        .placeholder(R.drawable.user)
                        .error(R.drawable.user)
                        .into(profileImage);
            }

            // Set all text views
            usernameText.setText(currentUser.getUsername() != null ? currentUser.getUsername() : "N/A");
            majorText.setText("Major: " + (currentUser.getMajor() != null ? currentUser.getMajor() : "N/A"));

            // Display year information with each component on a separate line
            int currentYear = new Date().getYear() + 1900;
            StringBuilder yearInfoBuilder = new StringBuilder();
            yearInfoBuilder.append("Year: ").append(currentUser.getYear()).append("\n");
            yearInfoBuilder.append("Started: ").append(currentUser.getStartingYear()).append("\n");
            yearInfoBuilder.append("Duration: ").append(currentYear - currentUser.getStartingYear()).append(" years");
            yearText.setText(yearInfoBuilder.toString());

            standingText.setText("Standing: " + (currentUser.getStanding() != null ? currentUser.getStanding() : "N/A"));

            // Display attendance information with each component on a separate line
            StringBuilder attendanceInfoBuilder = new StringBuilder();
            attendanceInfoBuilder.append("Total Attendance: ").append(currentUser.getTotalAttendance()).append("\n");
            attendanceInfoBuilder.append("Type: ").append(currentUser.getAttendanceType() != null ? currentUser.getAttendanceType() : "Regular");
            attendanceText.setText(attendanceInfoBuilder.toString());

            if (currentUser.getLastAttendanceTime() != null) {
                String lastAttendance = formatDateTime(currentUser.getLastAttendanceTime());
                lastAttendanceText.setText("Last Attendance: " + lastAttendance);
            } else {
                lastAttendanceText.setText("Last Attendance: N/A");
            }

            if (currentUser.getLatitude() != 0 && currentUser.getLongitude() != 0) {
                String locationInfo = String.format("Location: %.6f, %.6f",
                        currentUser.getLatitude(), currentUser.getLongitude());
                locationText.setText(locationInfo);
            } else {
                locationText.setText("Location: Not Available");
            }

            String timeSpent = formatTotalTime(currentUser.getTotalTimeUserSpent());
            totalTimeText.setText("Total Time Spent: " + timeSpent);

            if (currentUser.getExitTime() != null) {
                String exitTime = formatDateTime(currentUser.getExitTime());
                exitTimeText.setText("Exit Time: " + exitTime);
            } else {
                exitTimeText.setText("Exit Time: Not Available");
            }
        }

        // Set button click listeners
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateIntent = new Intent(UserProfileActivity.this, UpdateProfileActivity.class);
                updateIntent.putExtra("user_data", currentUser);
                startActivity(updateIntent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    private void checkRoleAndSetButtonVisibility() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        String userRole = sharedPreferences.getString("user_role", "");

        // Show buttons only for ROLE_ADMIN
        if ("ROLE_ADMIN".equals(userRole)) {
            updateButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            updateButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Profile");
        builder.setMessage("Are you sure you want to delete your profile? This action cannot be undone.");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUserProfile();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void deleteUserProfile() {
        if (currentUser != null && currentUser.getUsername() != null) {
            apiService.deleteUser(currentUser.getUsername()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(UserProfileActivity.this,
                                "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(UserProfileActivity.this,
                                "Failed to delete profile", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(UserProfileActivity.this,
                            "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String formatDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return "N/A";
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US);
            Date date = inputFormat.parse(dateTimeStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTimeStr;
        }
    }

    private String formatTotalTime(int minutes) {
        if (minutes <= 0) {
            return "0 minutes";
        }

        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;

        if (hours > 0) {
            return String.format("%d hours %d minutes", hours, remainingMinutes);
        } else {
            return String.format("%d minutes", minutes);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recheck role visibility when activity resumes
        checkRoleAndSetButtonVisibility();
    }
}