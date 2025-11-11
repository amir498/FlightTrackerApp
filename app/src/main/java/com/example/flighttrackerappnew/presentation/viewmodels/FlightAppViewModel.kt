package com.example.flighttrackerappnew.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flighttrackerappnew.data.db.FollowLiveFlightDao
import com.example.flighttrackerappnew.data.db.StarredFlightDao
import com.example.flighttrackerappnew.data.model.airLine.StaticAirLineItems
import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems
import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems
import com.example.flighttrackerappnew.data.model.fav.FavFlightData
import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.data.model.follow.FollowFlightData
import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData
import com.example.flighttrackerappnew.data.model.futureSchedule.FutureScheduleItem
import com.example.flighttrackerappnew.data.model.nearby.NearByAirportsDataItems
import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import com.example.flighttrackerappnew.domain.usecase.GetAirCraftUseCase
import com.example.flighttrackerappnew.domain.usecase.GetAirPortsUseCase
import com.example.flighttrackerappnew.domain.usecase.GetCitiesUseCase
import com.example.flighttrackerappnew.domain.usecase.GetFlightScheduleUseCase
import com.example.flighttrackerappnew.domain.usecase.GetFutureScheduleFlightUseCase
import com.example.flighttrackerappnew.domain.usecase.GetLiveFlightUseCase
import com.example.flighttrackerappnew.domain.usecase.GetNearByAirPortsUseCase
import com.example.flighttrackerappnew.domain.usecase.GetStaticAirLineUseCase
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import com.example.flighttrackerappnew.presentation.utils.isCitiesApiSuccess
import com.example.flighttrackerappnew.presentation.utils.isFutureScheduleApiSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FlightAppViewModel(
    private val getLiveFlightUseCase: GetLiveFlightUseCase,
    private val getStaticAirLineUseCase: GetStaticAirLineUseCase,
    private val getFlightScheduleUseCase: GetFlightScheduleUseCase,
    private val getAirPortsUseCase: GetAirPortsUseCase,
    private val getNearByAirPortsUseCase: GetNearByAirPortsUseCase,
    private val getCitiesUseCase: GetCitiesUseCase,
    private val getAirCraftUseCase: GetAirCraftUseCase,
    private val getFutureScheduleFlightUseCase: GetFutureScheduleFlightUseCase,
    private val starredFlightDao: StarredFlightDao,
    private val followFlightDao: FollowLiveFlightDao
) : ViewModel() {
    private val _liveFlightData = MutableLiveData<Resource<List<FlightDataItem>>>()
    val liveFlightData: LiveData<Resource<List<FlightDataItem>>> get() = _liveFlightData

    suspend fun getLiveFlight(latitude: Double, longitude: Double, distance: Int) {
        _liveFlightData.postValue(Resource.Loading)
        val result = getLiveFlightUseCase.execute(latitude, longitude, distance)
        _liveFlightData.postValue(result)
    }

    fun getAllData(lat: Double, long: Double, distance: Int) {
        getDynamicApiData(lat, long, distance)
        getStaticApiData()
        getOtherAppDataFromRoomDb()
    }

    private fun getOtherAppDataFromRoomDb() {
        viewModelScope.launch {
            async(Dispatchers.IO) { getFavFlightData() }
            async(Dispatchers.IO) { getFollowFlightData() }
        }
    }

    private fun getStaticApiData() {
        viewModelScope.launch {
            async(Dispatchers.IO) { getCities() }
            async(Dispatchers.IO) { getAirCraft() }
            async(Dispatchers.IO) { getStaticAirLines() }
            async(Dispatchers.IO) { getAirPorts() }
        }
    }

    fun getDynamicApiData(lat: Double, long: Double, distance: Int) {
        viewModelScope.launch {
            async(Dispatchers.IO) {
                getLiveFlight(lat, long, distance)
            }
            async(Dispatchers.IO) {
                getScheduleFlight()
            }
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

    fun getNearBy(lat: Double, long: Double, distance: Int) {
        viewModelScope.launch {
            _nearByData.postValue(Resource.Loading)
            val result = getNearByAirPortsUseCase.execute(lat, long, distance)
            _nearByData.postValue(result)
        }
    }

    private val _citiesData = MutableLiveData<Resource<List<CitiesDataItems>>>()
    val citiesData: LiveData<Resource<List<CitiesDataItems>>> get() = _citiesData
    fun getCities() {
        viewModelScope.launch {
            isCitiesApiSuccess = false
            _citiesData.postValue(Resource.Loading)
            val result = getCitiesUseCase.execute()
            _citiesData.postValue(result)
        }
    }

    private val _airCraftData = MutableLiveData<Resource<List<AirPlaneItems>>>()
    val airCraftData: LiveData<Resource<List<AirPlaneItems>>> get() = _airCraftData

    fun getAirCraft() {
        viewModelScope.launch {
            _airCraftData.postValue(Resource.Loading)
            val result = getAirCraftUseCase.execute()
            _airCraftData.postValue(result)
        }
    }

    private val _futureScheduleFlightData = MutableLiveData<Resource<List<FutureScheduleItem>>>()
    val futureScheduleFlightData: LiveData<Resource<List<FutureScheduleItem>>> get() = _futureScheduleFlightData

    fun getFutureScheduleFlight() {
        viewModelScope.launch {
            isFutureScheduleApiSuccess = false
            _futureScheduleFlightData.postValue(Resource.Loading)
            val result = getFutureScheduleFlightUseCase.execute()
            _futureScheduleFlightData.postValue(result)
        }
    }

    fun clearFutureFlightData() {
        _futureScheduleFlightData.postValue(Resource.Success(emptyList()))
    }

    private val _favFlightData = MutableLiveData<List<FavFlightData>>()
    val favFlightData: LiveData<List<FavFlightData>> get() = _favFlightData

    fun getFavFlightData() {
        viewModelScope.launch {
            val result = starredFlightDao.getFavFlightData()
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

    var arrivalFlightData: MutableLiveData<ArrayList<FullDetailFlightData>> =
        MutableLiveData<ArrayList<FullDetailFlightData>>()
    var departureFlightData: MutableLiveData<ArrayList<FullDetailFlightData>> =
        MutableLiveData<ArrayList<FullDetailFlightData>>()
}


