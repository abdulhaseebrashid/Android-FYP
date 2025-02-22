//package com.example.buddypunchclone;
//
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.http.Body;
//import retrofit2.http.DELETE;
//import retrofit2.http.GET;
//import retrofit2.http.POST;
//import retrofit2.http.PUT;
//import retrofit2.http.Path;
//
//public interface AddEmployeeApiService {
//    @POST("api/employees")
//    Call<EmployeeResponseDto> createEmployee(@Body AddEmployeeDTO employeeDTO);
//
//    @PUT("api/employees/{id}")
//    Call<EmployeeResponseDto> updateEmployee(@Path("id") Long id, @Body AddEmployeeDTO employeeDTO);
//
//    @DELETE("api/employees/{id}")
//    Call<Void> deleteEmployee(@Path("id") Long id);
//
//    @GET("api/employees/{id}")
//    Call<EmployeeResponseDto> getEmployeeById(@Path("id") Long id);
//
//    @GET("api/employees")
//    Call<List<EmployeeResponseDto>> getAllEmployees();
//
//
//
////    @GET("api/groups/{employeeId}")
////    Call<GroupResponseDto> getEmployeeGroupById(@Path("employeeId") Long employeeId);
//}



package com.example.buddypunchclone;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AddEmployeeApiService {

    @POST("api/employees")
    Call<EmployeeResponseDto> createEmployee(@Body AddEmployeeDTO employeeDTO);

    @PUT("api/employees/{id}")
    Call<EmployeeResponseDto> updateEmployee(@Path("id") Long id, @Body AddEmployeeDTO employeeDTO);

    @DELETE("api/employees/{id}")
    Call<Void> deleteEmployee(@Path("id") Long id);

    @GET("api/employees/{id}")
    Call<EmployeeResponseDto> getEmployeeById(@Path("id") Long id);

    @GET("api/employees")
    Call<List<EmployeeResponseDto>> getAllEmployees();

    @PUT("api/employees/{id}")
    Call<EmployeeResponseDto> updateEmployee(@Path("id") Long id, @Body EmployeeRequestDto employeeRequestDto);

    @PUT("api/employees/{id}")
    Call<EmployeeProfileResponseDto> updateEmployeeProfile(
            @Path("id") Long id,
            @Body EmployeeProfileUpdateDto profileUpdateDto
    );
}
