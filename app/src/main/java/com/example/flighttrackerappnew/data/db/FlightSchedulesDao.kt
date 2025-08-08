package com.example.flighttrackerappnew.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems

@Dao
interface FlightSchedulesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedulesFlightData(entity: List<FlightSchedulesItems>)

    @Query("SELECT * FROM SchedulesFlightsItems")
    suspend fun getSchedulesFlightData(): List<FlightSchedulesItems>
}