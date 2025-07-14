package com.example.liveflighttrackerapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.liveflighttrackerapp.data.model.tracking.TrackedDataItem

@Dao
interface TrackedFlightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackedFlightData(entity: TrackedDataItem)

    @Query("SELECT * FROM TrackedDataItem")
    suspend fun getTrackedFlightData(): List<TrackedDataItem>
}