package com.example.buddypunchclone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AttendanceActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private AttendanceUserAdapter userAdapter;
    private Button trackAttendanceButton;
    private List<AttendanceUser> userList;
    private List<AttendanceUser> filteredUserList;  // List for filtered attendance users

//    Button Code
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendace);

        setupViews();
        setupNavigationDrawer();
        setupBottomNavigation();
        setupNavigationHeader();
        setupRecyclerView();
        fetchAttendanceData();

//        Button Code
        setupTrackAttendanceButton();
        animateTrackAttendanceButton();
    }

    private void setupViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

//    Button Code
        trackAttendanceButton = findViewById(R.id.trackAttendanceButton);
    }
//    Button Code
    private void setupTrackAttendanceButton() {
        trackAttendanceButton.setOnClickListener(v -> {
            Toast.makeText(AttendanceActivity.this, "Tracking Attendance Records...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AttendanceActivity.this, AttendanceTrackingActivity.class));
        });
    }
//    Button Code
    private void animateTrackAttendanceButton() {
        Animation fadeInBounce = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        trackAttendanceButton.setVisibility(View.VISIBLE);
        trackAttendanceButton.startAnimation(fadeInBounce);
    }

    private void setupNavigationHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView emailTextView = headerView.findViewById(R.id.login_user_email);

        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("user_email", "info@example.com");

        emailTextView.setText(userEmail);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_Attendace);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(AttendanceActivity.this, SecondActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_Employees) {
                startActivity(new Intent(AttendanceActivity.this, EmployeesActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_Attendace) {
                return true; // Already on Attendance
            } else if (itemId == R.id.MapId) {
                startActivity(new Intent(AttendanceActivity.this, GeofencingActivity.class));
                finish();
                return true;
            }
            return false;
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
                startActivity(new Intent(AttendanceActivity.this, SecondActivity.class));
                finish();
            } else if (itemId == R.id.nav_Employees) {
                startActivity(new Intent(AttendanceActivity.this, EmployeesActivity.class));
                finish();
            } else if (itemId == R.id.MapId) {
                startActivity(new Intent(AttendanceActivity.this, GeofencingActivity.class));
                finish();
            } else if (itemId == R.id.nav_Attendace) {
                drawerLayout.closeDrawers();
            }  else if (itemId == R.id.nav_grouping_attendance) {
                startActivity(new Intent(AttendanceActivity.this, AttendanceGroupingMainActivity.class));
            } else if (itemId == R.id.nav_logout) {
                handleLogout();
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void setupRecyclerView() {
        userList = new ArrayList<>();
        filteredUserList = new ArrayList<>(); // Initialize filtered list
        userAdapter = new AttendanceUserAdapter(filteredUserList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);
    }

    private void fetchAttendanceData() {
        progressBar.setVisibility(View.VISIBLE);

        try {
            // Try fetching from Django API first
            AttendanceApi djangoApi = RegistrationRetrofitClient.getDjangoClient(this)
                    .create(AttendanceApi.class);
            Call<List<AttendanceUser>> djangoCall = djangoApi.getUsers();

            djangoCall.enqueue(new Callback<List<AttendanceUser>>() {
                @Override
                public void onResponse(Call<List<AttendanceUser>> call, Response<List<AttendanceUser>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        handleSuccessfulResponse(response.body());
                    } else {
                        // If Django API fails, try Spring Boot API
                        fetchFromSpringBootApi();
                    }
                }

                @Override
                public void onFailure(Call<List<AttendanceUser>> call, Throwable t) {
                    // If Django API fails, try Spring Boot API
                    fetchFromSpringBootApi();
                }
            });
        } catch (Exception e) {
            fetchFromSpringBootApi();
        }
    }

    private void fetchFromSpringBootApi() {
        AttendanceApi springApi = RegistrationRetrofitClient.getSpringClient(this)
                .create(AttendanceApi.class);
        Call<List<AttendanceUser>> springCall = springApi.getUsers();

        springCall.enqueue(new Callback<List<AttendanceUser>>() {
            @Override
            public void onResponse(Call<List<AttendanceUser>> call, Response<List<AttendanceUser>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleSuccessfulResponse(response.body());
                } else {
                    handleError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<AttendanceUser>> call, Throwable t) {
                handleError("Error fetching data: " + t.getMessage());
            }
        });
    }

    private void handleSuccessfulResponse(List<AttendanceUser> users) {
        progressBar.setVisibility(View.GONE);

        // Get the logged-in username and user role
        SharedPreferences prefs = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        String loggedUsername = prefs.getString("logged_username", "");
        String userRole = prefs.getString("user_role", "");

        // Filter users
        List<AttendanceUser> filteredUsers = new ArrayList<>();

        // If admin, show all users
        if ("ROLE_ADMIN".equals(userRole)) {
            filteredUsers.addAll(users);
        } else {
            // For non-admin, show only the logged-in user's card
            for (AttendanceUser user : users) {
                if (loggedUsername.equals(user.getUsername())) {
                    filteredUsers.add(user);
                    break;
                }
            }
        }

        userList.clear();
        userList.addAll(filteredUsers);
        filteredUserList.clear();
        filteredUserList.addAll(userList);  // Initially, no filtering
        userAdapter.notifyDataSetChanged();
    }

    private void handleError(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        showErrorMessage(errorMessage);
    }

    private void handleLogout() {
        SharedPreferences prefs = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_Attendace);
        }
    }

    // Handle SearchView filter
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu); // Ensure menu is loaded
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        // Set query text listener to filter users based on the username
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterAttendanceUsers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterAttendanceUsers(newText);
                return false;
            }
        });

        return true;
    }

    private void filterAttendanceUsers(String query) {
        List<AttendanceUser> filteredList = new ArrayList<>();
        for (AttendanceUser user : userList) {
            if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }

        filteredUserList.clear();
        filteredUserList.addAll(filteredList);
        userAdapter.notifyDataSetChanged();
    }
}
