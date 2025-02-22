//package com.example.buddypunchclone;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.ActionBarDrawerToggle;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.android.material.navigation.NavigationView;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class EmployeesActivity extends AppCompatActivity {
//
//    private static final String TAG = "EmployeesActivity";
//
//    private RecyclerView recyclerView;
//    private EmployeeAdapter employeeAdapter;
//    private List<EmployeeResponseDto> employeeList;
//    private ProgressBar progressBar;
//    private TextView errorTextView;
//    private ImageButton plusButton;
//    private BottomNavigationView bottomNavigationView;
//    private DrawerLayout drawerLayout;
//    private NavigationView navigationView;
//    private ActionBarDrawerToggle toggle;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_employees);
//
//        // Initialize views
//        initializeViews();
//
//        // Setup Navigation Drawer
//        setupNavigationDrawer();
//
//        // Setup RecyclerView
//        setupRecyclerView();
//
//        // Check user role and set plus button visibility
//        checkRoleAndSetVisibility();
//
//        // Setup plus button click listener
//        setupPlusButton();
//
//        // Setup bottom navigation
//        setupBottomNavigation();
//
//        // Setup navigation header
//        setupNavigationHeader();
//
//        // Fetch employee data
//        fetchEmployeeData();
//    }
//
//    private void initializeViews() {
//        recyclerView = findViewById(R.id.recyclerView);
//        progressBar = findViewById(R.id.progressBar);
//        errorTextView = findViewById(R.id.errorTextView);
//        plusButton = findViewById(R.id.plus_button);
//        bottomNavigationView = findViewById(R.id.bottom_navigation);
//        drawerLayout = findViewById(R.id.drawer_layout);
//        navigationView = findViewById(R.id.nav_view);
//    }
//
//    private void setupRecyclerView() {
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        employeeList = new ArrayList<>();
//        employeeAdapter = new EmployeeAdapter(this, employeeList);
//        recyclerView.setAdapter(employeeAdapter);
//    }
//
//    private void setupPlusButton() {
//        plusButton.setOnClickListener(v -> {
//            Intent intent = new Intent(EmployeesActivity.this, AddEmployeeActivity.class);
//            startActivity(intent);
//        });
//    }
//
//    private void setupNavigationDrawer() {
//        toggle = new ActionBarDrawerToggle(this, drawerLayout,
//                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//
//        navigationView.setNavigationItemSelectedListener(item -> {
//            int itemId = item.getItemId();
//            if (itemId == R.id.nav_dashboard) {
//                startActivity(new Intent(EmployeesActivity.this, SecondActivity.class));
//                finish();
//            } else if (itemId == R.id.nav_Employees) {
//                drawerLayout.closeDrawers();
//            } else if (itemId == R.id.MapId) {
//                startActivity(new Intent(EmployeesActivity.this, GeofencingActivity.class));
//                finish();
//            } else if (itemId == R.id.nav_Attendace) {
//                startActivity(new Intent(EmployeesActivity.this, AttendanceActivity.class));
//                finish();
//            } else if (itemId == R.id.nav_grouping_attendance) {
//                startActivity(new Intent(EmployeesActivity.this, AttendanceGroupingMainActivity.class));
//            } else if (itemId == R.id.nav_logout) {
//                handleLogout();
//            }
//            drawerLayout.closeDrawers();
//            return true;
//        });
//    }
//
//    private void setupNavigationHeader() {
//        View headerView = navigationView.getHeaderView(0);
//        TextView emailTextView = headerView.findViewById(R.id.login_user_email);
//
//        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
//        String userEmail = sharedPreferences.getString("user_email", "info@example.com");
//
//        emailTextView.setText(userEmail);
//    }
//
//    private void handleLogout() {
//        // Clear SharedPreferences
//        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear();
//        editor.apply();
//
//        // Navigate to login activity
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish();
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
//    }
//
//    private void setupBottomNavigation() {
//        bottomNavigationView.setSelectedItemId(R.id.nav_Employees);
//
//        bottomNavigationView.setOnItemSelectedListener(item -> {
//            int itemId = item.getItemId();
//            if (itemId == R.id.nav_dashboard) {
//                finish();
//                return true;
//            } else if (itemId == R.id.nav_Employees) {
//                return true;
//            } else if (itemId == R.id.nav_Attendace) {
//                startActivity(new Intent(this, AttendanceActivity.class));
//                finish();
//            } else if (itemId == R.id.MapId) {
//                startActivity(new Intent(this, GeofencingActivity.class));
//                finish();
//            }
//            return true;
//        });
//    }
//
//    private void checkRoleAndSetVisibility() {
//        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
//        String userRole = sharedPreferences.getString("user_role", "");
//
//        // Show plus button only for ROLE_ADMIN
//        plusButton.setVisibility("ROLE_ADMIN".equals(userRole) ? View.VISIBLE : View.GONE);
//    }
//
//    private void fetchEmployeeData() {
//        progressBar.setVisibility(View.VISIBLE);
//        errorTextView.setVisibility(View.GONE);
//
//        AddEmployeeApiService apiService = RegistrationRetrofitClient.getSpringClient(this).create(AddEmployeeApiService.class);
//        Call<List<EmployeeResponseDto>> call = apiService.getAllEmployees();
//
//        call.enqueue(new Callback<List<EmployeeResponseDto>>() {
//            @Override
//            public void onResponse(Call<List<EmployeeResponseDto>> call, Response<List<EmployeeResponseDto>> response) {
//                progressBar.setVisibility(View.GONE);
//
//                try {
//                    if (response.isSuccessful() && response.body() != null) {
//                        List<EmployeeResponseDto> employees = response.body();
//
//                        if (!employees.isEmpty()) {
//                            // Filter employees based on user role
//                            filterAndDisplayEmployees(employees);
//                        } else {
//                            showError("No employees found");
//                        }
//                    } else {
//                        String errorMessage = "Failed to load employees. Error code: " + response.code();
//                        showError(errorMessage);
//                    }
//                } catch (Exception e) {
//                    Log.e(TAG, "Error processing response", e);
//                    showError("Error processing employee data");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<EmployeeResponseDto>> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
//                String errorMessage = "Error: " + t.getMessage();
//                showError(errorMessage);
//            }
//        });
//    }
//
//    private void filterAndDisplayEmployees(List<EmployeeResponseDto> employees) {
//        SharedPreferences prefs = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
//        String userRole = prefs.getString("user_role", "");
//        String loggedUsername = prefs.getString("logged_username", "");
//
//        employeeList.clear();
//
//        if ("ROLE_ADMIN".equals(userRole)) {
//            // Show all employees for admin
//            employeeList.addAll(employees);
//        } else {
//            // Show only the logged-in user's data for non-admin
//            for (EmployeeResponseDto employee : employees) {
//                if (loggedUsername.equals(employee.getUsername())) {
//                    employeeList.add(employee);
//                    break;
//                }
//            }
//        }
//
//        employeeAdapter.notifyDataSetChanged();
//
//        // Show error if no matching employees found
//        if (employeeList.isEmpty()) {
//            showError("No matching employees found");
//        }
//    }
//
//    private void showError(String message) {
//        errorTextView.setText(message);
//        errorTextView.setVisibility(View.VISIBLE);
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        checkRoleAndSetVisibility();
//        if (bottomNavigationView != null) {
//            bottomNavigationView.setSelectedItemId(R.id.nav_Employees);
//        }
//        // Refresh employee data when returning to the activity
//        fetchEmployeeData();
//    }
//}






