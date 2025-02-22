package com.example.buddypunchclone;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeProfileUpdateActivity extends AppCompatActivity {
    private EditText firstNameEdit, lastNameEdit, emailEdit, phoneEdit,
            birthDateEdit, salaryEdit, additionalInfoEdit;
    private ProgressBar progressBar;
    private Long employeeId;
    private SimpleDateFormat displayDateFormat;
    private SimpleDateFormat apiDateFormat;
    private EmployeeResponseDto currentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile_update);

        initializeViews();
        employeeId = getIntent().getLongExtra("employee_id", -1);

        // Format for displaying dates to user
        displayDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        // Format for API communication
        apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US);

        if (employeeId != -1) {
            fetchEmployeeDetails();
        } else {
            Toast.makeText(this, "Invalid employee ID", Toast.LENGTH_SHORT).show();
            finish();
        }

        setupDatePicker();
    }

    private void initializeViews() {
        firstNameEdit = findViewById(R.id.firstNameEdit);
        lastNameEdit = findViewById(R.id.lastNameEdit);
        emailEdit = findViewById(R.id.emailEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        birthDateEdit = findViewById(R.id.birthDateEdit);
        salaryEdit = findViewById(R.id.salaryEdit);
        additionalInfoEdit = findViewById(R.id.additionalInfoEdit);
        progressBar = findViewById(R.id.progressBar);

        Button updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(v -> updateProfile());

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> finish());
    }

    private void setupDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
//            String date = dateFormat.format(calendar.getTime());
//            birthDateEdit.setText(date);
        };

        birthDateEdit.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(this, dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });
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
                    currentProfile = response.body();
                    populateFields(currentProfile);
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<EmployeeResponseDto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EmployeeProfileUpdateActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void populateFields(EmployeeResponseDto employee) {
        firstNameEdit.setText(employee.getFirstName());
        lastNameEdit.setText(employee.getLastName());
        emailEdit.setText(employee.getEmail());
        phoneEdit.setText(employee.getPhoneNumber());

        // Convert and format birth date
        try {
            Date birthDate = apiDateFormat.parse(employee.getBirthDate());
            birthDateEdit.setText(displayDateFormat.format(birthDate));
        } catch (ParseException e) {
            birthDateEdit.setText(employee.getBirthDate());
        }

        if (employee.getAnnualSalary() != null) {
            salaryEdit.setText(String.valueOf(employee.getAnnualSalary()));
        }
        additionalInfoEdit.setText(employee.getAdditionalInfo());
    }

    private void updateProfile() {
        if (!validateInputs()) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Create update DTO
        EmployeeRequestDto updateDto = new EmployeeRequestDto();

        // Set fields only if they've changed
        updateDto.setFirstName(getEditTextValue(firstNameEdit));
        updateDto.setLastName(getEditTextValue(lastNameEdit));
        updateDto.setEmail(getEditTextValue(emailEdit));
        updateDto.setPhoneNumber(getEditTextValue(phoneEdit));

        // Convert and set birth date
        try {
            Date birthDate = displayDateFormat.parse(birthDateEdit.getText().toString().trim());
            updateDto.setBirthDate(apiDateFormat.format(birthDate));
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Set salary
        try {
            double salary = Double.parseDouble(salaryEdit.getText().toString().trim());
            updateDto.setAnnualSalary(salary);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid salary format", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Set additional info
        updateDto.setAdditionalInfo(getEditTextValue(additionalInfoEdit));

        // Set UserType ID from current profile
        if (currentProfile != null) {
            // Assuming you have a way to get the UserType ID
            Long currentUserTypeId = getCurrentUserTypeId(currentProfile);
            updateDto.setUserTypeId(currentUserTypeId);
        }

        // Perform update
        performEmployeeUpdate(updateDto);
    }

    private String getEditTextValue(EditText editText) {
        return editText.getText().toString().trim();
    }

    private void performEmployeeUpdate(EmployeeRequestDto updateDto) {
        AddEmployeeApiService apiService = RegistrationRetrofitClient.getSpringClient(this)
                .create(AddEmployeeApiService.class);

        Call<EmployeeResponseDto> call = apiService.updateEmployee(employeeId, updateDto);
        call.enqueue(new Callback<EmployeeResponseDto>() {
            @Override
            public void onResponse(Call<EmployeeResponseDto> call, Response<EmployeeResponseDto> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(EmployeeProfileUpdateActivity.this,
                            "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<EmployeeResponseDto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EmployeeProfileUpdateActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleErrorResponse(Response<?> response) {
        String errorMessage = "Failed to update profile";
        try {
            if (response.errorBody() != null) {
                errorMessage += ": " + response.errorBody().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    // Implement this method to extract UserType ID from current profile
    private Long getCurrentUserTypeId(EmployeeResponseDto currentProfile) {
        // This is a placeholder. You'll need to implement the logic to get the UserType ID
        // This might involve mapping the UserType name to its ID or storing it separately
        return null; // Replace with actual implementation
    }

    private boolean validateInputs() {
        // Your existing validation logic
        if (firstNameEdit.getText().toString().trim().isEmpty() ||
                lastNameEdit.getText().toString().trim().isEmpty() ||
                emailEdit.getText().toString().trim().isEmpty() ||
                phoneEdit.getText().toString().trim().isEmpty() ||
                birthDateEdit.getText().toString().trim().isEmpty() ||
                salaryEdit.getText().toString().trim().isEmpty()) {

            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}