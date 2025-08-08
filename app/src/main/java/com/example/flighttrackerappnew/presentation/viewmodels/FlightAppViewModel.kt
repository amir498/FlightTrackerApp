package com.example.flighttrackerappnew.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flighttrackerappnew.data.db.FavFlightDao
import com.example.flighttrackerappnew.data.db.FollowLiveFlightDao
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
import com.example.flighttrackerappnew.domain.usecase.GetAirPlanesUseCase
import com.example.flighttrackerappnew.domain.usecase.GetAirPortsUseCase
import com.example.flighttrackerappnew.domain.usecase.GetCitiesUseCase
import com.example.flighttrackerappnew.domain.usecase.GetFlightScheduleUseCase
import com.example.flighttrackerappnew.domain.usecase.GetFutureScheduleFlightUseCase
import com.example.flighttrackerappnew.domain.usecase.GetLiveFlightUseCase
import com.example.flighttrackerappnew.domain.usecase.GetNearByAirPortsUseCase
import com.example.flighttrackerappnew.domain.usecase.GetStaticAirLineUseCase
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FlightAppViewModel(
    private val getLiveFlightUseCase: GetLiveFlightUseCase,
    private val getStaticAirLineUseCase: GetStaticAirLineUseCase,
    private val getFlightScheduleUseCase: GetFlightScheduleUseCase,
    private val getAirPortsUseCase: GetAirPortsUseCase,
    private val getNearByAirPortsUseCase: GetNearByAirPortsUseCase,
    private val getCitiesUseCase: GetCitiesUseCase,
    private val getAirPlanesUseCase: GetAirPlanesUseCase,
    private val getFutureScheduleFlightUseCase: GetFutureScheduleFlightUseCase,
    private val favFlightDao: FavFlightDao,
    private val followFlightDao: FollowLiveFlightDao,
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

    fun getAllData(onAllComplete: () -> Unit = {}) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getLiveFlight()
            }
            coroutineScope {
                val nearbyJob = async(Dispatchers.IO) { getNearBy() }
                val citiesJob = async(Dispatchers.IO) { getCities() }
                val planesJob = async(Dispatchers.IO) { getAirPlanes() }
                async(Dispatchers.IO) { getFavFlightData() }
                async(Dispatchers.IO) { getFollowFlightData() }

                awaitAll(nearbyJob, citiesJob, planesJob)
            }

            onAllComplete()
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

    private val _scheduleFlightData = MutableLiveData<Resource<List<FlightSchedulesItems>>>()
    val scheduleFlightData: LiveData<Resource<List<FlightSchedulesItems>>> get() = _scheduleFlightData

    fun getScheduleFlight() {
        viewModelScope.launch {
            _scheduleFlightData.postValue(Resource.Loading)
            val result = getFlightScheduleUseCase.execute()
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

    private val _futureScheduleFlightData = MutableLiveData<Resource<List<FutureScheduleItem>>>()
    val futureScheduleFlightData: LiveData<Resource<List<FutureScheduleItem>>> get() = _futureScheduleFlightData

    fun getFutureScheduleFlight() {
        viewModelScope.launch {
            _futureScheduleFlightData.postValue(Resource.Loading)
            val result = getFutureScheduleFlightUseCase.execute()
            _futureScheduleFlightData.postValue(result)
        }
    }

    private val _favFlightData = MutableLiveData<List<FullDetailFlightData>>()
    val favFlightData: LiveData<List<FullDetailFlightData>> get() = _favFlightData

    fun getFavFlightData() {
        viewModelScope.launch {
            val result = favFlightDao.getFavFlightData()
            _favFlightData.postValue(result)
        }
    }

    private val _followFlightData = MutableLiveData<List<FollowFlightData>>()
    val followFlightData: LiveData<List<FollowFlightData>> get() = _followFlightData

    fun getFollowFlightData() {
        viewModelScope.launch {
            val result = followFlightDao.getFollowLiveFlightData()
            _followFlightData.postValue(result)
        }
    }
}


