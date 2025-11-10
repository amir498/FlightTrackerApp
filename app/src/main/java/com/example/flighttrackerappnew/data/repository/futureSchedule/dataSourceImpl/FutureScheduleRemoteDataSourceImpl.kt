package com.example.flighttrackerappnew.data.repository.futureSchedule.dataSourceImpl

import android.util.Log
import com.example.flighttrackerappnew.data.repository.futureSchedule.FutureScheduleItemTypeToken
import com.example.flighttrackerappnew.data.api.FutureScheduleFlightService
import com.example.flighttrackerappnew.data.model.futureSchedule.FutureScheduleItem
import com.example.flighttrackerappnew.data.repository.futureSchedule.dataSource.FutureScheduleRemoteDataSource
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.airportCode
import com.example.flighttrackerappnew.presentation.utils.flightType
import com.example.flighttrackerappnew.presentation.utils.startDate
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class FutureScheduleRemoteDataSourceImpl(
    private val futureScheduleFlightService: FutureScheduleFlightService
) : FutureScheduleRemoteDataSource {

    private val apiKey = RemoteConfigManager.getString("api_key")

    override suspend fun getFutureFlightData(): List<FutureScheduleItem> {
        val response = futureScheduleFlightService.getSchedulesFlight(
            type = flightType,
            iataCode = airportCode.uppercase(),
            date = startDate,
            apiKey = apiKey
        )

        if (response.isSuccessful) {
            val body = response.body()?.string()
            try {
                val type = FutureScheduleItemTypeToken().type
                return Gson().fromJson(body, type)
            } catch (e: Exception) {
                Log.e("MY----TAG", "Parse array failed, trying error object: ${e.message}")
                try {
                    val errorJson = JSONObject(body ?: "{}")
                    val errorMessage =
                        errorJson.optJSONObject("error")?.optString("message")
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