package com.example.buddypunchclone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeesActivity extends AppCompatActivity {

    private static final String TAG = "EmployeesActivity";

    private RecyclerView recyclerView;
    private EmployeeAdapter employeeAdapter;
    private List<EmployeeResponseDto> employeeList;
    private List<EmployeeResponseDto> filteredEmployeeList;
    private Map<String, AttendanceResponse> attendanceMap;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private ImageButton plusButton;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees);

        initializeViews();
        setupNavigationDrawer();
        setupRecyclerView();
        checkRoleAndSetVisibility();
        setupPlusButton();
        setupBottomNavigation();
        setupNavigationHeader();
        fetchEmployeeData();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);
        plusButton = findViewById(R.id.plus_button);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        employeeList = new ArrayList<>();
        filteredEmployeeList = new ArrayList<>();
        attendanceMap = new HashMap<>();
        employeeAdapter = new EmployeeAdapter(this, filteredEmployeeList, attendanceMap);
        recyclerView.setAdapter(employeeAdapter);
    }


    private void setupPlusButton() {
        plusButton.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeesActivity.this, AddEmployeeActivity.class);
            startActivity(intent);
        });
    }

    private void setupNavigationDrawer() {
        toggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(EmployeesActivity.this, SecondActivity.class));
                finish();
            } else if (itemId == R.id.nav_Employees) {
                drawerLayout.closeDrawers();
            } else if (itemId == R.id.MapId) {
                startActivity(new Intent(EmployeesActivity.this, GeofencingActivity.class));
                finish();
            } else if (itemId == R.id.nav_Attendace) {
                startActivity(new Intent(EmployeesActivity.this, AttendanceActivity.class));
                finish();
            } else if (itemId == R.id.nav_grouping_attendance) {
                startActivity(new Intent(EmployeesActivity.this, AttendanceGroupingMainActivity.class));
            } else if (itemId == R.id.nav_logout) {
                handleLogout();
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void setupNavigationHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView emailTextView = headerView.findViewById(R.id.login_user_email);

        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("user_email", "info@example.com");

        emailTextView.setText(userEmail);
    }

    private void handleLogout() {
        // Clear SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Navigate to login activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_Employees);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                finish();
                return true;
            } else if (itemId == R.id.nav_Employees) {
                return true;
            } else if (itemId == R.id.nav_Attendace) {
                startActivity(new Intent(this, AttendanceActivity.class));
                finish();
            } else if (itemId == R.id.MapId) {
                startActivity(new Intent(this, GeofencingActivity.class));
                finish();
            }
            return true;
        });
    }

    private void checkRoleAndSetVisibility() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        String userRole = sharedPreferences.getString("user_role", "");

        // Show plus button only for ROLE_ADMIN
        plusButton.setVisibility("ROLE_ADMIN".equals(userRole) ? View.VISIBLE : View.GONE);
    }

    private void fetchEmployeeData() {
        progressBar.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.GONE);

        AddEmployeeApiService apiService = RegistrationRetrofitClient.getSpringClient(this).create(AddEmployeeApiService.class);
        Call<List<EmployeeResponseDto>> call = apiService.getAllEmployees();

        call.enqueue(new Callback<List<EmployeeResponseDto>>() {
            @Override
            public void onResponse(Call<List<EmployeeResponseDto>> call, Response<List<EmployeeResponseDto>> response) {
                progressBar.setVisibility(View.GONE);

                try {
                    if (response.isSuccessful() && response.body() != null) {
                        List<EmployeeResponseDto> employees = response.body();

                        if (!employees.isEmpty()) {
                            filterAndDisplayEmployees(employees);
                            // Fetch attendance for each employee
                            for (EmployeeResponseDto employee : employees) {
                                fetchAttendanceForEmployee(employee.getUsername());
                            }
                        } else {
                            showError("No employees found");
                        }
                    } else {
                        showError("Failed to load employees. Error code: " + response.code());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing response", e);
                    showError("Error processing employee data");
                }
            }

            @Override
            public void onFailure(Call<List<EmployeeResponseDto>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Error: " + t.getMessage());
            }
        });
    }

    private void fetchAttendanceForEmployee(String username) {
        // Try Django API first
        ApiService djangoApi = RegistrationRetrofitClient.getDjangoClient(this).create(ApiService.class);
        Call<AttendanceUser> djangoCall = djangoApi.getUser(username);

        djangoCall.enqueue(new Callback<AttendanceUser>() {
            @Override
            public void onResponse(Call<AttendanceUser> call, Response<AttendanceUser> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AttendanceUser user = response.body();
                    updateAttendanceMapWithUser(username, user);
                } else {
                    // If Django API fails, try Spring Boot API
                    fetchFromSpringBootApi(username);
                }
            }

            @Override
            public void onFailure(Call<AttendanceUser> call, Throwable t) {
                // If Django API fails, try Spring Boot API
                fetchFromSpringBootApi(username);
            }
        });
    }
    private void fetchFromSpringBootApi(String username) {
        ApiService springApi = RegistrationRetrofitClient.getSpringClient(this).create(ApiService.class);
        Call<AttendanceUser> springCall = springApi.getUser(username);

        springCall.enqueue(new Callback<AttendanceUser>() {
            @Override
            public void onResponse(Call<AttendanceUser> call, Response<AttendanceUser> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AttendanceUser user = response.body();
                    updateAttendanceMapWithUser(username, user);
                }
            }

            @Override
            public void onFailure(Call<AttendanceUser> call, Throwable t) {
                Log.e(TAG, "Error fetching attendance for user: " + username, t);
            }
        });
    }
    private void updateAttendanceMapWithUser(String username, AttendanceUser user) {
        if (user != null && user.getLastAttendanceTime() != null) {
            AttendanceResponse attendanceResponse = new AttendanceResponse();
            AttendanceData attendanceData = new AttendanceData();
            attendanceData.setLastAttendanceTime(user.getLastAttendanceTime());
            attendanceResponse.setAttendanceData(attendanceData);
            attendanceMap.put(username, attendanceResponse);
            employeeAdapter.updateAttendanceData(attendanceMap);
        }
    }

    private void filterAndDisplayEmployees(List<EmployeeResponseDto> employees) {
        SharedPreferences prefs = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        String userRole = prefs.getString("user_role", "");
        String loggedUsername = prefs.getString("logged_username", "");

        employeeList.clear();

        if ("ROLE_ADMIN".equals(userRole)) {
            employeeList.addAll(employees);
        } else {
            for (EmployeeResponseDto employee : employees) {
                if (loggedUsername.equals(employee.getUsername())) {
                    employeeList.add(employee);
                    break;
                }
            }
        }

        filteredEmployeeList.clear();
        filteredEmployeeList.addAll(employeeList);  // Initially, no filtering
        employeeAdapter.notifyDataSetChanged();

        if (filteredEmployeeList.isEmpty()) {
            showError("No matching employees found");
        }
    }

    private void showError(String message) {
        errorTextView.setText(message);
        errorTextView.setVisibility(View.VISIBLE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Handle SearchView filter
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu); // Ensure menu is loaded
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        // Set query text listener to filter employees based on the username
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterEmployees(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterEmployees(newText);
                return false;
            }
        });

        return true;
    }

    private void filterEmployees(String query) {
        List<EmployeeResponseDto> filteredList = new ArrayList<>();
        for (EmployeeResponseDto employee : employeeList) {
            if (employee.getUsername().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(employee);
            }
        }

        filteredEmployeeList.clear();
        filteredEmployeeList.addAll(filteredList);
        employeeAdapter.notifyDataSetChanged();

        if (filteredEmployeeList.isEmpty()) {
            showError("No matching employees found");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkRoleAndSetVisibility();
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_Employees);
        }
        fetchEmployeeData(); // This will also trigger attendance fetching
    }
}
