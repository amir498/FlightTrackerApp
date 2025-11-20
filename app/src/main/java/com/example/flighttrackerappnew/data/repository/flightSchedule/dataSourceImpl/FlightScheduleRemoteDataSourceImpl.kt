package com.example.flighttrackerappnew.data.repository.flightSchedule.dataSourceImpl

import android.util.Log
import com.example.flighttrackerappnew.data.api.FlightSchedulesService
import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import com.example.flighttrackerappnew.data.repository.flightSchedule.FlightSchedulesItemsTypeToken
import com.example.flighttrackerappnew.data.repository.flightSchedule.dataSource.FlightScheduleRemoteDataSource
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class FlightScheduleRemoteDataSourceImpl(
    private val scheduleFlightService: FlightSchedulesService
) : FlightScheduleRemoteDataSource {

    private val apiKey = RemoteConfigManager.getString("api_key")

    override suspend fun getLiveFlightData(): List<FlightSchedulesItems> {
        Log.d("MY--TAG", "getLiveFlightData:$apiKey ")
        val response = scheduleFlightService.getSchedulesFlight(apiKey)

        if (response.isSuccessful) {
            val body = response.body()?.string()
            try {
                val type = FlightSchedulesItemsTypeToken().type
                return Gson().fromJson(body, type)
            } catch (e: Exception) {
                Log.e("MY----TAG", "Parse array failed, trying error object: ${e.message}")
                try {
                    val errorJson = JSONObject(body ?: "{}")
                    val errorObj = errorJson.optJSONObject("error")
                    val errorMessage = errorObj?.optString("message")
                        ?: errorJson.optString("error", "Unknown API error")
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
