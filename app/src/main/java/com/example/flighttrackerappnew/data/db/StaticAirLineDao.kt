package com.example.flighttrackerappnew.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flighttrackerappnew.data.model.airLine.StaticAirLineItems

@Dao
interface StaticAirLineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaticAirLineData(entity: List<StaticAirLineItems>)

    @Query("SELECT * FROM StaticAirLineItems")
    suspend fun getStaticAirLineData(): List<StaticAirLineItems>
}