package com.example.buddypunchclone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeProfileActivity extends AppCompatActivity {
    private static final int UPDATE_PROFILE_REQUEST = 100;

    private TextView nameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private TextView usernameTextView;
    private TextView userTypeTextView;
    private TextView birthDateTextView;
    private TextView hireDateTextView;
    private TextView salaryTextView;
    private TextView additionalInfoTextView;
    private ProgressBar progressBar;
    private Button updateButton;
    private Button deleteButton;
    private Long employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile);

        initializeViews();
        checkUserRole();

        employeeId = getIntent().getLongExtra("employee_id", -1);
        if (employeeId != -1) {
            fetchEmployeeDetails();
        } else {
            Toast.makeText(this, "Invalid employee ID", Toast.LENGTH_SHORT).show();
            finish();
        }

        setupButtons();
    }

    private void initializeViews() {
        // Initialize all TextViews
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        usernameTextView = findViewById(R.id.usernameTextView);
        userTypeTextView = findViewById(R.id.userTypeTextView);
        birthDateTextView = findViewById(R.id.birthDateTextView);
        hireDateTextView = findViewById(R.id.hireDateTextView);
        salaryTextView = findViewById(R.id.salaryTextView);
        additionalInfoTextView = findViewById(R.id.additionalInfoTextView);

        // Initialize other views
        progressBar = findViewById(R.id.progressBar);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);
    }

    private void checkUserRole() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        String userRole = sharedPreferences.getString("user_role", "");

        // Only show update and delete buttons for admin
        if ("ROLE_ADMIN".equals(userRole)) {
            updateButton.setVisibility(View.VISIBLE); // Show update button if user is Admin
            deleteButton.setVisibility(View.VISIBLE); // Show delete button if user is Admin
        } else {
            updateButton.setVisibility(View.GONE); // Hide update button if user is not Admin
            deleteButton.setVisibility(View.GONE); // Hide delete button if user is not Admin
        }
    }

    private void setupButtons() {
        updateButton.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeProfileActivity.this, EmployeeProfileUpdateActivity.class);
            intent.putExtra("employee_id", employeeId);
            startActivityForResult(intent, UPDATE_PROFILE_REQUEST);
        });

        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void fetchEmployeeDetails() {
        progressBar.setVisibility(View.VISIBLE);

        AddEmployeeApiService apiService = RegistrationRetrofitClient.getSpringClient(this)
                .create(AddEmployeeApiService.class);

        Call<EmployeeResponseDto> call = apiService.getEmployeeById(employeeId);
        call.enqueue(new Callback<EmployeeResponseDto>() {
            @Override
            public void onResponse(Call<EmployeeResponseDto> call, Response<EmployeeResponseDto> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    displayEmployeeDetails(response.body());
                } else {
                    showError("Failed to load employee details");
                }
            }

            @Override
            public void onFailure(Call<EmployeeResponseDto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Error: " + t.getMessage());
            }
        });
    }

    private void displayEmployeeDetails(EmployeeResponseDto employee) {
        nameTextView.setText(String.format("%s %s", employee.getFirstName(), employee.getLastName()));
        emailTextView.setText(employee.getEmail());
        phoneTextView.setText(employee.getPhoneNumber());
        usernameTextView.setText(employee.getUsername());
        userTypeTextView.setText(employee.getUserType());
        birthDateTextView.setText(employee.getBirthDate());
        hireDateTextView.setText(employee.getHireDate());

        if (employee.getAnnualSalary() != null) {
            salaryTextView.setText(String.format("$%.2f", employee.getAnnualSalary()));
        } else {
            salaryTextView.setText("N/A");
        }

        additionalInfoTextView.setText(employee.getAdditionalInfo());
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Employee")
                .setMessage("Are you sure you want to delete this employee?")
                .setPositiveButton("Delete", (dialog, which) -> deleteEmployee())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteEmployee() {
        progressBar.setVisibility(View.VISIBLE);

        AddEmployeeApiService apiService = RegistrationRetrofitClient.getSpringClient(this)
                .create(AddEmployeeApiService.class);

        Call<Void> call = apiService.deleteEmployee(employeeId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(EmployeeProfileActivity.this,
                            "Employee deleted successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    showError("Failed to delete employee");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Error: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_PROFILE_REQUEST && resultCode == RESULT_OK) {
            // Refresh the profile data after successful update
            fetchEmployeeDetails();
        }
    }
}
