package com.example.liveflighttrackerapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.liveflighttrackerapp.data.model.airport.AirportsDataItems

@Dao
interface AirportsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAirportsData(entity: List<AirportsDataItems>)

    @Query("SELECT * FROM AirportsDataItems")
    suspend fun getAirportsData(): List<AirportsDataItems>
}