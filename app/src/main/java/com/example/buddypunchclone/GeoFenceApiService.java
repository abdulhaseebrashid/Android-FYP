package com.example.buddypunchclone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Header;
import retrofit2.http.Path;

import java.util.List;

public interface GeoFenceApiService {

    @GET("/api/geofences") // Adjust the URL path to match your backend controller
    Call<List<GeoFence>> getAllGeoFences(@Header("Authorization") String token);

    @POST("/api/geofence-entries")
    Call<ResponseBody> createGeoFenceEntry(
            @Header("Authorization") String token,
            @Body GeoFenceEntry geoFenceEntry
    );

//    @PATCH("/api/geofence-entries/{id}/exit")
//    Call<ResponseBody> updateGeoFenceExit(
//            @Header("Authorization") String token,
//            @Path("id") Long id
//    );

    @PATCH("/api/geofence-entries/{id}/exit")
    Call<ResponseBody> updateGeoFenceExit(
            @Header("Authorization") String token,
            @Path("id") Long id,
            @Body GeoFenceEntry geoFenceEntry  // Using same GeoFenceEntry class
    );

}
