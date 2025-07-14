package com.example.liveflighttrackerapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.liveflighttrackerapp.data.model.airLine.StaticAirLineItems
import com.example.liveflighttrackerapp.data.model.schedulesFlight.SchedulesFlightsItems

@Dao
interface SchedulesFlightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedulesFlightData(entity: List<SchedulesFlightsItems>)

    @Query("SELECT * FROM SchedulesFlightsItems")
    suspend fun getSchedulesFlightData(): List<SchedulesFlightsItems>
}