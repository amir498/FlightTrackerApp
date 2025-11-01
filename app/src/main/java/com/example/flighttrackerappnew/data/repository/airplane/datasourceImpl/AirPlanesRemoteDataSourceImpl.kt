package com.example.flighttrackerappnew.data.repository.airplane.datasourceImpl

import android.util.Log
import com.example.flighttrackerappnew.data.api.AirPlanesService
import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.data.repository.airplane.datasource.AirPlanesRemoteDataSource
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class AirPlanesRemoteDataSourceImpl(
    private val airPlanesService: AirPlanesService
) : AirPlanesRemoteDataSource {

    private val apiKey = RemoteConfigManager.getString("api_key")

    override suspend fun getStaticAirPlaneFromRemote(): List<AirPlaneItems> {
        val response = airPlanesService.getAirPlanesData(apiKey)

        if (response.isSuccessful) {
            val body = response.body()?.string()
            try {
                val type = object : TypeToken<List<AirPlaneItems>>() {}.type
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
