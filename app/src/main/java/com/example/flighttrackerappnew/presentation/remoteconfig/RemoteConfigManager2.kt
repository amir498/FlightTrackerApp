package com.example.flighttrackerappnew.presentation.remoteconfig

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.tasks.await
import java.util.concurrent.ConcurrentHashMap

class RemoteConfigManager2(private val remoteConfig: FirebaseRemoteConfig) {

    private val cache = ConcurrentHashMap<String, Any>()

    suspend fun fetchAndActivateSafe(): Boolean {
        return try {
            remoteConfig.fetchAndActivate().await()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getBoolean(key: String): Boolean {
        return (cache[key] as? Boolean)
            ?: remoteConfig.getBoolean(key).also { cache[key] = it }
    }

    fun getString(key: String): String {
        return (cache[key] as? String)
            ?: remoteConfig.getString(key).also { cache[key] = it }
    }

    fun getInt(key: String): Int {
        return (cache[key] as? Int)
            ?: remoteConfig.getLong(key).toInt().also { cache[key] = it }
    }

    fun clearCache() {
        cache.clear()
    }
}