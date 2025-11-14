package com.example.flighttrackerappnew.data.repository.flightScheduleForSpecificIataNo.dataSourceImpl

import android.util.Log
import com.example.flighttrackerappnew.data.api.FlightSchedulesServiceForSpecificiataNo
import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import com.example.flighttrackerappnew.data.repository.flightSchedule.FlightSchedulesItemsTypeToken
import com.example.flighttrackerappnew.data.repository.flightScheduleForSpecificIataNo.dataSource.FlightScheduleRemoteDataSourceForSpecificIataNumber
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class FlightScheduleRemoteDataSourceForSpecificIataNumberImpl(
    private val flightSchedulesServiceForSpecificiataNo: FlightSchedulesServiceForSpecificiataNo
) : FlightScheduleRemoteDataSourceForSpecificIataNumber {

    private val apiKey = RemoteConfigManager.getString("api_key")

    override suspend fun getScheduleFlightDataForSpecificIataNumber(iataNo: String): List<FlightSchedulesItems> {
        val response = flightSchedulesServiceForSpecificiataNo.getSchedulesFlightForSpecificIataNo(apiKey,iataNo)

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
