package com.example.flighttrackerappnew.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flighttrackerappnew.data.model.futureSchedule.FutureScheduleItem
import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems

@Dao
interface FutureSchedulesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedulesFlightData(entity: List<FutureScheduleItem>)

    @Query("SELECT * FROM FutureScheduleFlight")
    suspend fun getSchedulesFlightData(): List<FutureScheduleItem>
}