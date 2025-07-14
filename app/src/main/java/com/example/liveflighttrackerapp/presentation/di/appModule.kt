package com.example.liveflighttrackerapp.presentation.di

import android.app.Application
import androidx.room.Room
import com.example.liveflighttrackerapp.data.api.AirPlanesService
import com.example.liveflighttrackerapp.data.api.AirportsService
import com.example.liveflighttrackerapp.data.api.CitiesService
import com.example.liveflighttrackerapp.data.api.FlightApiService
import com.example.liveflighttrackerapp.data.api.NearbyService
import com.example.liveflighttrackerapp.data.api.SchedulesFlightService
import com.example.liveflighttrackerapp.data.api.StaticAirLineService
import com.example.liveflighttrackerapp.data.db.AppDatabase
import com.example.liveflighttrackerapp.data.repository.airLine.StaticAirLineRepositoryImpl
import com.example.liveflighttrackerapp.data.repository.airLine.dataSource.StaticAirLineCacheDataSource
import com.example.liveflighttrackerapp.data.repository.airLine.dataSource.StaticAirLineRemoteDataSource
import com.example.liveflighttrackerapp.data.repository.airLine.dataSource.StaticAirLineRoomDataSource
import com.example.liveflighttrackerapp.data.repository.airLine.dataSourceImpl.StaticAirLineCacheDataSourceImpl
import com.example.liveflighttrackerapp.data.repository.airLine.dataSourceImpl.StaticAirLineRemoteDataSourceImpl
import com.example.liveflighttrackerapp.data.repository.airLine.dataSourceImpl.StaticAirLineRoomDataSourceImpl
import com.example.liveflighttrackerapp.data.repository.airplane.AirPlanesRepositoryImpl
import com.example.liveflighttrackerapp.data.repository.airplane.datasource.AirPlanesCacheDataSource
import com.example.liveflighttrackerapp.data.repository.airplane.datasource.AirPlanesRemoteDataSource
import com.example.liveflighttrackerapp.data.repository.airplane.datasource.AirPlanesRoomDataSource
import com.example.liveflighttrackerapp.data.repository.airplane.datasourceImpl.AirPlanesCacheDataSourceImpl
import com.example.liveflighttrackerapp.data.repository.airplane.datasourceImpl.AirPlanesRemoteDataSourceImpl
import com.example.liveflighttrackerapp.data.repository.airplane.datasourceImpl.AirPlanesRoomDataSourceImpl
import com.example.liveflighttrackerapp.data.repository.airports.AirPortsRepositoryImpl
import com.example.liveflighttrackerapp.data.repository.airports.datasource.AirPortsCacheDataSource
import com.example.liveflighttrackerapp.data.repository.airports.datasource.AirPortsRemoteDataSource
import com.example.liveflighttrackerapp.data.repository.airports.datasource.AirPortsRoomDataSource
import com.example.liveflighttrackerapp.data.repository.airports.datasourceImpl.AirPortsCacheDataSourceImpl
import com.example.liveflighttrackerapp.data.repository.airports.datasourceImpl.AirPortsRemoteDataSourceImpl
import com.example.liveflighttrackerapp.data.repository.airports.datasourceImpl.AirPortsRoomDataSourceImpl
import com.example.liveflighttrackerapp.data.repository.cities.CitiesRepositoryImpl
import com.example.liveflighttrackerapp.data.repository.cities.datasource.CitiesCacheDataSource
import com.example.liveflighttrackerapp.data.repository.cities.datasource.CitiesRemoteDataSource
import com.example.liveflighttrackerapp.data.repository.cities.datasource.CitiesRoomDataSource
import com.example.liveflighttrackerapp.data.repository.cities.datasourceImpl.CitiesCacheDataSourceImpl
import com.example.liveflighttrackerapp.data.repository.cities.datasourceImpl.CitiesRemoteDataSourceImpl
import com.example.liveflighttrackerapp.data.repository.cities.datasourceImpl.CitiesRoomDataSourceImpl
import com.example.liveflighttrackerapp.data.repository.flight.LiveFlightRepositoryImpl
import com.example.liveflighttrackerapp.data.repository.flight.datasource.LiveFlightCacheDataSource
import com.example.liveflighttrackerapp.data.repository.flight.datasource.LiveFlightRemoteDataSource
import com.example.liveflighttrackerapp.data.repository.flight.datasource.LiveFlightRoomDataSource
import com.example.liveflighttrackerapp.data.repository.flight.datasourceImpl.LiveFlightCacheDataSourceImpl
import com.example.liveflighttrackerapp.data.repository.flight.datasourceImpl.LiveFlightRemoteDataSourceImpl
import com.example.liveflighttrackerapp.data.repository.flight.datasourceImpl.LiveFlightRoomDataSourceImpl
import com.example.liveflighttrackerapp.data.repository.nearby.NearByAirportsAirPortsRepositoryImpl
import com.example.liveflighttrackerapp.data.repository.nearby.datasource.NearByAirPortsCacheDataSource
import com.example.liveflighttrackerapp.data.repository.nearby.datasource.NearByAirPortsRemoteDataSource
import com.example.liveflighttrackerapp.data.repository.nearby.datasource.NearByAirPortsRoomDataSource
import com.example.liveflighttrackerapp.data.repository.nearby.datasourceImpl.NearByAirPortsCacheDataSourceImpl
import com.example.liveflighttrackerapp.data.repository.nearby.datasourceImpl.NearByAirPortsRemoteDataSourceImpl
import com.example.liveflighttrackerapp.data.repository.nearby.datasourceImpl.NearByAirPortsRoomDataSourceImpl
import com.example.liveflighttrackerapp.data.repository.scheduleFlight.ScheduleFlightRepositoryImpl
import com.example.liveflighttrackerapp.data.repository.scheduleFlight.dataSource.ScheduleFlightCacheDataSource
import com.example.liveflighttrackerapp.data.repository.scheduleFlight.dataSource.ScheduleFlightRemoteDataSource
import com.example.liveflighttrackerapp.data.repository.scheduleFlight.dataSource.ScheduleFlightRoomDataSource
import com.example.liveflighttrackerapp.data.repository.scheduleFlight.dataSourceImpl.ScheduleFlightCacheDataSourceImpl
import com.example.liveflighttrackerapp.data.repository.scheduleFlight.dataSourceImpl.ScheduleFlightRemoteDataSourceImpl
import com.example.liveflighttrackerapp.data.repository.scheduleFlight.dataSourceImpl.ScheduleFlightRoomDataSourceImpl
import com.example.liveflighttrackerapp.domain.repository.AirPlanesRepository
import com.example.liveflighttrackerapp.domain.repository.AirPortsRepository
import com.example.liveflighttrackerapp.domain.repository.CitiesRepository
import com.example.liveflighttrackerapp.domain.repository.LiveFlightRepository
import com.example.liveflighttrackerapp.domain.repository.NearByAirPortsRepository
import com.example.liveflighttrackerapp.domain.repository.ScheduleFlightRepository
import com.example.liveflighttrackerapp.domain.repository.StaticAirLineRepository
import com.example.liveflighttrackerapp.domain.usecase.GetAirPlanesUseCase
import com.example.liveflighttrackerapp.domain.usecase.GetAirPortsUseCase
import com.example.liveflighttrackerapp.domain.usecase.GetCitiesUseCase
import com.example.liveflighttrackerapp.domain.usecase.GetLiveFlightUseCase
import com.example.liveflighttrackerapp.domain.usecase.GetNearByAirPortsUseCase
import com.example.liveflighttrackerapp.domain.usecase.GetScheduleFlightUseCase
import com.example.liveflighttrackerapp.domain.usecase.GetStaticAirLineUseCase
import com.example.liveflighttrackerapp.presentation.googleMap.MyGoogleMap
import com.example.liveflighttrackerapp.presentation.viewmodels.FlightAppViewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    single {
        Room.databaseBuilder(
            get<Application>(),
            AppDatabase::class.java,
            "flightApp"
        ).fallbackToDestructiveMigration(false).build()
    }

    single { get<AppDatabase>().liveFlightDao() }
    single { get<AppDatabase>().staticAirLineDao() }
    single { get<AppDatabase>().schedulesFlightDao() }
    single { get<AppDatabase>().airportsDao() }
    single { get<AppDatabase>().nearByDao() }
    single { get<AppDatabase>().citiesDao() }
    single { get<AppDatabase>().airPlaneDao() }
    single { get<AppDatabase>().trackedFlightDao() }

    single<LiveFlightRoomDataSource> { LiveFlightRoomDataSourceImpl(get()) }
    single<LiveFlightCacheDataSource> { LiveFlightCacheDataSourceImpl() }
    single<LiveFlightRemoteDataSource> { LiveFlightRemoteDataSourceImpl(get()) }

    single { get<Retrofit>().create(FlightApiService::class.java) }
    single { get<Retrofit>().create(StaticAirLineService::class.java) }
    single { get<Retrofit>().create(SchedulesFlightService::class.java) }
    single { get<Retrofit>().create(AirportsService::class.java) }
    single { get<Retrofit>().create(NearbyService::class.java) }
    single { get<Retrofit>().create(CitiesService::class.java) }
    single { get<Retrofit>().create(AirPlanesService::class.java) }

    single {
        okhttp3.OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val originalUrl = original.url

                val newUrl = originalUrl.newBuilder()
                    .addQueryParameter("key", "2eb823-ea25c3")
                    .build()

                val newRequest = original.newBuilder().url(newUrl).build()
                chain.proceed(newRequest)
            }
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://aviation-edge.com/v2/public/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }



    single<LiveFlightRepository> {
        LiveFlightRepositoryImpl(
            liveFlightCacheDataSource = get(),
            liveFlightRemoteDataSource = get(),
            liveFlightRoomDataSource = get()
        )
    }

    single<ScheduleFlightRepository> { ScheduleFlightRepositoryImpl(get(), get(), get()) }
    single<ScheduleFlightRemoteDataSource> { ScheduleFlightRemoteDataSourceImpl(get()) }
    single<ScheduleFlightCacheDataSource> { ScheduleFlightCacheDataSourceImpl() }
    single<ScheduleFlightRoomDataSource> { ScheduleFlightRoomDataSourceImpl(get()) }

    single { GetStaticAirLineUseCase(get()) }
    single { GetLiveFlightUseCase(get()) }
    single { GetScheduleFlightUseCase(get()) }

    single { MyGoogleMap() }
    single<StaticAirLineRepository> { StaticAirLineRepositoryImpl(get(), get(), get()) }
    single<StaticAirLineRemoteDataSource> { StaticAirLineRemoteDataSourceImpl(get()) }
    single<StaticAirLineRoomDataSource> { StaticAirLineRoomDataSourceImpl(get()) }
    single<StaticAirLineCacheDataSource> { StaticAirLineCacheDataSourceImpl() }

    single { GetAirPortsUseCase(get()) }
    single<AirPortsRepository> { AirPortsRepositoryImpl(get(), get(), get()) }
    single<AirPortsRoomDataSource> { AirPortsRoomDataSourceImpl(get()) }
    single<AirPortsCacheDataSource> { AirPortsCacheDataSourceImpl() }
    single<AirPortsRemoteDataSource> { AirPortsRemoteDataSourceImpl(get()) }

    single { GetCitiesUseCase(get()) }
    single<CitiesRepository> { CitiesRepositoryImpl(get(), get(), get()) }
    single<CitiesRoomDataSource> { CitiesRoomDataSourceImpl(get()) }
    single<CitiesRemoteDataSource> { CitiesRemoteDataSourceImpl(get()) }
    single<CitiesCacheDataSource> { CitiesCacheDataSourceImpl() }


    single { GetAirPlanesUseCase(get()) }
    single<AirPlanesRepository> { AirPlanesRepositoryImpl(get(), get(), get()) }
    single<AirPlanesRemoteDataSource> { AirPlanesRemoteDataSourceImpl(get()) }
    single<AirPlanesRoomDataSource> { AirPlanesRoomDataSourceImpl(get()) }
    single<AirPlanesCacheDataSource> { AirPlanesCacheDataSourceImpl() }

    single<NearByAirPortsRepository> { NearByAirportsAirPortsRepositoryImpl(get(), get(), get()) }
    single<NearByAirPortsCacheDataSource> { NearByAirPortsCacheDataSourceImpl() }
    single<NearByAirPortsRemoteDataSource> { NearByAirPortsRemoteDataSourceImpl(get()) }
    single<NearByAirPortsRoomDataSource> { NearByAirPortsRoomDataSourceImpl(get()) }
    single { GetNearByAirPortsUseCase(get()) }
    single { FlightAppViewModel(get(), get(), get(), get(), get(), get(), get()) }
}

