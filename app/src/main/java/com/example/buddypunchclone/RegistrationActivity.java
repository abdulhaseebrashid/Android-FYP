package com.example.buddypunchclone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {

    private EditText editTextFirstName, editTextLastName, editTextUsername, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonRegister;
    private RegistrationApiService apiService;
    private static final String TAG = "RegistrationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize UI components
        editTextFirstName = findViewById(R.id.first_name);
        editTextLastName = findViewById(R.id.last_name);
        editTextUsername = findViewById(R.id.username);
        editTextEmail = findViewById(R.id.gmail);
        editTextPassword = findViewById(R.id.password);
        editTextConfirmPassword = findViewById(R.id.confirm_password);
        buttonRegister = findViewById(R.id.register_button);

        // Initialize Retrofit API service
        apiService = RegistrationRetrofitClient.getSpringClient(this).create(RegistrationApiService.class);

        // Handle register button click
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Extract user input
                String firstName = editTextFirstName.getText().toString();
                String lastName = editTextLastName.getText().toString();
                String username = editTextUsername.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();

                // Simple validation logic
                if (password.equals(confirmPassword)) {
                    RegistrationDto registrationDto = new RegistrationDto(firstName, lastName, username, email, password, confirmPassword);
                    registerUser(registrationDto);
                } else {
                    Toast.makeText(RegistrationActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerUser(RegistrationDto registrationDto) {
        // Make API call using Retrofit
        apiService.registerUser(registrationDto).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // Parse the response body as string
                        String responseString = response.body().string();
                        Toast.makeText(RegistrationActivity.this, "Registration Successful: " + responseString, Toast.LENGTH_SHORT).show();
                        // Redirect to LoginActivity
                        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();  // End the current activity
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(RegistrationActivity.this, "An error occurred while parsing response.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Log the error body in case of failure
                    try {
                        Log.e(TAG, "Response body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(RegistrationActivity.this, "Registration Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "API Call failed: " + t.getMessage(), t);
                Toast.makeText(RegistrationActivity.this, "An error occurred. Please check your connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
