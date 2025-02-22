// UpdateProfileActivity.java
package com.example.buddypunchclone;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity {
    // Declare views and variables
    private EditText setUsername;
    private EditText setMajor;
    private EditText setStanding;
    private EditText setYear;
    private EditText setStartingYear;
    private ApiService apiService;
    private AttendanceUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        // Setup action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Update Profile");
        }

        // Initialize API service
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Initialize all views
        initializeViews();

        // Get user data from intent
        currentUser = (AttendanceUser) getIntent().getSerializableExtra("user_data");

        // Populate fields with current data
        if (currentUser != null) {
            populateUserData();
        }

        // Setup save button click listener
        Button btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    updateProfile();
                }
            }
        });
    }

    private void initializeViews() {
        setUsername = findViewById(R.id.set_username);
        setMajor = findViewById(R.id.set_major);
        setStanding = findViewById(R.id.set_standing);
        setYear = findViewById(R.id.set_year);
        setStartingYear = findViewById(R.id.set_starting_year);
    }

    private void populateUserData() {
        setUsername.setText(currentUser.getUsername());
        setMajor.setText(currentUser.getMajor());
        setStanding.setText(currentUser.getStanding());
        setYear.setText(String.valueOf(currentUser.getYear()));
        setStartingYear.setText(String.valueOf(currentUser.getStartingYear()));

        // Disable username editing as it's the identifier
        setUsername.setEnabled(false);
    }

    private boolean validateInput() {
        // Validate Major
        if (setMajor.getText().toString().trim().isEmpty()) {
            setMajor.setError("Major is required");
            setMajor.requestFocus();
            return false;
        }

        // Validate Standing
        if (setStanding.getText().toString().trim().isEmpty()) {
            setStanding.setError("Standing is required");
            setStanding.requestFocus();
            return false;
        }

        // Validate Years
        if (setYear.getText().toString().trim().isEmpty()) {
            setYear.setError("Year is required");
            setYear.requestFocus();
            return false;
        }

        if (setStartingYear.getText().toString().trim().isEmpty()) {
            setStartingYear.setError("Starting year is required");
            setStartingYear.requestFocus();
            return false;
        }

        return true;
    }

    private void updateProfile() {
        if (currentUser == null) return;

        // Create updated user object
        AttendanceUser updatedUser = new AttendanceUser();

        // Set the updated fields
        updatedUser.setUsername(currentUser.getUsername());  // Keep the same username
        updatedUser.setMajor(setMajor.getText().toString().trim());
        updatedUser.setStanding(setStanding.getText().toString().trim());
        updatedUser.setYear(Integer.parseInt(setYear.getText().toString().trim()));
        updatedUser.setStartingYear(Integer.parseInt(setStartingYear.getText().toString().trim()));

        // Preserve existing fields that shouldn't be modified
        updatedUser.setTotalAttendance(currentUser.getTotalAttendance());
        updatedUser.setLastAttendanceTime(currentUser.getLastAttendanceTime());
        updatedUser.setExitTime(currentUser.getExitTime());
        updatedUser.setExitUserAttendance(currentUser.isExitUserAttendance());
        updatedUser.setTotalTimeUserSpent(currentUser.getTotalTimeUserSpent());
        updatedUser.setAttendanceType(currentUser.getAttendanceType());
        updatedUser.setLatitude(currentUser.getLatitude());
        updatedUser.setLongitude(currentUser.getLongitude());

        // Make API call to update the user
        apiService.updateUser(currentUser.getUsername(), updatedUser)
                .enqueue(new Callback<AttendanceUser>() {
                    @Override
                    public void onResponse(Call<AttendanceUser> call, Response<AttendanceUser> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(UpdateProfileActivity.this,
                                    "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(UpdateProfileActivity.this,
                                    "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AttendanceUser> call, Throwable t) {
                        Toast.makeText(UpdateProfileActivity.this,
                                "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
