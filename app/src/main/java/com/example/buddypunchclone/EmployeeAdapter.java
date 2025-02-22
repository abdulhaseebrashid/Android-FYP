package com.example.buddypunchclone;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// EmployeeAdapter.java
public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private final List<EmployeeResponseDto> employeeList;
    private final Context context;
    private Map<String, AttendanceResponse> attendanceMap;

    public EmployeeAdapter(Context context, List<EmployeeResponseDto> employeeList, Map<String, AttendanceResponse> attendanceMap) {
        this.context = context;
        this.employeeList = employeeList;
        this.attendanceMap = attendanceMap;
    }


    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.employee_item, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        EmployeeResponseDto employee = employeeList.get(position);

        String fullName = employee.getFirstName() + " " + employee.getLastName();
        holder.nameTextView.setText(fullName);
        holder.emailTextView.setText(employee.getEmail());
        holder.phoneTextView.setText(employee.getPhoneNumber());
        holder.userTypeTextView.setText(employee.getUserType());

        // Update attendance status and card color
        updateAttendanceStatus(holder, employee);

        // Click listener to open EmployeeProfileActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EmployeeProfileActivity.class);
            intent.putExtra("employee_id", employee.getId());
            intent.putExtra("firstName", employee.getFirstName());
            intent.putExtra("lastName", employee.getLastName());
            intent.putExtra("email", employee.getEmail());
            intent.putExtra("phoneNumber", employee.getPhoneNumber());
            intent.putExtra("username", employee.getUsername());
            intent.putExtra("userType", employee.getUserType());
            intent.putExtra("birthDate", employee.getBirthDate());
            intent.putExtra("hireDate", employee.getHireDate());
            intent.putExtra("terminationDate", employee.getTerminationDate());
            intent.putExtra("annualSalary", employee.getAnnualSalary());
            intent.putExtra("additionalInfo", employee.getAdditionalInfo());

            context.startActivity(intent);
        });
    }


    private void updateAttendanceStatus(@NonNull EmployeeViewHolder holder, EmployeeResponseDto employee) {
        MaterialCardView cardView = (MaterialCardView) holder.itemView;
        TextView statusTextView = holder.attendanceStatusTextView;

        // Get attendance data from the map using employee username
        AttendanceResponse attendanceResponse = attendanceMap.get(employee.getUsername());

        if (hasMarkedAttendanceToday(attendanceResponse)) {
            // Green color for marked attendance
            cardView.setCardBackgroundColor(context.getColor(R.color.attendance_marked_green));
            statusTextView.setText("✓ Today's Attendance Marked");
            statusTextView.setTextColor(context.getColor(android.R.color.white));
            updateTextColors(holder, android.R.color.white);
        } else {
            // Red color for pending attendance
            cardView.setCardBackgroundColor(context.getColor(R.color.attendance_pending_red));
            statusTextView.setText("✗ Today's Attendance Pending");
            statusTextView.setTextColor(context.getColor(android.R.color.white));
            updateTextColors(holder, android.R.color.white);
        }
    }

    private void updateTextColors(EmployeeViewHolder holder, int colorResId) {
        holder.nameTextView.setTextColor(context.getColor(colorResId));
        holder.emailTextView.setTextColor(context.getColor(colorResId));
        holder.phoneTextView.setTextColor(context.getColor(colorResId));
        holder.userTypeTextView.setTextColor(context.getColor(colorResId));
    }

    private boolean hasMarkedAttendanceToday(AttendanceResponse attendanceResponse) {
        if (attendanceResponse == null ||
                attendanceResponse.getAttendanceData() == null ||
                attendanceResponse.getAttendanceData().getLastAttendanceTime() == null) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());
        String lastAttendance = attendanceResponse.getAttendanceData().getLastAttendanceTime().split(" ")[0];

        return today.equals(lastAttendance);
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public void updateAttendanceData(Map<String, AttendanceResponse> newAttendanceMap) {
        this.attendanceMap = newAttendanceMap;
        notifyDataSetChanged();
    }

    static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, emailTextView, phoneTextView, userTypeTextView, attendanceStatusTextView;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            phoneTextView = itemView.findViewById(R.id.phoneTextView);
            userTypeTextView = itemView.findViewById(R.id.userTypeTextView);
            attendanceStatusTextView = itemView.findViewById(R.id.attendanceStatusTextView);
        }
    }
}