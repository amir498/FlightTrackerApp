package com.example.liveflighttrackerapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.liveflighttrackerapp.data.model.nearby.NearByAirportsDataItems

@Dao
interface NearByDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNearBy(entity: List<NearByAirportsDataItems>)

    @Query("SELECT * FROM AirportsDataItems")
    suspend fun getNearByData(): List<NearByAirportsDataItems>
}