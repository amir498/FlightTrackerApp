package com.example.flighttrackerappnew.data.repository.cities.datasourceImpl

import android.util.Log
import com.example.flighttrackerappnew.data.api.CitiesService
import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems
import com.example.flighttrackerappnew.data.repository.cities.datasource.CitiesRemoteDataSource
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class CitiesRemoteDataSourceImpl(
    private val citiesService: CitiesService
) : CitiesRemoteDataSource {

    private val apiKey = RemoteConfigManager.getString("api_key")

    override suspend fun getCitiesFromRemote(): List<CitiesDataItems> {
        val response = citiesService.getCities(apiKey)

        if (response.isSuccessful) {
            val body = response.body()?.string()
            try {
                val type = object : TypeToken<List<CitiesDataItems>>() {}.type
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
