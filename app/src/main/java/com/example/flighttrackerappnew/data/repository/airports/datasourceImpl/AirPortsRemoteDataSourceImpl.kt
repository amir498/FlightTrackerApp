package com.example.flighttrackerappnew.data.repository.airports.datasourceImpl

import android.util.Log
import com.example.flighttrackerappnew.data.FutureScheduleItemTypeToken
import com.example.flighttrackerappnew.data.api.AirportsService
import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems
import com.example.flighttrackerappnew.data.repository.airports.datasource.AirPortsRemoteDataSource
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class AirPortsRemoteDataSourceImpl(
    private val airPortsService: AirportsService
) : AirPortsRemoteDataSource {

    private val apiKey = RemoteConfigManager.getString("api_key")

    override suspend fun getAirPortsFromRemote(): List<AirportsDataItems> {
        val response = airPortsService.getAirportsLine(apiKey)

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
