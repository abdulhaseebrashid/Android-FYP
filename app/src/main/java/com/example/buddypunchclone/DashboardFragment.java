package com.example.buddypunchclone;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardFragment extends Fragment {
    private CardView attendanceCard;
    private CardView employeesCard;
    private CardView geoFenceCard;
    private CardView liveFeedCard;
    private CardView selfAttendanceCard;
    private CardView attendanceGroupCard;
    private BottomNavigationView bottomNavigationView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize all card views and bottom navigation
        initializeViews(view);

        // Check role and handle LiveFeed visibility
        checkRoleAndSetVisibility();

        // Setup all click listeners for cards
        setupAllCardListeners();

        // Setup bottom navigation listener
        setupBottomNavigation();

        // Animate cards on entry
        animateCardsOnEntry();

        return view;
    }

    private void initializeViews(View view) {
        selfAttendanceCard = view.findViewById(R.id.selfAttendanceCard);
        attendanceCard = view.findViewById(R.id.attendanceCard);
        employeesCard = view.findViewById(R.id.employeesCard);
        geoFenceCard = view.findViewById(R.id.geoFenceCard);
        liveFeedCard = view.findViewById(R.id.liveFeedCard);
        attendanceGroupCard = view.findViewById(R.id.attendanceGroupCard);
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                return true; // Already on dashboard
            } else if (itemId == R.id.nav_Employees) {
                startActivity(new Intent(getActivity(), EmployeesActivity.class));
            } else if (itemId == R.id.nav_Attendace) {
                startActivity(new Intent(getActivity(), AttendanceActivity.class));
            } else if (itemId == R.id.MapId) {
                startActivity(new Intent(getActivity(), GeofencingActivity.class));
            } else if (itemId == R.id.nav_grouping_attendance) {
                startActivity(new Intent(getActivity(), AttendanceGroupingMainActivity.class));
            }
            return true;
        });
    }

    private void checkRoleAndSetVisibility() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        String userRole = sharedPreferences.getString("user_role", "");

        // Show LiveFeed only for ROLE_ADMIN
        if ("ROLE_ADMIN".equals(userRole)) {
            liveFeedCard.setVisibility(View.VISIBLE);
        } else {
            liveFeedCard.setVisibility(View.GONE);
        }
    }

    private void setupAllCardListeners() {
        setupCardClickListener(selfAttendanceCard, AttendanceMarkActivity.class);
        setupCardClickListener(attendanceCard, AttendanceActivity.class);
        setupCardClickListener(employeesCard, EmployeesActivity.class);
        setupCardClickListener(geoFenceCard, GeofencingActivity.class);
        setupCardClickListener(liveFeedCard, IPSLiveFeedMainActivity.class);
        setupCardClickListener(attendanceGroupCard,AttendanceGroupingMainActivity.class);
    }

    private void setupCardClickListener(CardView card, Class<?> destinationActivity) {
        card.setOnClickListener(v -> {
            animateCardClick(card);
            Intent intent = new Intent(getActivity(), destinationActivity);
            startActivity(intent);

            // Update bottom navigation selection based on the card clicked
            if (destinationActivity.equals(EmployeesActivity.class)) {
                bottomNavigationView.setSelectedItemId(R.id.nav_Employees);
            } else if (destinationActivity.equals(AttendanceActivity.class)) {
                bottomNavigationView.setSelectedItemId(R.id.nav_Attendace);
            } else if (destinationActivity.equals(GeofencingActivity.class)) {
                bottomNavigationView.setSelectedItemId(R.id.MapId);
            }
        });
    }

    private void animateCardClick(View card) {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(card, "scaleX", 1.0f, 0.95f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(card, "scaleY", 1.0f, 0.95f);
        scaleDownX.setDuration(100);
        scaleDownY.setDuration(100);

        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(card, "scaleX", 0.95f, 1.0f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(card, "scaleY", 0.95f, 1.0f);
        scaleUpX.setDuration(100);
        scaleUpY.setDuration(100);

        animatorSet.play(scaleDownX).with(scaleDownY);
        animatorSet.play(scaleUpX).with(scaleUpY).after(scaleDownX);

        animatorSet.start();
    }

    private void animateCardsOnEntry() {
        float startY = 100f;
        int delay = 100;

        animateCardEntry(selfAttendanceCard, startY, delay);
        animateCardEntry(attendanceCard, startY, delay * 2);
        animateCardEntry(employeesCard, startY, delay * 3);
        animateCardEntry(geoFenceCard, startY, delay * 4);

        // Only animate LiveFeed if it's visible
        if (liveFeedCard.getVisibility() == View.VISIBLE) {
            animateCardEntry(liveFeedCard, startY, delay * 5);
        }
    }

    private void animateCardEntry(View view, float startY, int delay) {
        view.setTranslationY(startY);
        view.setAlpha(0f);

        view.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(300)
                .setStartDelay(delay)
                .setInterpolator(new OvershootInterpolator(1.2f))
                .start();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recheck role visibility when fragment resumes
        checkRoleAndSetVisibility();
        // Ensure dashboard is selected in bottom navigation when returning to this fragment
        bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);
    }
}