package com.example.liveflighttrackerapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.liveflighttrackerapp.data.model.airplane.AirPlaneItems
import com.example.liveflighttrackerapp.data.model.airport.AirportsDataItems

@Dao
interface AirPlaneDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAirPlaneData(entity: List<AirPlaneItems>)

    @Query("SELECT * FROM AirPlaneItems")
    suspend fun getAirPlaneData(): List<AirPlaneItems>
}