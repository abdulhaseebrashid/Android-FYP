package com.example.buddypunchclone;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginApiService {
    @POST("/api/auth/loginPage")
    Call<JwtAuthResponse> loginUser(@Body LoginDto loginDto);
}