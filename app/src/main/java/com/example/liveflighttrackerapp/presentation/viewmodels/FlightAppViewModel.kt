package com.example.liveflighttrackerapp.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveflighttrackerapp.data.model.airLine.StaticAirLineItems
import com.example.liveflighttrackerapp.data.model.airplane.AirPlaneItems
import com.example.liveflighttrackerapp.data.model.airport.AirportsDataItems
import com.example.liveflighttrackerapp.data.model.cities.CitiesDataItems
import com.example.liveflighttrackerapp.data.model.flight.FlightDataItem
import com.example.liveflighttrackerapp.data.model.nearby.NearByAirportsDataItems
import com.example.liveflighttrackerapp.data.model.schedulesFlight.SchedulesFlightsItems
import com.example.liveflighttrackerapp.domain.usecase.GetAirPlanesUseCase
import com.example.liveflighttrackerapp.domain.usecase.GetAirPortsUseCase
import com.example.liveflighttrackerapp.domain.usecase.GetCitiesUseCase
import com.example.liveflighttrackerapp.domain.usecase.GetLiveFlightUseCase
import com.example.liveflighttrackerapp.domain.usecase.GetNearByAirPortsUseCase
import com.example.liveflighttrackerapp.domain.usecase.GetScheduleFlightUseCase
import com.example.liveflighttrackerapp.domain.usecase.GetStaticAirLineUseCase
import com.example.liveflighttrackerapp.presentation.sealedClasses.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FlightAppViewModel(
    private val getLiveFlightUseCase: GetLiveFlightUseCase,
    private val getStaticAirLineUseCase: GetStaticAirLineUseCase,
    private val getScheduleFlightUseCase: GetScheduleFlightUseCase,
    private val getAirPortsUseCase: GetAirPortsUseCase,
    private val getNearByAirPortsUseCase: GetNearByAirPortsUseCase,
    private val getCitiesUseCase: GetCitiesUseCase,
    private val getAirPlanesUseCase: GetAirPlanesUseCase,
) : ViewModel() {

    private val _liveFlightData = MutableLiveData<Resource<List<FlightDataItem>>>()
    val liveFlightData: LiveData<Resource<List<FlightDataItem>>> get() = _liveFlightData

    fun getLiveFlight() {
        viewModelScope.launch {
            _liveFlightData.postValue(Resource.Loading)
            val result = getLiveFlightUseCase.execute()
            _liveFlightData.postValue(result)
        }
    }

    fun loadAllData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getLiveFlight()
            }

            val nearbyJob = async(Dispatchers.IO) { getNearBy() }
            val citiesJob = async(Dispatchers.IO) { getCities() }
            val planesJob = async(Dispatchers.IO) { getAirPlanes() }

            nearbyJob.await()
            citiesJob.await()
            planesJob.await()
        }
    }

    private val _staticAirLineData = MutableLiveData<Resource<List<StaticAirLineItems>>>()
    val staticAirLineData: LiveData<Resource<List<StaticAirLineItems>>> get() = _staticAirLineData
    fun getStaticAirLines() {
        viewModelScope.launch {
            _staticAirLineData.postValue(Resource.Loading)
            val result = getStaticAirLineUseCase.execute()
            _staticAirLineData.postValue(result)
        }
    }

    private val _scheduleFlightData = MutableLiveData<Resource<List<SchedulesFlightsItems>>>()
    val scheduleFlightData: LiveData<Resource<List<SchedulesFlightsItems>>> get() = _scheduleFlightData

    fun getScheduleFlight() {
        viewModelScope.launch {
            _scheduleFlightData.postValue(Resource.Loading)
            val result = getScheduleFlightUseCase.execute()
            _scheduleFlightData.postValue(result)
        }
    }

    private val _airPortsData = MutableLiveData<Resource<List<AirportsDataItems>>>()
    val airPortsData: LiveData<Resource<List<AirportsDataItems>>> get() = _airPortsData

    fun getAirPorts() {
        viewModelScope.launch {
            _airPortsData.postValue(Resource.Loading)
            val result = getAirPortsUseCase.execute()
            _airPortsData.postValue(result)
        }
    }

    private val _nearByData = MutableLiveData<Resource<List<NearByAirportsDataItems>>>()
    val nearByData: LiveData<Resource<List<NearByAirportsDataItems>>> get() = _nearByData

    fun getNearBy() {
        viewModelScope.launch {
            _nearByData.postValue(Resource.Loading)
            val result = getNearByAirPortsUseCase.execute()
            _nearByData.postValue(result)
        }
    }

    private val _citiesData = MutableLiveData<Resource<List<CitiesDataItems>>>()
    val citiesData: LiveData<Resource<List<CitiesDataItems>>> get() = _citiesData
    fun getCities() {
        viewModelScope.launch {
            _citiesData.postValue(Resource.Loading)
            val result = getCitiesUseCase.execute()
            _citiesData.postValue(result)
        }
    }

    private val _airPlanesData = MutableLiveData<Resource<List<AirPlaneItems>>>()
    val airPlanesData: LiveData<Resource<List<AirPlaneItems>>> get() = _airPlanesData

    fun getAirPlanes() {
        viewModelScope.launch {
            _airPlanesData.postValue(Resource.Loading)
            val result = getAirPlanesUseCase.execute()
            _airPlanesData.postValue(result)
        }
    }

}