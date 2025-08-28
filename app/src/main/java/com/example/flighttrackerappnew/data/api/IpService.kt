package com.example.flighttrackerappnew.data.api

import com.example.flighttrackerappnew.data.model.IpLocationResponse
import retrofit2.http.GET

interface IpService {
    @GET("json/")
    suspend fun getLocation(): IpLocationResponse
}