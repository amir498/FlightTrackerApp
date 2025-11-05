package com.example.flighttrackerappnew.data.repository.airLine.dataSourceImpl

import android.util.Log
import com.example.flighttrackerappnew.data.FutureScheduleItemTypeToken
import com.example.flighttrackerappnew.data.api.StaticAirLineService
import com.example.flighttrackerappnew.data.model.airLine.StaticAirLineItems
import com.example.flighttrackerappnew.data.repository.airLine.dataSource.StaticAirLineRemoteDataSource
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class StaticAirLineRemoteDataSourceImpl(
    private val staticAirLineService: StaticAirLineService
) : StaticAirLineRemoteDataSource {

    private val apiKey = RemoteConfigManager.getString("api_key")

    override suspend fun getStaticAirLineFromRemote(): List<StaticAirLineItems> {
        val response = staticAirLineService.getStaticAirLine(apiKey)

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
