//package com.example.buddypunchclone;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonDeserializationContext;
//import com.google.gson.JsonDeserializer;
//import com.google.gson.JsonElement;
//import com.google.gson.reflect.TypeToken;
//
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.List;
//
//import okhttp3.Interceptor;
//import okhttp3.OkHttpClient;
//import okhttp3.logging.HttpLoggingInterceptor;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class RegistrationRetrofitClient {
//    private static final String BASE_URL = "http://10.0.2.2:8080/"; // Ensure this matches your backend server URL
//    private static Retrofit retrofit = null;
//
//    public static Retrofit getClient(Context context) {
//        if (retrofit == null) {
//            // Create a logging interceptor for debugging network calls
//            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//            // Create OkHttpClient with logging interceptor
//            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//            httpClient.addInterceptor(logging);
//
//            // Add an interceptor to add the Authorization header
//            httpClient.addInterceptor(new Interceptor() {
//                @Override
//                public okhttp3.Response intercept(Chain chain) throws java.io.IOException {
//                    SharedPreferences sharedPreferences = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE);
//                    String token = sharedPreferences.getString("auth_token", null);
//                    if (token != null) {
//                        okhttp3.Request newRequest = chain.request().newBuilder()
//                                .addHeader("Authorization", "Bearer " + token)
//                                .build();
//                        return chain.proceed(newRequest);
//                    }
//                    return chain.proceed(chain.request());
//                }
//            });
//
//            // Custom JSON deserializer to handle different response formats
//            Gson gson = new GsonBuilder()
//                    .registerTypeAdapter(new TypeToken<List<EmployeeResponseDto>>(){}.getType(),
//                            new JsonDeserializer<List<EmployeeResponseDto>>() {
//                                @Override
//                                public List<EmployeeResponseDto> deserialize(
//                                        JsonElement json,
//                                        Type typeOfT,
//                                        JsonDeserializationContext context
//                                ) {
//                                    // If it's already an array, deserialize normally
//                                    if (json.isJsonArray()) {
//                                        return new Gson().fromJson(json, typeOfT);
//                                    }
//
//                                    // If it's a single object, wrap it in a list
//                                    if (json.isJsonObject()) {
//                                        List<EmployeeResponseDto> list = new ArrayList<>();
//                                        EmployeeResponseDto employee = new Gson().fromJson(json, EmployeeResponseDto.class);
//                                        list.add(employee);
//                                        return list;
//                                    }
//
//                                    // If it's a string or something else, return an empty list
//                                    return new ArrayList<>();
//                                }
//                            })
//                    .setDateFormat("yyyy-MM-dd")
//                    .setLenient()
//                    .create();
//
//            // Build Retrofit with Gson converter and OkHttpClient
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(BASE_URL)
//                    .client(httpClient.build())
//                    .addConverterFactory(GsonConverterFactory.create(gson))
//                    .build();
//        }
//        return retrofit;
//    }
//}




package com.example.buddypunchclone;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrationRetrofitClient {
    private static final String SPRING_BASE_URL = "http://172.16.27.92:8080/";
    private static final String DJANGO_BASE_URL = "http://172.16.27.92:8000/";
    private static Retrofit springRetrofit = null;
    private static Retrofit djangoRetrofit = null;

    public static Retrofit getSpringClient(Context context) {
        if (springRetrofit == null) {
            springRetrofit = buildRetrofitClient(context, SPRING_BASE_URL, true);
        }
        return springRetrofit;
    }

    public static Retrofit getDjangoClient(Context context) {
        if (djangoRetrofit == null) {
            djangoRetrofit = buildRetrofitClient(context, DJANGO_BASE_URL, false);
        }
        return djangoRetrofit;
    }

    private static Retrofit buildRetrofitClient(Context context, String baseUrl, boolean isSpringBoot) {
        // Create a logging interceptor for debugging
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Create OkHttpClient builder
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

        // Add logging interceptor
        httpClient.addInterceptor(logging);

        // Add authorization interceptor if needed
        if (isSpringBoot) {
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws java.io.IOException {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE);
                    String token = sharedPreferences.getString("auth_token", null);
                    if (token != null) {
                        okhttp3.Request newRequest = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer " + token)
                                .build();
                        return chain.proceed(newRequest);
                    }
                    return chain.proceed(chain.request());
                }
            });
        }

        // Create Gson builder
        GsonBuilder gsonBuilder = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .setLenient();

        // Add type adapter for Spring Boot responses
        if (isSpringBoot) {
            gsonBuilder.registerTypeAdapter(
                    new TypeToken<List<EmployeeResponseDto>>(){}.getType(),
                    new JsonDeserializer<List<EmployeeResponseDto>>() {
                        @Override
                        public List<EmployeeResponseDto> deserialize(
                                JsonElement json,
                                Type typeOfT,
                                JsonDeserializationContext context
                        ) {
                            if (json.isJsonArray()) {
                                return new Gson().fromJson(json, typeOfT);
                            }
                            if (json.isJsonObject()) {
                                List<EmployeeResponseDto> list = new ArrayList<>();
                                EmployeeResponseDto employee = new Gson().fromJson(json, EmployeeResponseDto.class);
                                list.add(employee);
                                return list;
                            }
                            return new ArrayList<>();
                        }
                    });
        } else {
            // Add type adapter for Django responses if needed
            gsonBuilder.registerTypeAdapter(
                    new TypeToken<List<AttendanceUser>>(){}.getType(),
                    new JsonDeserializer<List<AttendanceUser>>() {
                        @Override
                        public List<AttendanceUser> deserialize(
                                JsonElement json,
                                Type typeOfT,
                                JsonDeserializationContext context
                        ) {
                            if (json.isJsonArray()) {
                                return new Gson().fromJson(json, typeOfT);
                            }
                            if (json.isJsonObject()) {
                                List<AttendanceUser> list = new ArrayList<>();
                                AttendanceUser user = new Gson().fromJson(json, AttendanceUser.class);
                                list.add(user);
                                return list;
                            }
                            return new ArrayList<>();
                        }
                    });
        }

        // Build and return Retrofit instance
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .build();
    }
}
