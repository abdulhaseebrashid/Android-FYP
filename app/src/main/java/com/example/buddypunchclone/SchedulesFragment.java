//package com.example.buddypunchclone;
//
//import android.app.Dialog;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.CalendarView;
//import android.widget.TextView;
//
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//
//public class SchedulesFragment extends Fragment {
//
//    private boolean isStartDateDialog = false;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_schedules, container, false);
//
//        String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
//        final Dialog dialog = new Dialog(view.getContext());
//
//        TextView fragmentStartDatePreview = (TextView)
//                view.findViewById(R.id.startDatePreview);
//
//        TextView fragmentEndDatePreview = (TextView)
//                view.findViewById(R.id.endDatePreview);
//
//        fragmentStartDatePreview.setText(currentDate);
//        fragmentEndDatePreview.setText(currentDate);
//
//        fragmentStartDatePreview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                isStartDateDialog = true;
//                dialog.show();
//            }
//        });
//
//        fragmentEndDatePreview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                isStartDateDialog = false;
//                dialog.show();
//            }
//        });
//
//        dialog.setContentView(R.layout.calendar_layout);
//        CalendarView dialogCalendar = (CalendarView) dialog.findViewById(R.id.calendar);
//        Button okButton = (Button) dialog.findViewById(R.id.okButton);
//        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
//
//        dialogCalendar
//                .setOnDateChangeListener(
//                        new CalendarView
//                                .OnDateChangeListener() {
//                            @Override
//                            public void onSelectedDayChange(
//                                    @NonNull CalendarView view,
//                                    int year,
//                                    int month,
//                                    int dayOfMonth) {
//                                if (isStartDateDialog) {
//                                    fragmentStartDatePreview.setText(dayOfMonth + "/" + month + "/" + year);
//                                } else {
//                                    fragmentEndDatePreview.setText(dayOfMonth + "/" + month + "/" + year);
//                                }
//                            }
//                        }
//                );
//        okButton.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                }
//        );
//
//        cancelButton.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (isStartDateDialog) {
//                            fragmentStartDatePreview.setText(currentDate);
//                        } else {
//                            fragmentEndDatePreview.setText(currentDate);
//                        }
//                        dialog.dismiss();
//                    }
//                }
//        );
//
//        return view;
//    }
//}
