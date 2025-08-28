package com.example.flighttrackerappnew.presentation.di

import android.app.Application
import androidx.room.Room
import com.example.flighttrackerappnew.data.api.AirPlanesService
import com.example.flighttrackerappnew.data.api.AirportsService
import com.example.flighttrackerappnew.data.api.CitiesService
import com.example.flighttrackerappnew.data.api.FlightApiService
import com.example.flighttrackerappnew.data.api.FlightSchedulesService
import com.example.flighttrackerappnew.data.api.FutureScheduleFlightService
import com.example.flighttrackerappnew.data.api.NearbyService
import com.example.flighttrackerappnew.data.api.StaticAirLineService
import com.example.flighttrackerappnew.BuildConfig
import com.example.flighttrackerappnew.data.api.IpService
import com.example.flighttrackerappnew.data.db.AppDatabase
import com.example.flighttrackerappnew.data.repository.airLine.StaticAirLineRepositoryImpl
import com.example.flighttrackerappnew.data.repository.airLine.dataSource.StaticAirLineCacheDataSource
import com.example.flighttrackerappnew.data.repository.airLine.dataSource.StaticAirLineRemoteDataSource
import com.example.flighttrackerappnew.data.repository.airLine.dataSource.StaticAirLineRoomDataSource
import com.example.flighttrackerappnew.data.repository.airLine.dataSourceImpl.StaticAirLineCacheDataSourceImpl
import com.example.flighttrackerappnew.data.repository.airLine.dataSourceImpl.StaticAirLineRemoteDataSourceImpl
import com.example.flighttrackerappnew.data.repository.airLine.dataSourceImpl.StaticAirLineRoomDataSourceImpl
import com.example.flighttrackerappnew.data.repository.airplane.AirPlanesRepositoryImpl
import com.example.flighttrackerappnew.data.repository.airplane.datasource.AirPlanesCacheDataSource
import com.example.flighttrackerappnew.data.repository.airplane.datasource.AirPlanesRemoteDataSource
import com.example.flighttrackerappnew.data.repository.airplane.datasource.AirPlanesRoomDataSource
import com.example.flighttrackerappnew.data.repository.airplane.datasourceImpl.AirPlanesCacheDataSourceImpl
import com.example.flighttrackerappnew.data.repository.airplane.datasourceImpl.AirPlanesRemoteDataSourceImpl
import com.example.flighttrackerappnew.data.repository.airplane.datasourceImpl.AirPlanesRoomDataSourceImpl
import com.example.flighttrackerappnew.data.repository.airports.AirPortsRepositoryImpl
import com.example.flighttrackerappnew.data.repository.airports.datasource.AirPortsCacheDataSource
import com.example.flighttrackerappnew.data.repository.airports.datasource.AirPortsRemoteDataSource
import com.example.flighttrackerappnew.data.repository.airports.datasource.AirPortsRoomDataSource
import com.example.flighttrackerappnew.data.repository.airports.datasourceImpl.AirPortsCacheDataSourceImpl
import com.example.flighttrackerappnew.data.repository.airports.datasourceImpl.AirPortsRemoteDataSourceImpl
import com.example.flighttrackerappnew.data.repository.airports.datasourceImpl.AirPortsRoomDataSourceImpl
import com.example.flighttrackerappnew.data.repository.billing.BillingRepository
import com.example.flighttrackerappnew.data.repository.cities.CitiesRepositoryImpl
import com.example.flighttrackerappnew.data.repository.cities.datasource.CitiesCacheDataSource
import com.example.flighttrackerappnew.data.repository.cities.datasource.CitiesRemoteDataSource
import com.example.flighttrackerappnew.data.repository.cities.datasource.CitiesRoomDataSource
import com.example.flighttrackerappnew.data.repository.cities.datasourceImpl.CitiesCacheDataSourceImpl
import com.example.flighttrackerappnew.data.repository.cities.datasourceImpl.CitiesRemoteDataSourceImpl
import com.example.flighttrackerappnew.data.repository.cities.datasourceImpl.CitiesRoomDataSourceImpl
import com.example.flighttrackerappnew.data.repository.flight.LiveFlightRepositoryImpl
import com.example.flighttrackerappnew.data.repository.flight.datasource.LiveFlightCacheDataSource
import com.example.flighttrackerappnew.data.repository.flight.datasource.LiveFlightRemoteDataSource
import com.example.flighttrackerappnew.data.repository.flight.datasource.LiveFlightRoomDataSource
import com.example.flighttrackerappnew.data.repository.flight.datasourceImpl.LiveFlightCacheDataSourceImpl
import com.example.flighttrackerappnew.data.repository.flight.datasourceImpl.LiveFlightRemoteDataSourceImpl
import com.example.flighttrackerappnew.data.repository.flight.datasourceImpl.LiveFlightRoomDataSourceImpl
import com.example.flighttrackerappnew.data.repository.flightSchedule.FlightScheduleRepositoryImpl
import com.example.flighttrackerappnew.data.repository.flightSchedule.dataSource.FlightScheduleCacheDataSource
import com.example.flighttrackerappnew.data.repository.flightSchedule.dataSource.FlightScheduleRemoteDataSource
import com.example.flighttrackerappnew.data.repository.flightSchedule.dataSource.FlightScheduleRoomDataSource
import com.example.flighttrackerappnew.data.repository.flightSchedule.dataSourceImpl.FlightScheduleCacheDataSourceImpl
import com.example.flighttrackerappnew.data.repository.flightSchedule.dataSourceImpl.FlightScheduleRemoteDataSourceImpl
import com.example.flighttrackerappnew.data.repository.flightSchedule.dataSourceImpl.FlightScheduleRoomDataSourceImpl
import com.example.flighttrackerappnew.data.repository.futureSchedule.FutureScheduleRepositoryImpl
import com.example.flighttrackerappnew.data.repository.futureSchedule.dataSource.FutureScheduleCacheDataSource
import com.example.flighttrackerappnew.data.repository.futureSchedule.dataSource.FutureScheduleRemoteDataSource
import com.example.flighttrackerappnew.data.repository.futureSchedule.dataSource.FutureScheduleRoomDataSource
import com.example.flighttrackerappnew.data.repository.futureSchedule.dataSourceImpl.FutureScheduleCacheDataSourceImpl
import com.example.flighttrackerappnew.data.repository.futureSchedule.dataSourceImpl.FutureScheduleRemoteDataSourceImpl
import com.example.flighttrackerappnew.data.repository.futureSchedule.dataSourceImpl.FutureScheduleRoomDataSourceImpl
import com.example.flighttrackerappnew.data.repository.nearby.NearByAirportsAirPortsRepositoryImpl
import com.example.flighttrackerappnew.data.repository.nearby.datasource.NearByAirPortsCacheDataSource
import com.example.flighttrackerappnew.data.repository.nearby.datasource.NearByAirPortsRemoteDataSource
import com.example.flighttrackerappnew.data.repository.nearby.datasource.NearByAirPortsRoomDataSource
import com.example.flighttrackerappnew.data.repository.nearby.datasourceImpl.NearByAirPortsCacheDataSourceImpl
import com.example.flighttrackerappnew.data.repository.nearby.datasourceImpl.NearByAirPortsRemoteDataSourceImpl
import com.example.flighttrackerappnew.data.repository.nearby.datasourceImpl.NearByAirPortsRoomDataSourceImpl
import com.example.flighttrackerappnew.domain.repository.AirPlanesRepository
import com.example.flighttrackerappnew.domain.repository.AirPortsRepository
import com.example.flighttrackerappnew.domain.repository.CitiesRepository
import com.example.flighttrackerappnew.domain.repository.FlightScheduleRepository
import com.example.flighttrackerappnew.domain.repository.FutureScheduleFlightRepository
import com.example.flighttrackerappnew.domain.repository.LiveFlightRepository
import com.example.flighttrackerappnew.domain.repository.NearByAirPortsRepository
import com.example.flighttrackerappnew.domain.repository.StaticAirLineRepository
import com.example.flighttrackerappnew.domain.usecase.BillingUseCase
import com.example.flighttrackerappnew.domain.usecase.GetAirPlanesUseCase
import com.example.flighttrackerappnew.domain.usecase.GetAirPortsUseCase
import com.example.flighttrackerappnew.domain.usecase.GetCitiesUseCase
import com.example.flighttrackerappnew.domain.usecase.GetFlightScheduleUseCase
import com.example.flighttrackerappnew.domain.usecase.GetFutureScheduleFlightUseCase
import com.example.flighttrackerappnew.domain.usecase.GetLiveFlightUseCase
import com.example.flighttrackerappnew.domain.usecase.GetNearByAirPortsUseCase
import com.example.flighttrackerappnew.domain.usecase.GetStaticAirLineUseCase
import com.example.flighttrackerappnew.presentation.adManager.banner.BannerAdManager
import com.example.flighttrackerappnew.presentation.adManager.NativeAd1LangScreen1
import com.example.flighttrackerappnew.presentation.adManager.NativeAd1LangScreen2
import com.example.flighttrackerappnew.presentation.adManager.NativeAd2LangScreen1
import com.example.flighttrackerappnew.presentation.adManager.NativeAd2LangScreen2
import com.example.flighttrackerappnew.presentation.adManager.NativeAdHome
import com.example.flighttrackerappnew.presentation.adManager.controller.NativeAdController
import com.example.flighttrackerappnew.presentation.adManager.NativeAdMapStyle
import com.example.flighttrackerappnew.presentation.adManager.NativeAdOnb1
import com.example.flighttrackerappnew.presentation.adManager.NativeAdOnb2
import com.example.flighttrackerappnew.presentation.adManager.NativeAdOnb4
import com.example.flighttrackerappnew.presentation.adManager.NativeAdOther
import com.example.flighttrackerappnew.presentation.adManager.NativeAdWelcomeScreen
import com.example.flighttrackerappnew.presentation.adManager.OnBoardingFullNativeAd1
import com.example.flighttrackerappnew.presentation.adManager.OnBoardingFullNativeAd2
import com.example.flighttrackerappnew.presentation.adManager.rewarded.RewardedAdManager
import com.example.flighttrackerappnew.presentation.getAllApsData.DataCollector
import com.example.flighttrackerappnew.presentation.googleMap.MyGoogleMap
import com.example.flighttrackerappnew.presentation.googleMap.MyGoogleMapNearAirports
import com.example.flighttrackerappnew.presentation.googleMap.MyGoogleMapRoute
import com.example.flighttrackerappnew.presentation.helper.BaseConfig
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.viewmodels.FlightAppViewModel
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {
    single<BaseConfig> { Config(get()) }
    single { Config(get()) }

    single {
        Room.databaseBuilder(
            get<Application>(),
            AppDatabase::class.java,
            "flightApp"
        ).fallbackToDestructiveMigration(true).build()
    }

    single { DataCollector() }
    single { BillingRepository(get(), get()) }
    single { BillingUseCase(get()) }
    single { get<AppDatabase>().liveFlightDao() }
    single { get<AppDatabase>().staticAirLineDao() }
    single { get<AppDatabase>().schedulesFlightDao() }
    single { get<AppDatabase>().airportsDao() }
    single { get<AppDatabase>().nearByDao() }
    single { get<AppDatabase>().citiesDao() }
    single { get<AppDatabase>().airPlaneDao() }
    single { get<AppDatabase>().trackedFlightDao() }
    single { get<AppDatabase>().followLiveFlightDao() }
    single { get<AppDatabase>().favFlightDao() }
    single { get<AppDatabase>().futureFlightDao() }

    single<LiveFlightRoomDataSource> { LiveFlightRoomDataSourceImpl(get()) }
    single<LiveFlightCacheDataSource> { LiveFlightCacheDataSourceImpl() }
    single<LiveFlightRemoteDataSource> { LiveFlightRemoteDataSourceImpl(get()) }

    single { get<Retrofit>(named("aviationRetrofit")).create(FlightApiService::class.java) }
    single { get<Retrofit>(named("aviationRetrofit")).create(StaticAirLineService::class.java) }
    single { get<Retrofit>(named("aviationRetrofit")).create(FlightSchedulesService::class.java) }
    single { get<Retrofit>(named("aviationRetrofit")).create(AirportsService::class.java) }
    single { get<Retrofit>(named("aviationRetrofit")).create(NearbyService::class.java) }
    single { get<Retrofit>(named("aviationRetrofit")).create(CitiesService::class.java) }
    single { get<Retrofit>(named("aviationRetrofit")).create(AirPlanesService::class.java) }
    single { get<Retrofit>(named("aviationRetrofit")).create(FutureScheduleFlightService::class.java) }

    single {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val originalUrl = original.url

                val newUrl = originalUrl.newBuilder()
                    .addQueryParameter("key", BuildConfig.API_KEY)
                    .build()

                val newRequest = original.newBuilder().url(newUrl).build()
                chain.proceed(newRequest)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single(named("aviationRetrofit")) {
        Retrofit.Builder()
            .baseUrl("https://aviation-edge.com/v2/public/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

// For IP-API (separate client, no interceptor needed)
//    single(named("ipRetrofit")) {
//        Retrofit.Builder()
//            .baseUrl("http://ip-api.com/") // no interceptor needed
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }

// IP Service
//    single { get<Retrofit>(named("ipRetrofit")).create(IpService::class.java) }


    single<LiveFlightRepository> {
        LiveFlightRepositoryImpl(
            liveFlightCacheDataSource = get(),
            liveFlightRemoteDataSource = get(),
            liveFlightRoomDataSource = get()
        )
    }

    single<FlightScheduleRepository> { FlightScheduleRepositoryImpl(get(), get(), get()) }
    single<FlightScheduleRemoteDataSource> { FlightScheduleRemoteDataSourceImpl(get()) }
    single<FlightScheduleCacheDataSource> { FlightScheduleCacheDataSourceImpl() }
    single<FlightScheduleRoomDataSource> { FlightScheduleRoomDataSourceImpl(get()) }

    single { GetStaticAirLineUseCase(get()) }
    single { GetLiveFlightUseCase(get()) }
    single { GetFlightScheduleUseCase(get()) }

    factory { BannerAdManager() }
    single { NativeAd1LangScreen1() }
    single { NativeAd2LangScreen1() }
    single { NativeAd1LangScreen2() }
    single { NativeAd2LangScreen2() }
    single { NativeAdOnb2() }
    single { NativeAdOnb1() }
    single { NativeAdOnb4() }
    single { NativeAdWelcomeScreen() }
    single { NativeAdMapStyle() }
    single { OnBoardingFullNativeAd1() }
    single { OnBoardingFullNativeAd2() }
    factory { NativeAdOther() }
    factory { NativeAdHome() }
    factory { RewardedAdManager() }
    single {
        NativeAdController(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    single { MyGoogleMap() }
    single { MyGoogleMapRoute() }
    single { MyGoogleMapNearAirports() }
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

    single { GetFutureScheduleFlightUseCase(get()) }
    single<FutureScheduleFlightRepository> { FutureScheduleRepositoryImpl(get(), get(), get()) }
    single<FutureScheduleRoomDataSource> { FutureScheduleRoomDataSourceImpl(get()) }
    single<FutureScheduleCacheDataSource> { FutureScheduleCacheDataSourceImpl() }
    single<FutureScheduleRemoteDataSource> { FutureScheduleRemoteDataSourceImpl(get()) }

    single<NearByAirPortsRepository> { NearByAirportsAirPortsRepositoryImpl(get(), get(), get()) }
    single<NearByAirPortsCacheDataSource> { NearByAirPortsCacheDataSourceImpl() }
    single<NearByAirPortsRemoteDataSource> { NearByAirPortsRemoteDataSourceImpl(get()) }
    single<NearByAirPortsRoomDataSource> { NearByAirPortsRoomDataSourceImpl(get()) }
    single { GetNearByAirPortsUseCase(get()) }
    single {
        FlightAppViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
}

