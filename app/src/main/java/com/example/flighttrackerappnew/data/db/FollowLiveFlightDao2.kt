package com.example.flighttrackerappnew.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flighttrackerappnew.data.model.FollowFlightData
import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData

@Dao
interface FollowLiveFlightDao2 {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollowLiveFlightData(entity: FullDetailFlightData)

    @Query("SELECT * FROM FullDetailFlightData")
    suspend fun getFollowLiveFlightData(): List<FullDetailFlightData>

    @Query("DELETE FROM FullDetailFlightData WHERE flightNo = :flightNumber")
    suspend fun deleteFollowFlightByNumber(flightNumber: String)
}