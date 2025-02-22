package com.example.buddypunchclone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceMarkActivity extends AppCompatActivity {
    private static final String TAG = "AttendanceMarkActivity";
    private static final int CAMERA_PERMISSION_CODE = 100;

    private ImageView previewImageView;
    private TextView statusTextView;
    private Button captureButton;
    private Button submitButton;
    private TextView attendanceDetailsText;

    private File imageFile;
    private ApiService apiService;

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Bundle extras = result.getData().getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    handleCapturedImage(imageBitmap);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_mark);

        initializeViews();
        setupButtons();
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    private void initializeViews() {
        previewImageView = findViewById(R.id.previewImageView);
        statusTextView = findViewById(R.id.statusTextView);
        captureButton = findViewById(R.id.captureButton);
        submitButton = findViewById(R.id.submitButton);
        attendanceDetailsText = findViewById(R.id.attendanceDetailsText);
    }

    private void setupButtons() {
        captureButton.setOnClickListener(v -> checkCameraPermissionAndOpen());
        submitButton.setOnClickListener(v -> submitAttendance());
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(takePictureIntent);
    }

    private void handleCapturedImage(Bitmap bitmap) {
        previewImageView.setImageBitmap(bitmap);
        previewImageView.setVisibility(View.VISIBLE);

        try {
            imageFile = createImageFile(bitmap);
        } catch (IOException e) {
            Log.e(TAG, "Error creating image file: " + e.getMessage());
            showStatus("Error processing image", false);
        }
    }

    private File createImageFile(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
        File file = new File(getCacheDir(), "attendance_image.jpg");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bos.toByteArray());
        fos.flush();
        fos.close();
        return file;
    }

    private void submitAttendance() {
        if (imageFile == null) {
            showStatus("Please capture or select an image first", false);
            return;
        }

        showStatus("Processing...", true);
        submitButton.setEnabled(false); // Disable button while processing

        try {
            RequestBody requestFile = RequestBody.create(
                    MediaType.parse("image/*"),
                    imageFile
            );

            MultipartBody.Part imagePart = MultipartBody.Part.createFormData(
                    "image",
                    imageFile.getName(),
                    requestFile
            );

            apiService.markAttendance(imagePart).enqueue(new Callback<AttendanceResponse>() {
                @Override
                public void onResponse(Call<AttendanceResponse> call, Response<AttendanceResponse> response) {
                    submitButton.setEnabled(true); // Re-enable button

                    if (response.isSuccessful() && response.body() != null) {
                        AttendanceResponse data = response.body();
                        String message = data.getMessage();

                        // Show success message from server or default message
                        showStatus(message != null ? message : "Attendance marked successfully!", true);

                        // Only show details if we have valid data
                        if (data.getAttendanceData() != null) {
                            showAttendanceDetails(data);
                        }

                        // Clear the image after successful submission
                        clearImage();
                    } else {
                        try {
                            // Try to get error message from server
                            String errorBody = response.errorBody() != null ?
                                    response.errorBody().string() : "Unknown error occurred";
                            showStatus("Error: " + errorBody, false);
                        } catch (IOException e) {
                            showStatus("Error marking attendance", false);
                        }
                    }
                }

                @Override
                public void onFailure(Call<AttendanceResponse> call, Throwable t) {
                    submitButton.setEnabled(true); // Re-enable button
                    Log.e(TAG, "API call failed", t);
                    showStatus("Connection error: " + t.getMessage(), false);
                }
            });
        } catch (Exception e) {
            submitButton.setEnabled(true);
            Log.e(TAG, "Error submitting attendance", e);
            showStatus("Error processing request: " + e.getMessage(), false);
        }
    }

    private void clearImage() {
        imageFile = null;
        previewImageView.setImageDrawable(null);
        previewImageView.setVisibility(View.GONE);
    }

    private void showStatus(String message, boolean isSuccess) {
        runOnUiThread(() -> {
            if (statusTextView != null) {
                statusTextView.setText(message);
                statusTextView.setBackgroundResource(isSuccess ?
                        R.color.status_success_background : R.color.status_error_background);
                statusTextView.setVisibility(View.VISIBLE);

                // Auto-hide status after 3 seconds if success
                if (isSuccess) {
                    statusTextView.postDelayed(() -> {
                        if (statusTextView != null) {
                            statusTextView.setVisibility(View.GONE);
                        }
                    }, 3000);
                }
            }
        });
    }

    private void showAttendanceDetails(AttendanceResponse data) {
        if (data == null || data.getAttendanceData() == null) {
            return;
        }

        AttendanceData attendanceData = data.getAttendanceData();

        String details = String.format(
                "Student ID: %s\n" +
                        "Last Attendance: %s\n" +
                        "Total Attendance: %d\n" +
                        "Attendance Type: %s",
                data.getStudentId(),
                attendanceData.getLastAttendanceTime(),
                attendanceData.getTotalAttendance(),
                attendanceData.getAttendanceType()
        );

        runOnUiThread(() -> {
            if (attendanceDetailsText != null) {
                attendanceDetailsText.setText(details);
                attendanceDetailsText.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any pending operations
        if (imageFile != null && imageFile.exists()) {
            imageFile.delete();
        }
    }
}
