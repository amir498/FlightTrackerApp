package com.example.flighttrackerappnew.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flighttrackerappnew.data.model.fav.FavFlightData
import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData

@Dao
interface StarredFlightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavFlightData(entity: FavFlightData)

    @Query("SELECT * FROM FavFlightData")
    suspend fun getFavFlightData(): List<FavFlightData>

    @Query("DELETE FROM FavFlightData WHERE flightNo = :flightNumber")
    suspend fun deleteFavFlightByNumber(flightNumber: String)
}