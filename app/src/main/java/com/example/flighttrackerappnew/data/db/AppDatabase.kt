package com.example.flighttrackerappnew.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.flighttrackerappnew.data.model.FollowFlightData
import com.example.flighttrackerappnew.data.model.airLine.StaticAirLineItems
import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems
import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems
import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData
import com.example.flighttrackerappnew.data.model.futureSchedule.FutureScheduleItem
import com.example.flighttrackerappnew.data.model.nearby.NearByAirportsDataItems
import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import com.example.flighttrackerappnew.data.model.tracking.TrackedDataItem

@Database(
    entities = [
        FlightDataItem::class,
        StaticAirLineItems::class,
        FlightSchedulesItems::class,
        AirportsDataItems::class,
        NearByAirportsDataItems::class,
        CitiesDataItems::class,
        AirPlaneItems::class,
        TrackedDataItem::class,
        FollowFlightData::class,
        FullDetailFlightData::class,
        FutureScheduleItem::class,
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun liveFlightDao(): LiveFlightDao
    abstract fun staticAirLineDao(): StaticAirLineDao
    abstract fun schedulesFlightDao(): FlightSchedulesDao
    abstract fun airportsDao(): AirportsDao
    abstract fun nearByDao(): NearByDao
    abstract fun citiesDao(): CitiesDao
    abstract fun airPlaneDao(): AirPlaneDao
    abstract fun trackedFlightDao(): TrackedFlightDao
    abstract fun followLiveFlightDao(): FollowLiveFlightDao
    abstract fun favFlightDao(): FavFlightDao
    abstract fun futureFlightDao(): FutureSchedulesDao
}