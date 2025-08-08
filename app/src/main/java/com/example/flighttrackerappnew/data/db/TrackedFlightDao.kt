package com.example.flighttrackerappnew.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flighttrackerappnew.data.model.tracking.TrackedDataItem

@Dao
interface TrackedFlightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackedFlightData(entity: TrackedDataItem)

    @Query("SELECT * FROM TrackedDataItem")
    suspend fun getTrackedFlightData(): List<TrackedDataItem>
}