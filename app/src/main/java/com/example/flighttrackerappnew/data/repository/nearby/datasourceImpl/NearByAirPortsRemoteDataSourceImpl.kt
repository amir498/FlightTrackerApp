package com.example.flighttrackerappnew.data.repository.nearby.datasourceImpl

import android.util.Log
import com.example.flighttrackerappnew.data.api.NearbyService
import com.example.flighttrackerappnew.data.model.nearby.NearByAirportsDataItems
import com.example.flighttrackerappnew.data.repository.nearby.datasource.NearByAirPortsRemoteDataSource
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class NearByAirPortsRemoteDataSourceImpl(
    private val nearByService: NearbyService
) : NearByAirPortsRemoteDataSource {

    private val apiKey = RemoteConfigManager.getString("api_key")

    override suspend fun getNearByData(
        lat: Double,
        long: Double,
        distance: Int
    ): List<NearByAirportsDataItems> {
        val response = nearByService.getNearby(lat, long, distance, apiKey)

        if (response.isSuccessful) {
            val body = response.body()?.string()
            try {
                val type = object : TypeToken<List<NearByAirportsDataItems>>() {}.type
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
