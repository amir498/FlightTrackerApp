package com.example.flighttrackerappnew.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flighttrackerappnew.data.model.flight.FlightDataItem

@Dao
interface LiveFlightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiveFlightData(entity: List<FlightDataItem>)

    @Query("SELECT * FROM FlightDataItem")
    suspend fun getLiveFlightData(): List<FlightDataItem>
}