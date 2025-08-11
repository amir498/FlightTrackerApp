package com.example.liveflighttrackerapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.liveflighttrackerapp.data.model.airLine.StaticAirLineItems
import com.example.liveflighttrackerapp.data.model.airplane.AirPlaneItems
import com.example.liveflighttrackerapp.data.model.airport.AirportsDataItems
import com.example.liveflighttrackerapp.data.model.cities.CitiesDataItems
import com.example.liveflighttrackerapp.data.model.flight.FlightDataItem
import com.example.liveflighttrackerapp.data.model.nearby.NearByAirportsDataItems
import com.example.liveflighttrackerapp.data.model.schedulesFlight.SchedulesFlightsItems
import com.example.liveflighttrackerapp.data.model.tracking.TrackedDataItem

@Database(
    entities = [
        FlightDataItem::class,
        StaticAirLineItems::class,
        SchedulesFlightsItems::class,
        AirportsDataItems::class,
        NearByAirportsDataItems::class,
        CitiesDataItems::class,
        AirPlaneItems::class,
        TrackedDataItem::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun liveFlightDao(): LiveFlightDao
    abstract fun staticAirLineDao(): StaticAirLineDao
    abstract fun schedulesFlightDao(): SchedulesFlightDao
    abstract fun airportsDao(): AirportsDao
    abstract fun nearByDao(): NearByDao
    abstract fun citiesDao(): CitiesDao
    abstract fun airPlaneDao(): AirPlaneDao
    abstract fun trackedFlightDao(): TrackedFlightDao
}