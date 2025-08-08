package com.example.flighttrackerappnew.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems

@Dao
interface CitiesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCitiesData(entity: List<CitiesDataItems>)

    @Query("SELECT * FROM CitiesDataItems")
    suspend fun getCitiesData(): List<CitiesDataItems>
}