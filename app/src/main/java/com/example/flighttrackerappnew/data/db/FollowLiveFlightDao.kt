package com.example.flighttrackerappnew.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flighttrackerappnew.data.model.FollowFlightData
import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData

@Dao
interface FollowLiveFlightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollowLiveFlightData(entity: FollowFlightData)

    @Query("SELECT * FROM FollowFlightData")
    suspend fun getFollowLiveFlightData(): List<FollowFlightData>

    @Query("DELETE FROM FollowFlightData WHERE flightNum = :flightNumber")
    suspend fun deleteFollowFlightByNumber(flightNumber: String)
}