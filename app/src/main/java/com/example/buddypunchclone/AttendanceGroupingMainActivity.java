//package com.example.buddypunchclone;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.ActionBarDrawerToggle;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
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
//import java.util.stream.Collectors;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class AttendanceGroupingMainActivity extends AppCompatActivity {
//    private DrawerLayout drawerLayout;
//    private ActionBarDrawerToggle toggle;
//    private NavigationView navigationView;
//    private RecyclerView employeeRecyclerView;
//    private AttendanceUserAdapter employeeAdapter;
//    private List<AttendanceUser> employeeList;
//    private List<String> groupEmployeeUsernames;
//    private BottomNavigationView bottomNavigationView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_attendace_grouping_main);
//
//        setupViews();
//        setupNavigationDrawer();
//        setupBottomNavigation();
//        setupNavigationHeader();
//        setupRecyclerView();
//        fetchGroupEmployeesAndUsers();
//    }
//
//    private void setupViews() {
//        drawerLayout = findViewById(R.id.drawer_layout);
//        navigationView = findViewById(R.id.nav_view);
//        employeeRecyclerView = findViewById(R.id.employeeRecyclerView);
//        bottomNavigationView = findViewById(R.id.bottom_navigation);
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
//    private void setupBottomNavigation() {
//        bottomNavigationView.setOnItemSelectedListener(item -> {
//            int itemId = item.getItemId();
//            if (itemId == R.id.nav_dashboard) {
//                startActivity(new Intent(this, SecondActivity.class));
//                finish();
//                return true;
//            } else if (itemId == R.id.nav_Employees) {
//                startActivity(new Intent(this, EmployeesActivity.class));
//                finish();
//                return true;
//            } else if (itemId == R.id.nav_Attendace) {
//                startActivity(new Intent(this, AttendanceActivity.class));
//                finish();
//                return true;
//            } else if (itemId == R.id.MapId) {
//                startActivity(new Intent(this, GeofencingActivity.class));
//                finish();
//                return true;
//            }
//            return false;
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
//                startActivity(new Intent(this, SecondActivity.class));
//                finish();
//            } else if (itemId == R.id.nav_Employees) {
//                startActivity(new Intent(this, EmployeesActivity.class));
//                finish();
//            } else if (itemId == R.id.MapId) {
//                startActivity(new Intent(this, GeofencingActivity.class));
//                finish();
//            } else if (itemId == R.id.nav_Attendace) {
//                startActivity(new Intent(this, AttendanceActivity.class));
//                finish();
//            } else if (itemId == R.id.nav_logout) {
//                handleLogout();
//            }
//            drawerLayout.closeDrawers();
//            return true;
//        });
//    }
//
//    private void handleLogout() {
//        SharedPreferences prefs = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
//        prefs.edit().clear().apply();
//
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        finish();
//    }
//
//    private void setupRecyclerView() {
//        employeeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        employeeList = new ArrayList<>();
//
//        // Create custom adapter that overrides the click behavior
//        employeeAdapter = new AttendanceUserAdapter(employeeList) {
//            @Override
//            public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
//                super.onBindViewHolder(holder, position);
//
//                // Override click behavior
//                holder.itemView.setOnClickListener(v -> {
//                    // Either do nothing or show a toast
//                    Toast.makeText(v.getContext(),
//                            "Group member: " + employeeList.get(position).getUsername(),
//                            Toast.LENGTH_SHORT).show();
//                });
//            }
//        };
//
//        employeeRecyclerView.setAdapter(employeeAdapter);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (toggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//
//
//    }
//
//    private void fetchGroupEmployeesAndUsers() {
//        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
//        String loggedInUsername = sharedPreferences.getString("logged_username", "");
//
//        ApiService springApiService = RegistrationRetrofitClient.getSpringClient(this)
//                .create(ApiService.class);
//
//        Call<List<GroupResponseDto>> groupCall = springApiService.getGroups();
//        groupCall.enqueue(new Callback<List<GroupResponseDto>>() {
//            @Override
//            public void onResponse(Call<List<GroupResponseDto>> call, Response<List<GroupResponseDto>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    List<GroupResponseDto> groups = response.body();
//
//                    // Find the group where the logged-in user is present and get other group usernames
//                    groupEmployeeUsernames = groups.stream()
//                            .filter(group -> group.getEmployees().stream()
//                                    .anyMatch(emp -> emp.getUsername().equals(loggedInUsername)))
//                            .findFirst()
//                            .map(group -> group.getEmployees().stream()
//                                    .filter(emp -> !emp.getUsername().equals(loggedInUsername))
//                                    .map(EmployeeResponseDto::getUsername)
//                                    .collect(Collectors.toList()))
//                            .orElse(new ArrayList<>());
//
//                    // Fetch users from Django API
//                    fetchDjangoUsers(groupEmployeeUsernames);
//                } else {
//                    Toast.makeText(AttendanceGroupingMainActivity.this,
//                            "Failed to fetch groups", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<GroupResponseDto>> call, Throwable t) {
//                Toast.makeText(AttendanceGroupingMainActivity.this,
//                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void fetchDjangoUsers(List<String> groupUsernames) {
//        AttendanceApi djangoAttendanceApi = RegistrationRetrofitClient.getDjangoClient(this)
//                .create(AttendanceApi.class);
//
//        djangoAttendanceApi.getUsers().enqueue(new Callback<List<AttendanceUser>>() {
//            @Override
//            public void onResponse(Call<List<AttendanceUser>> call, Response<List<AttendanceUser>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    // Filter users based on group usernames (excluding logged-in user)
//                    List<AttendanceUser> filteredUsers = response.body().stream()
//                            .filter(user -> groupUsernames.contains(user.getUsername()))
//                            .collect(Collectors.toList());
//
//                    employeeList.clear();
//                    employeeList.addAll(filteredUsers);
//                    employeeAdapter.notifyDataSetChanged();
//
//                    Toast.makeText(AttendanceGroupingMainActivity.this,
//                            "Group Members: " + filteredUsers.size(),
//                            Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(AttendanceGroupingMainActivity.this,
//                            "Failed to fetch users", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<AttendanceUser>> call, Throwable t) {
//                Toast.makeText(AttendanceGroupingMainActivity.this,
//                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}


