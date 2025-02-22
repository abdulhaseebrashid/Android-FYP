package com.example.buddypunchclone;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEmployeeActivity extends AppCompatActivity {

    // UI Components
    private EditText firstname, lastname, username, email, phoneNumber, annualSalaryEditText, additionalInfoEditText;
    private TextView birthDatePreview, hireDatePreview, terminationDatePreview;
    private Spinner userTypeSpinner;
    private Button chooseFileButton, submitButton;

    // API Service
    private AddEmployeeApiService employeeApiService;

    // Other Variables
    private boolean isEditing = false;
    private Long employeeId = null;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Dialog dialog;
    private String currentDate;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        // Initialize Retrofit API Service
        employeeApiService = RegistrationRetrofitClient.getSpringClient(this).create(AddEmployeeApiService.class);

        // Initialize UI Components
        initializeViews();

        // Set up Date Selection Dialogs
        setupDateSelection();

        // Set up File Selection and Submission
        setupFileSelection();

        // Check if Editing Existing Employee
        if (getIntent().hasExtra("employee")) {
            EmployeeResponseDto employee = (EmployeeResponseDto) getIntent().getSerializableExtra("employee");
            populateEmployeeData(employee);
            isEditing = true;
            employeeId = employee.getId();
        }
    }

    private void initializeViews() {
        // Initialize current date in yyyy-MM-dd format
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Calendar.getInstance().getTime());

        // Initialize EditTexts
        firstname = findViewById(R.id.firstNameEditText);
        lastname = findViewById(R.id.lastNameEditText);
        username = findViewById(R.id.usernameEditText);
        email = findViewById(R.id.emailEditText);
        phoneNumber = findViewById(R.id.phoneEditText);
        annualSalaryEditText = findViewById(R.id.annualSalaryEditTxt);
        additionalInfoEditText = findViewById(R.id.otherInfoEditTxt);

        // Initialize TextViews for Dates
        birthDatePreview = findViewById(R.id.birthDatePreview);
        hireDatePreview = findViewById(R.id.hireDatePreview);
        terminationDatePreview = findViewById(R.id.terminationDatePreview);

        // Set Default Dates
        birthDatePreview.setText(currentDate);
        hireDatePreview.setText(currentDate);
        terminationDatePreview.setText(currentDate);

        // Initialize Spinner for User Types
        userTypeSpinner = findViewById(R.id.userTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);

        // Initialize Buttons
        chooseFileButton = findViewById(R.id.chooseFileButton);
        submitButton = findViewById(R.id.submitButton);
    }

    private void setupDateSelection() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.calendar_layout);

        ImageView birthDateArrow = findViewById(R.id.birthDateArrow);
        ImageView hireDateArrow = findViewById(R.id.hireDateArrow);
        ImageView terminationDateArrow = findViewById(R.id.terminationDateArrow);

        birthDateArrow.setOnClickListener(view -> showDateDialog(true, false, false));
        hireDateArrow.setOnClickListener(view -> showDateDialog(false, true, false));
        terminationDateArrow.setOnClickListener(view -> showDateDialog(false, false, true));
    }

    private void showDateDialog(boolean isBirth, boolean isHire, boolean isTermination) {
        CalendarView dialogCalendar = dialog.findViewById(R.id.calendar);
        Button okButton = dialog.findViewById(R.id.okButton);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);

        dialogCalendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            if (isBirth) {
                birthDatePreview.setText(selectedDate);
            } else if (isHire) {
                hireDatePreview.setText(selectedDate);
            } else if (isTermination) {
                terminationDatePreview.setText(selectedDate);
            }
        });

        okButton.setOnClickListener(v -> dialog.dismiss());
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void setupFileSelection() {
        chooseFileButton.setOnClickListener(v -> openGallery());

        submitButton.setOnClickListener(v -> submitEmployeeData());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            // Handle the selected image URI as needed
            Toast.makeText(this, "Image Selected: " + selectedImageUri.toString(), Toast.LENGTH_SHORT).show();
            // If you plan to upload the image, implement the upload logic here
        }
    }

    private void submitEmployeeData() {
        // Validate Required Fields
        if (validateInput()) {
            AddEmployeeDTO employeeDTO = createEmployeeDTO();
            if (employeeDTO != null) {
                submitEmployeeToServer(employeeDTO);
            }
        }
    }

    private boolean validateInput() {
        if (firstname.getText().toString().trim().isEmpty() ||
                lastname.getText().toString().trim().isEmpty() ||
                username.getText().toString().trim().isEmpty() ||
                email.getText().toString().trim().isEmpty() ||
                phoneNumber.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Additional validations (e.g., email format, salary numeric) can be added here

        return true;
    }

    private AddEmployeeDTO createEmployeeDTO() {
        AddEmployeeDTO employeeDTO = new AddEmployeeDTO();
        employeeDTO.setFirstName(firstname.getText().toString().trim());
        employeeDTO.setLastName(lastname.getText().toString().trim());
        employeeDTO.setUsername(username.getText().toString().trim());
        employeeDTO.setEmail(email.getText().toString().trim());
        employeeDTO.setPhoneNumber(phoneNumber.getText().toString().trim());

        String annualSalaryStr = annualSalaryEditText.getText().toString().trim();
        if (!annualSalaryStr.isEmpty()) {
            try {
                employeeDTO.setAnnualSalary(Double.parseDouble(annualSalaryStr));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid salary format", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        employeeDTO.setAdditionalInfo(additionalInfoEditText.getText().toString().trim());
        employeeDTO.setBirthDate(birthDatePreview.getText().toString().trim());
        employeeDTO.setHireDate(hireDatePreview.getText().toString().trim());
        employeeDTO.setTerminationDate(terminationDatePreview.getText().toString().trim());

        // Set User Type ID based on Spinner Selection
        employeeDTO.setUserTypeId(getUserTypeId());

        // If handling images, set imageUrl here or handle separately

        return employeeDTO;
    }

    private Long getUserTypeId() {
        // Map Spinner selection to corresponding User Type ID
        // Assuming the spinner entries are in the order: Admin, Manager, User
        // and their corresponding IDs are 1, 2, 3 respectively.
        int selectedPosition = userTypeSpinner.getSelectedItemPosition();
        switch (selectedPosition) {
            case 0:
                return 1L; // Admin
            case 1:
                return 2L; // Manager
            case 2:
                return 3L; // User
            default:
                return 3L; // Default to User
        }
    }

    private void populateEmployeeData(EmployeeResponseDto employee) {
        firstname.setText(employee.getFirstName());
        lastname.setText(employee.getLastName());
        username.setText(employee.getUsername());
        email.setText(employee.getEmail());
        phoneNumber.setText(employee.getPhoneNumber());
        // Additional fields like annualSalary and additionalInfo are not present in EmployeeResponseDto
        // Consider fetching a more comprehensive DTO if needed
    }

    private void submitEmployeeToServer(AddEmployeeDTO employeeDTO) {
        if (isEditing && employeeId != null) {
            // Update Existing Employee
            employeeApiService.updateEmployee(employeeId, employeeDTO).enqueue(new Callback<EmployeeResponseDto>() {
                @Override
                public void onResponse(Call<EmployeeResponseDto> call, Response<EmployeeResponseDto> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddEmployeeActivity.this,
                                "Employee updated successfully!",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddEmployeeActivity.this,
                                "Failed to update employee: " + response.message(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<EmployeeResponseDto> call, Throwable t) {
                    Toast.makeText(AddEmployeeActivity.this,
                            "Error: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Create New Employee
            employeeApiService.createEmployee(employeeDTO).enqueue(new Callback<EmployeeResponseDto>() {
                @Override
                public void onResponse(Call<EmployeeResponseDto> call, Response<EmployeeResponseDto> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddEmployeeActivity.this,
                                "Employee added successfully!",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddEmployeeActivity.this,
                                "Failed to add employee: " + response.message(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<EmployeeResponseDto> call, Throwable t) {
                    Toast.makeText(AddEmployeeActivity.this,
                            "Error: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
