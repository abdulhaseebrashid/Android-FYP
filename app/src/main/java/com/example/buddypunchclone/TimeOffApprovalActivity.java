//package com.example.buddypunchclone;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.MenuItem;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.ActionBarDrawerToggle;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.drawerlayout.widget.DrawerLayout;
//import com.google.android.material.navigation.NavigationView;
//import java.util.Objects;
//
//public class TimeOffApprovalActivity extends AppCompatActivity {
//
//    private DrawerLayout drawerLayout;
//    private ActionBarDrawerToggle toggle;
//    private NavigationView navigationView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_time_of_approval);
//
//        drawerLayout = findViewById(R.id.drawer_layout);
//        navigationView = findViewById(R.id.nav_view);
//
//        // Setup drawer toggle
//        toggle = new ActionBarDrawerToggle(this, drawerLayout,
//                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//
//        // Set up NavigationView item selection listener
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                int itemId = item.getItemId();
//                if (itemId == R.id.nav_dashboard) {
//                    startActivity(new Intent(TimeOffApprovalActivity.this, SecondActivity.class)
//                            .putExtra("fragment", "dashboard"));
//                } else if (itemId == R.id.nav_schedules) {
//                    startActivity(new Intent(TimeOffApprovalActivity.this, SecondActivity.class)
//                            .putExtra("fragment", "schedules"));
//                } else if (itemId == R.id.MapId) {
//                    startActivity(new Intent(TimeOffApprovalActivity.this, MapActivity.class));
//                } else if (itemId == R.id.punchApproval) {
//                    startActivity(new Intent(TimeOffApprovalActivity.this, PunchApprovalActivity.class));
//                } else if (itemId == R.id.timeOfApproval) {
//                    startActivity(new Intent(TimeOffApprovalActivity.this, TimeOffApprovalActivity.class));
//                } else if (itemId == R.id.timeOff) {
//                    startActivity(new Intent(TimeOffApprovalActivity.this, TimeOffActivity.class));
//                } else if (itemId == R.id.timeCard) {
//                    startActivity(new Intent(TimeOffApprovalActivity.this, TimeCardActivity.class));
//                } else if (itemId == R.id.nav_notifications) {
//                    startActivity(new Intent(TimeOffApprovalActivity.this, MyNotificationActivity.class));
//                } else if (itemId == R.id.nav_Employees) {
//                    startActivity(new Intent(TimeOffApprovalActivity.this, EmployeesActivity.class));
//                } else if (itemId == R.id.nav_settings) {
//                    startActivity(new Intent(TimeOffApprovalActivity.this, SettingsActivity.class));
//                } else if (itemId == R.id.nav_logout) {
//                    // Handle logout
//                    Intent intent = new Intent(TimeOffApprovalActivity.this, MainActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    finish(); // Optional: finish the current activity to remove it from the stack
//                }
//                drawerLayout.closeDrawers();
//                return true;
//            }
//        });
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (toggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//}
