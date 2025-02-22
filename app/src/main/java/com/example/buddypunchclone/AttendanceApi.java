package com.example.buddypunchclone;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface AttendanceApi {
    @GET("api/users/")  // Update this path to match your Django API endpoint
    Call<List<AttendanceUser>> getUsers();
}