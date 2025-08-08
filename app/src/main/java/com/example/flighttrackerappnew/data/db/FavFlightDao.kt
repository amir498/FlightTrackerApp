package com.example.flighttrackerappnew.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData

@Dao
interface FavFlightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavFlightData(entity: FullDetailFlightData)

    @Query("SELECT * FROM FullDetailFlightData")
    suspend fun getFavFlightData(): List<FullDetailFlightData>

    @Query("DELETE FROM FullDetailFlightData WHERE flightNo = :flightNumber")
    suspend fun deleteFavFlightByNumber(flightNumber: String)
}