package com.example.liveflighttrackerapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.liveflighttrackerapp.data.model.airport.AirportsDataItems
import com.example.liveflighttrackerapp.data.model.cities.CitiesDataItems

@Dao
interface CitiesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCitiesData(entity: List<CitiesDataItems>)

    @Query("SELECT * FROM CitiesDataItems")
    suspend fun getCitiesData(): List<CitiesDataItems>
}