package com.example.flighttrackerappnew.data.repository.flight.datasourceImpl

import android.util.Log
import com.example.flighttrackerappnew.data.repository.futureSchedule.FutureScheduleItemTypeToken
import com.example.flighttrackerappnew.data.repository.flight.LiveScheduleItemTypeToken
import com.example.flighttrackerappnew.data.api.FlightApiService
import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.data.repository.flight.datasource.LiveFlightRemoteDataSource
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class LiveFlightRemoteDataSourceImpl(
    private val flightApiService: FlightApiService
) : LiveFlightRemoteDataSource {

    private val apiKey = RemoteConfigManager.getString("api_key")

    override suspend fun getLiveFlightData(
        latitude: Double,
        longitude: Double,
        distance: Int
    ): List<FlightDataItem> {

        val response = flightApiService.getFlights(latitude, longitude, distance, apiKey)

        if (response.isSuccessful) {
            val body = response.body()?.string()
            try {
                val type = LiveScheduleItemTypeToken().type
                return Gson().fromJson(body, type)
            } catch (e: Exception) {
                Log.e("MY----TAG", "Parse array failed, trying error object: ${e.message}")
                try {
                    val errorJson = JSONObject(body ?: "{}")
                    val errorMessage = errorJson.optString("error", "Unknown API error")
                    throw IOException("API error: $errorMessage")
                } catch (inner: Exception) {
                    throw IOException("API error: Unknown error format — ${inner.message}")
                }
            }
        } else {
            val errorText = response.errorBody()?.string()
            Log.e("MY----TAG", "HTTP error: ${response.code()} — $errorText")
            throw HttpException(response)
        }
    }
}