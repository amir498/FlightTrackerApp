package com.example.flighttrackerappnew.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems

@Dao
interface AirPlaneDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAirPlaneData(entity: List<AirPlaneItems>)

    @Query("SELECT * FROM AirPlaneItems")
    suspend fun getAirPlaneData(): List<AirPlaneItems>
}