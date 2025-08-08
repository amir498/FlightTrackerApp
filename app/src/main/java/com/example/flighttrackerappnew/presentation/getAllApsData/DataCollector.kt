package com.example.flighttrackerappnew.presentation.getAllApsData

import com.example.flighttrackerappnew.data.model.airLine.StaticAirLineItems
import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems
import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems
import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.data.model.futureSchedule.FutureScheduleItem
import com.example.flighttrackerappnew.data.model.nearby.NearByAirportsDataItems
import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems

class DataCollector {

    var cities: List<CitiesDataItems> = emptyList()

    var airports: List<AirportsDataItems> = emptyList()

    var flights: List<FlightDataItem> = emptyList()

    var planes: List<AirPlaneItems> = emptyList()

    var staticAirlines: List<StaticAirLineItems> = emptyList()

    var schedules: List<FlightSchedulesItems> = emptyList()

    var nearby: List<NearByAirportsDataItems> = emptyList()

    var matchingAirplanes: List<AirPlaneItems> = emptyList()

    var futureScheduleFlightData: List<FutureScheduleItem> = emptyList()
}
