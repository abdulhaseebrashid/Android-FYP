package com.example.buddypunchclone;

//import com.example.buddypunchauth.RegistrationDto;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegistrationApiService {
    @POST("/api/auth/register")
    Call<ResponseBody> registerUser(@Body RegistrationDto registrationDto);

}
