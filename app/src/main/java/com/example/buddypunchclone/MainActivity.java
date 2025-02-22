//package com.example.buddypunchclone;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.FrameLayout;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class MainActivity extends AppCompatActivity {
//
//    private EditText emailEditText, passwordEditText;
//    private Button loginButton, registerMeButton;
//    private ImageButton gmailButton, qrButton, pinButton;
//    private LinearLayout loginLayout;
//    private FrameLayout fragmentContainer;
//    private LoginApiService apiService;
//    private static final String TAG = "MainActivity";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Initialize views
//        emailEditText = findViewById(R.id.email);
//        passwordEditText = findViewById(R.id.password);
//        loginButton = findViewById(R.id.login_button);
//        registerMeButton = findViewById(R.id.register_me_button);
//        gmailButton = findViewById(R.id.login_with_gmail);
//        qrButton = findViewById(R.id.login_with_qr);
//        pinButton = findViewById(R.id.login_with_pin);
//        loginLayout = findViewById(R.id.login_layout);
//        fragmentContainer = findViewById(R.id.fragment_container);
//
//        // Initialize Retrofit API service
//        apiService = RegistrationRetrofitClient.getClient(this).create(LoginApiService.class);
//
//        // Set click listeners
//
//        // Login button click
//        loginButton.setOnClickListener(v -> {
//            String usernameOrEmail = emailEditText.getText().toString().trim();
//            String password = passwordEditText.getText().toString().trim();
//
//            // Validate input
//            if (validateLoginInput(usernameOrEmail, password)) {
//                // Create DTO and attempt login
//                LoginDto loginDto = new LoginDto(usernameOrEmail, password);
//                loginUser(loginDto);
//            }
//        });
//
//        // Register Me button click (navigate to Registration Activity)
//        registerMeButton.setOnClickListener(v -> {
//            // Navigate to Registration Activity
//            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
//            startActivity(intent);
//        });
//
//        // Gmail login button click
//        gmailButton.setOnClickListener(v -> {
//            // Perform Gmail login logic (To be implemented)
//            Toast.makeText(this, "Gmail login coming soon", Toast.LENGTH_SHORT).show();
//        });
//
//        // QR code login button click
//        qrButton.setOnClickListener(v -> {
//            // Perform QR code login logic (To be implemented)
//            Toast.makeText(this, "QR code login coming soon", Toast.LENGTH_SHORT).show();
//        });
//
//        // PIN login button click
//        pinButton.setOnClickListener(v -> {
//            // Perform PIN login logic (To be implemented)
//            Toast.makeText(this, "PIN login coming soon", Toast.LENGTH_SHORT).show();
//        });
//    }
//
//    // Input validation method
//    private boolean validateLoginInput(String usernameOrEmail, String password) {
//        if (TextUtils.isEmpty(usernameOrEmail)) {
//            emailEditText.setError("Username or Email is required");
//            emailEditText.requestFocus();
//            return false;
//        }
//
//        if (TextUtils.isEmpty(password)) {
//            passwordEditText.setError("Password is required");
//            passwordEditText.requestFocus();
//            return false;
//        }
//
//        return true;
//    }
//
//    private void loginUser(LoginDto loginDto) {
//        // Show loading indicator or disable login button
//        loginButton.setEnabled(false);
//
//        // Make API call using Retrofit
//        apiService.loginUser(loginDto).enqueue(new Callback<JwtAuthResponse>() {
//            @Override
//            public void onResponse(Call<JwtAuthResponse> call, Response<JwtAuthResponse> response) {
//                // Re-enable login button
//                loginButton.setEnabled(true);
//
//                if (response.isSuccessful() && response.body() != null) {
//                    // Login successful
//                    String accessToken = response.body().getAccessToken();
//
//                    // Store the token (SharedPreferences)
//                    SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("auth_token", accessToken);
//                    editor.apply();
//
//                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
//
//                    // Redirect to another activity (e.g., Dashboard)
//                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
//                    // Optionally pass the token to the next activity
//                    intent.putExtra("ACCESS_TOKEN", accessToken);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    // Login failed
//                    String errorMessage = "Login Failed: Invalid credentials";
//
//                    // Check if there's a more specific error message
//                    if (response.errorBody() != null) {
//                        try {
//                            errorMessage = response.errorBody().string();
//                        } catch (Exception e) {
//                            Log.e(TAG, "Error parsing error body", e);
//                        }
//                    }
//
//                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
//
//                    // Optional: Clear password field
//                    passwordEditText.setText("");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JwtAuthResponse> call, Throwable t) {
//                // Re-enable login button
//                loginButton.setEnabled(true);
//
//                // Network error or other failure
//                Log.e(TAG, "Login API call failed", t);
//                Toast.makeText(MainActivity.this, "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}






