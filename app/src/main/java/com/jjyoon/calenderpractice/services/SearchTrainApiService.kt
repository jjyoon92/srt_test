package com.jjyoon.calenderpractice.services

import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SearchTrainApiService {
    @Headers("accept: application/json",
        "content-type: application/json")
    @POST("/train/search")
    fun searchTrain(@Body requestData: RequestBody): Call<ApiResponse>
}

data class ApiResponse (
    // 응답 데이터 필드 정의
    @SerializedName("result")
    val result: String,
    @SerializedName("message")
    val message: String
)