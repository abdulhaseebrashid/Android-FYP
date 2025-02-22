package com.example.buddypunchclone;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/system-status/")
    Call<SystemStatusResponse> getSystemStatus();

    @POST("api/send-whatsapp/")
    Call<Void> sendWhatsAppNotification(@Body NotificationRequest request);

    @Multipart
    @POST("api/mark-attendance/")
    Call<AttendanceResponse> markAttendance(@Part MultipartBody.Part image);

    @GET("api/users/{username}/")
    Call<AttendanceUser> getUser(@Path("username") String username);

    @PUT("api/users/{username}/")
    Call<AttendanceUser> updateUser(@Path("username") String username, @Body AttendanceUser user);

    @DELETE("api/users/{username}/")
    Call<Void> deleteUser(@Path("username") String username);

    // New method to fetch groups
    @GET("api/groups")
    Call<List<GroupResponseDto>> getGroups();
}