package com.example.buddypunchclone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton, registerMeButton;
    private ImageButton gmailButton, facebookButton, githubButton;
    private LinearLayout loginLayout;
    private FrameLayout fragmentContainer;
    private LoginApiService apiService;
    private static final String TAG = "MainActivity";
    private boolean isPasswordVisible = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        registerMeButton = findViewById(R.id.register_me_button);
        gmailButton = findViewById(R.id.login_with_gmail);
        facebookButton = findViewById(R.id.login_with_facebook);
        githubButton = findViewById(R.id.login_with_github);
        loginLayout = findViewById(R.id.login_layout);
        fragmentContainer = findViewById(R.id.fragment_container);

        // Initialize Retrofit API service
        apiService = RegistrationRetrofitClient.getSpringClient(this).create(LoginApiService.class);

        // Password visibility toggle
        passwordEditText.setOnTouchListener((v, event) -> {
            final int RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[RIGHT].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });

        // Set click listeners

        // Login button click
        loginButton.setOnClickListener(v -> {
            String usernameOrEmail = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Validate input
            if (validateLoginInput(usernameOrEmail, password)) {
                // Create DTO and attempt login
                LoginDto loginDto = new LoginDto(usernameOrEmail, password);
                loginUser(loginDto);
            }
        });

        // Register Me button click (navigate to Registration Activity)
        registerMeButton.setOnClickListener(v -> {
            // Navigate to Registration Activity
            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        // Gmail login button click
        gmailButton.setOnClickListener(v -> {
            // Perform Gmail login logic (To be implemented)
            Toast.makeText(this, "Gmail login coming soon", Toast.LENGTH_SHORT).show();
        });

        // QR code login button click
        facebookButton.setOnClickListener(v -> {
            // Perform QR code login logic (To be implemented)
            Toast.makeText(this, "Facebook login coming soon", Toast.LENGTH_SHORT).show();
        });

        // PIN login button click
        githubButton.setOnClickListener(v -> {
            // Perform PIN login logic (To be implemented)
            Toast.makeText(this, "Github login coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    // Toggle password visibility method
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            isPasswordVisible = false;
        } else {
            // Show password
            passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            isPasswordVisible = true;
        }

        // Move cursor to the end of the text
        passwordEditText.setSelection(passwordEditText.getText().length());
    }

    // Input validation method
    private boolean validateLoginInput(String usernameOrEmail, String password) {
        if (TextUtils.isEmpty(usernameOrEmail)) {
            emailEditText.setError("Username or Email is required");
            emailEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void loginUser(LoginDto loginDto) {
        // Show loading indicator or disable login button
        loginButton.setEnabled(false);

        // Make API call using Retrofit
        apiService.loginUser(loginDto).enqueue(new Callback<JwtAuthResponse>() {
            @Override
            public void onResponse(Call<JwtAuthResponse> call, Response<JwtAuthResponse> response) {
                // Re-enable login button
                loginButton.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    // Login successful
                    String accessToken = response.body().getAccessToken();
                    String role = response.body().getRole();

                    // Store the token and login email (SharedPreferences)
                    SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("auth_token", accessToken);

                    // Store the email used for login
                    editor.putString("user_email", loginDto.getUsernameOrEmail());
                    editor.putString("user_role", role);
                    editor.putString("logged_username", loginDto.getUsernameOrEmail());
                    editor.apply();

                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();


                    // Redirect to another activity (e.g., Dashboard)
                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    // Optionally pass the token to the next activity
                    intent.putExtra("ACCESS_TOKEN", accessToken);
                    startActivity(intent);
                    finish();
                } else {
                    // Login failed
                    String errorMessage = "Login Failed: Invalid credentials";

                    // Check if there's a more specific error message
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    }

                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                    // Optional: Clear password field
                    passwordEditText.setText("");
                }
            }

            @Override
            public void onFailure(Call<JwtAuthResponse> call, Throwable t) {
                // Re-enable login button
                loginButton.setEnabled(true);

                // Network error or other failure
                Log.e(TAG, "Login API call failed", t);
                Toast.makeText(MainActivity.this, "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