package com.example.buddypunchclone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.SearchView;

public class AttendanceGroupingMainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private RecyclerView employeeRecyclerView;
    private AttendanceUserAdapter employeeAdapter;
    private GroupOwnerAdapter groupOwnerAdapter;
    private List<AttendanceUser> employeeList;
    private List<GroupResponseDto> allGroupsList;
    private List<String> groupEmployeeUsernames;
    private BottomNavigationView bottomNavigationView;
    private boolean isAdmin = false;
    private boolean showingGroupMembers = false;
    private String selectedGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendace_grouping_main);

        setupViews();
        setupNavigationDrawer();
        setupBottomNavigation();
        setupNavigationHeader();
        checkUserRole();
        setupRecyclerView();

        if (isAdmin) {
            fetchAllGroups();
            Objects.requireNonNull(getSupportActionBar()).setTitle("All Groups");
        } else {
            fetchGroupEmployeesAndUsers();
            Objects.requireNonNull(getSupportActionBar()).setTitle("My Group Members");
        }
    }

    private void checkUserRole() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        String userRole = sharedPreferences.getString("user_role", "");
        isAdmin = "ROLE_ADMIN".equals(userRole);
    }

    private void setupViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        employeeRecyclerView = findViewById(R.id.employeeRecyclerView);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupNavigationHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView emailTextView = headerView.findViewById(R.id.login_user_email);

        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("user_email", "info@example.com");

        emailTextView.setText(userEmail);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(this, SecondActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_Employees) {
                startActivity(new Intent(this, EmployeesActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_Attendace) {
                startActivity(new Intent(this, AttendanceActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.MapId) {
                startActivity(new Intent(this, GeofencingActivity.class));
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
                startActivity(new Intent(this, SecondActivity.class));
                finish();
            } else if (itemId == R.id.nav_Employees) {
                startActivity(new Intent(this, EmployeesActivity.class));
                finish();
            } else if (itemId == R.id.MapId) {
                startActivity(new Intent(this, GeofencingActivity.class));
                finish();
            } else if (itemId == R.id.nav_Attendace) {
                startActivity(new Intent(this, AttendanceActivity.class));
                finish();
            } else if (itemId == R.id.nav_logout) {
                handleLogout();
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterGroups(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterGroups(newText);
                return false;
            }
        });

        return true;
    }

    private void filterGroups(String query) {
        if (isAdmin && !showingGroupMembers) {
            // Filter groups when admin is viewing all groups
            List<GroupResponseDto> filteredList = new ArrayList<>();
            for (GroupResponseDto group : allGroupsList) {
                // Search by owner name, group ID, or description
                boolean matchesOwner = group.getOwner() != null &&
                        group.getOwner().toString().toLowerCase().contains(query.toLowerCase());
                boolean matchesId = group.getId() != null &&
                        group.getId().toString().contains(query);
                boolean matchesDescription = group.getDescription() != null &&
                        group.getDescription().toLowerCase().contains(query.toLowerCase());

                if (matchesOwner || matchesId || matchesDescription) {
                    filteredList.add(group);
                }
            }

            groupOwnerAdapter.updateData(filteredList);

            if (filteredList.isEmpty()) {
                Toast.makeText(this, "No matching groups found", Toast.LENGTH_SHORT).show();
            }
        } else if (isAdmin && showingGroupMembers) {
            // Filter group members when admin is viewing members of a specific group
            List<AttendanceUser> filteredList = new ArrayList<>();
            for (AttendanceUser user : employeeList) {
                if (user.getUsername() != null &&
                        user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(user);
                }
            }

            List<AttendanceUser> tempList = new ArrayList<>(filteredList);
            employeeList.clear();
            employeeList.addAll(tempList);
            employeeAdapter.notifyDataSetChanged();

            if (filteredList.isEmpty()) {
                Toast.makeText(this, "No matching members found", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Filter group members for regular users
            List<AttendanceUser> filteredList = new ArrayList<>();
            for (AttendanceUser user : employeeList) {
                if (user.getUsername() != null &&
                        user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(user);
                }
            }

            List<AttendanceUser> tempList = new ArrayList<>(filteredList);
            employeeList.clear();
            employeeList.addAll(tempList);
            employeeAdapter.notifyDataSetChanged();

            if (filteredList.isEmpty()) {
                Toast.makeText(this, "No matching members found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleLogout() {
        SharedPreferences prefs = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void setupRecyclerView() {
        employeeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (isAdmin) {
            allGroupsList = new ArrayList<>();
            groupOwnerAdapter = new GroupOwnerAdapter(allGroupsList, groupId -> {
                // When a group owner is clicked, show its members
                selectedGroupId = String.valueOf(groupId); // Convert Long to String
                showingGroupMembers = true;
                Objects.requireNonNull(getSupportActionBar()).setTitle("Group Members");
                fetchGroupMembers(groupId);
            });
            employeeRecyclerView.setAdapter(groupOwnerAdapter);
        } else {
            employeeList = new ArrayList<>();
            employeeAdapter = new AttendanceUserAdapter(employeeList) {
                @Override
                public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
                    super.onBindViewHolder(holder, position);

                    // Override click behavior
                    holder.itemView.setOnClickListener(v -> {
                        // Either do nothing or show a toast
                        Toast.makeText(v.getContext(),
                                "Group member: " + employeeList.get(position).getUsername(),
                                Toast.LENGTH_SHORT).show();
                    });
                }
            };
            employeeRecyclerView.setAdapter(employeeAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home && isAdmin && showingGroupMembers) {
            // Back button pressed and showing members, go back to groups list
            showingGroupMembers = false;
            Objects.requireNonNull(getSupportActionBar()).setTitle("All Groups");
            employeeRecyclerView.setAdapter(groupOwnerAdapter);
            return true;
        } else if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isAdmin && showingGroupMembers) {
            // Go back to groups list
            showingGroupMembers = false;
            Objects.requireNonNull(getSupportActionBar()).setTitle("All Groups");
            employeeRecyclerView.setAdapter(groupOwnerAdapter);
        } else {
            super.onBackPressed();
        }
    }

    private void fetchAllGroups() {
        ApiService springApiService = RegistrationRetrofitClient.getSpringClient(this)
                .create(ApiService.class);

        Call<List<GroupResponseDto>> groupCall = springApiService.getGroups();
        groupCall.enqueue(new Callback<List<GroupResponseDto>>() {
            @Override
            public void onResponse(Call<List<GroupResponseDto>> call, Response<List<GroupResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allGroupsList.clear();
                    allGroupsList.addAll(response.body());
                    groupOwnerAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AttendanceGroupingMainActivity.this,
                            "Failed to fetch groups", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<GroupResponseDto>> call, Throwable t) {
                Toast.makeText(AttendanceGroupingMainActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchGroupMembers(Long groupId) {
        // First get the usernames from the selected group
        List<String> memberUsernames = allGroupsList.stream()
                .filter(group -> group.getId().equals(groupId))
                .findFirst()
                .map(group -> group.getEmployees().stream()
                        .map(EmployeeResponseDto::getUsername)
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());

        // Then fetch the actual user details for those usernames
        fetchUserDetails(memberUsernames);
    }

    private void fetchUserDetails(List<String> usernames) {
        AttendanceApi djangoAttendanceApi = RegistrationRetrofitClient.getDjangoClient(this)
                .create(AttendanceApi.class);

        djangoAttendanceApi.getUsers().enqueue(new Callback<List<AttendanceUser>>() {
            @Override
            public void onResponse(Call<List<AttendanceUser>> call, Response<List<AttendanceUser>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Filter users based on usernames
                    List<AttendanceUser> filteredUsers = response.body().stream()
                            .filter(user -> usernames.contains(user.getUsername()))
                            .collect(Collectors.toList());

                    // Create and set adapter for members
                    employeeList = new ArrayList<>(filteredUsers);
                    employeeAdapter = new AttendanceUserAdapter(employeeList) {
                        @Override
                        public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
                            super.onBindViewHolder(holder, position);

                            // Override click behavior
                            holder.itemView.setOnClickListener(v -> {
                                // Either do nothing or show a toast
                                Toast.makeText(v.getContext(),
                                        "Group member: " + employeeList.get(position).getUsername(),
                                        Toast.LENGTH_SHORT).show();
                            });
                        }
                    };
                    employeeRecyclerView.setAdapter(employeeAdapter);

                    Toast.makeText(AttendanceGroupingMainActivity.this,
                            "Group Members: " + filteredUsers.size(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AttendanceGroupingMainActivity.this,
                            "Failed to fetch users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AttendanceUser>> call, Throwable t) {
                Toast.makeText(AttendanceGroupingMainActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchGroupEmployeesAndUsers() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        String loggedInUsername = sharedPreferences.getString("logged_username", "");

        ApiService springApiService = RegistrationRetrofitClient.getSpringClient(this)
                .create(ApiService.class);

        Call<List<GroupResponseDto>> groupCall = springApiService.getGroups();
        groupCall.enqueue(new Callback<List<GroupResponseDto>>() {
            @Override
            public void onResponse(Call<List<GroupResponseDto>> call, Response<List<GroupResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GroupResponseDto> groups = response.body();

                    // Find the group where the logged-in user is present and get other group usernames
                    groupEmployeeUsernames = groups.stream()
                            .filter(group -> group.getEmployees().stream()
                                    .anyMatch(emp -> emp.getUsername().equals(loggedInUsername)))
                            .findFirst()
                            .map(group -> group.getEmployees().stream()
                                    .filter(emp -> !emp.getUsername().equals(loggedInUsername))
                                    .map(EmployeeResponseDto::getUsername)
                                    .collect(Collectors.toList()))
                            .orElse(new ArrayList<>());

                    // Fetch users from Django API
                    fetchDjangoUsers(groupEmployeeUsernames);
                } else {
                    Toast.makeText(AttendanceGroupingMainActivity.this,
                            "Failed to fetch groups", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<GroupResponseDto>> call, Throwable t) {
                Toast.makeText(AttendanceGroupingMainActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDjangoUsers(List<String> groupUsernames) {
        AttendanceApi djangoAttendanceApi = RegistrationRetrofitClient.getDjangoClient(this)
                .create(AttendanceApi.class);

        djangoAttendanceApi.getUsers().enqueue(new Callback<List<AttendanceUser>>() {
            @Override
            public void onResponse(Call<List<AttendanceUser>> call, Response<List<AttendanceUser>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Filter users based on group usernames (excluding logged-in user)
                    List<AttendanceUser> filteredUsers = response.body().stream()
                            .filter(user -> groupUsernames.contains(user.getUsername()))
                            .collect(Collectors.toList());

                    employeeList.clear();
                    employeeList.addAll(filteredUsers);
                    employeeAdapter.notifyDataSetChanged();

                    Toast.makeText(AttendanceGroupingMainActivity.this,
                            "Group Members: " + filteredUsers.size(),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AttendanceGroupingMainActivity.this,
                            "Failed to fetch users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AttendanceUser>> call, Throwable t) {
                Toast.makeText(AttendanceGroupingMainActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}