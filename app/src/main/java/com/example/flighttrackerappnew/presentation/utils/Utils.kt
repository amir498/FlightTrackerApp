package com.example.flighttrackerappnew.presentation.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.core.net.toUri
import com.example.flighttrackerappnew.data.model.fav.FavFlightData
import com.example.flighttrackerappnew.data.model.follow.FollowFlightData
import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

fun formatTo12HourTime(input: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)
        val outputFormat = SimpleDateFormat("h:mm a", Locale.US)
        val date: Date = inputFormat.parse(input)!!
        outputFormat.format(date)
    } catch (_: Exception) {
        "N/A"
    }
}

fun getNetworkCountryIso(context: Context): String? {
    val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return tm.networkCountryIso?.uppercase(Locale.ROOT)
}

fun getSimCountryIso(context: Context): String? {
    val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return tm.simCountryIso?.uppercase(Locale.ROOT)
}

fun getCurrentCountryLatLon(context: Context): Pair<Double, Double>? {
    val countries = context.loadCountries()

    val countryIso = getNetworkCountryIso(context)
        ?: getSimCountryIso(context)
        ?: Locale.getDefault().country.uppercase(Locale.ROOT)

    val country = countries.find { it.iso2.equals(countryIso, ignoreCase = true) }

    return country?.let {
        Pair(it.latitude.toDouble(), it.longitude.toDouble())
    }
}

fun getFlightProgressPercent(dep: String, arr: String): Int {
    if (dep == "N/A" || arr == "N/A") return 0

    val baseDate = "01/01/1970"
    val fullFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault())
    fullFormat.isLenient = false

    return try {
        val depTime = fullFormat.parse("$baseDate $dep") ?: return 0
        var arrTime = fullFormat.parse("$baseDate $arr") ?: return 0
        val now = Date()

        if (arrTime.before(depTime)) {
            val calendar = Calendar.getInstance()
            calendar.time = arrTime
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            arrTime = calendar.time
        }

        if (now.time >= arrTime.time) return 100

        val totalDuration = arrTime.time - depTime.time
        val elapsed = now.time - depTime.time

        if (totalDuration <= 0) return 0
        if (elapsed <= 0) return 0

        val progress = (elapsed.toDouble() / totalDuration) * 100
        progress.coerceIn(0.0, 100.0).toInt()

    } catch (_: Exception) {
        0
    }
}

fun getTimeDifference(dep: String, arr: String): String {
    if (dep == "N/A" || arr == "N/A") return "N/A"

    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
    format.isLenient = false

    return try {
        val depTime = format.parse(dep)
        val arrTime = format.parse(arr)

        if (depTime == null || arrTime == null) return "N/A"

        var diff = arrTime.time - depTime.time

        if (diff < 0) diff += TimeUnit.DAYS.toMillis(1)

        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff) % 60

        when {
            hours > 0 && minutes > 0 -> "$hours hrs $minutes mins"
            hours > 0 -> "$hours hrs"
            minutes > 0 -> "$minutes mins"
            else -> "$seconds secs"
        }

    } catch (_: Exception) {
        "N/A"
    }
}

fun formatIsoDate(input: String): String {
    try {
        Log.d("MY--TAG", "formatIsoDate:$input")
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        isoFormat.timeZone = TimeZone.getTimeZone("UTC")

        val date: Date = isoFormat.parse(input)!!

        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return outputFormat.format(date)
    } catch (_: RuntimeException) {
        return "N/A"
    } catch (_: ParseException) {
        return "N/A"
    }
}

fun openGoogleMap(lat: String, long: String, context: Context) {
    try {
        val latitude = lat.toDouble()
        val longitude = long.toDouble()

        val label = "Saved Location"
        val uri = "http://maps.google.com/maps?q=loc:$latitude,$longitude($label)".toUri()

        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            context.showToast("Google Maps not found")
        }
    } catch (_: NumberFormatException) {
        context.showToast("N/A")
    }
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
fun isTiramisuPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
fun isRedVelvetCakePlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

fun FavFlightData.toFullDetailData(): FullDetailFlightData {
    return FullDetailFlightData(
        id = id,
        flightNo = flightNo,
        depIataCode = depIataCode,
        arrIataCode = arrIataCode,
        arrAirportName = arrAirportName,
        depAirportName = depAirportName,
        arrCity = arrCity,
        depCity = depCity,
        nameAirport = nameAirport,
        callSign = callSign,
        scheduledArrTime = scheduledArrTime,
        scheduledDepTime = scheduledDepTime,
        actualDepTime = actualDepTime,
        estimatedArrTime = estimatedArrTime,
        flightIataNumber = flightIataNumber,
        airlineName = airlineName,
        flightIcaoNo = flightIcaoNo,
        terminal = terminal,
        gate = gate,
        delay = delay,
        scheduled = scheduled,
        altitude = altitude,
        direction = direction,
        latitude = latitude,
        longitude = longitude,
        hSpeed = hSpeed,
        vSpeed = vSpeed,
        status = status,
        squawk = squawk,
        modelName = modelName,
        modelCode = modelCode,
        airCraftType = airCraftType,
        regNo = regNo,
        iataModel = iataModel,
        icaoHex = icaoHex,
        productionLine = productionLine,
        series = series,
        lineNumber = lineNumber,
        constructionNo = constructionNo,
        firstFlight = firstFlight,
        deliveryDate = deliveryDate,
        rolloutDate = rolloutDate,
        currentOwner = currentOwner,
        planeStatus = planeStatus,
        airLineIataCode = airLineIataCode,
        airLineICaoCode = airLineICaoCode,
        airPlaneIataCode = airPlaneIataCode,
        engineCount = engineCount,
        regDate = regDate,
        progress = progress,
        type = type
    )
}

fun FollowFlightData.toFullDetail(): FullDetailFlightData {
    return FullDetailFlightData(
        id = id,
        flightNo = flightNo,
        depIataCode = depIataCode,
        arrIataCode = arrIataCode,
        arrAirportName = arrAirportName,
        depAirportName = depAirportName,
        arrCity = arrCity,
        depCity = depCity,
        nameAirport = nameAirport,
        callSign = callSign,
        scheduledArrTime = scheduledArrTime,
        scheduledDepTime = scheduledDepTime,
        actualDepTime = actualDepTime,
        estimatedArrTime = estimatedArrTime,
        flightIataNumber = flightIataNumber,
        airlineName = airlineName,
        flightIcaoNo = flightIcaoNo,
        terminal = terminal,
        gate = gate,
        delay = delay,
        scheduled = scheduled,
        altitude = altitude,
        direction = direction,
        latitude = latitude,
        longitude = longitude,
        hSpeed = hSpeed,
        vSpeed = vSpeed,
        status = status,
        squawk = squawk,
        modelName = modelName,
        modelCode = modelCode,
        airCraftType = airCraftType,
        regNo = regNo,
        iataModel = iataModel,
        icaoHex = icaoHex,
        productionLine = productionLine,
        series = series,
        lineNumber = lineNumber,
        constructionNo = constructionNo,
        firstFlight = firstFlight,
        deliveryDate = deliveryDate,
        rolloutDate = rolloutDate,
        currentOwner = currentOwner,
        planeStatus = planeStatus,
        airLineIataCode = airLineIataCode,
        airLineICaoCode = airLineICaoCode,
        airPlaneIataCode = airPlaneIataCode,
        engineCount = engineCount,
        regDate = regDate,
        progress = progress,
        type = type
    )
}

fun FullDetailFlightData.toFollowFlightData(): FollowFlightData {
    return FollowFlightData(
        id = id,
        flightNo = flightNo,
        depIataCode = depIataCode,
        arrIataCode = arrIataCode,
        arrAirportName = arrAirportName,
        depAirportName = depAirportName,
        arrCity = arrCity,
        depCity = depCity,
        nameAirport = nameAirport,
        callSign = callSign,
        scheduledArrTime = scheduledArrTime,
        scheduledDepTime = scheduledDepTime,
        actualDepTime = actualDepTime,
        estimatedArrTime = estimatedArrTime,
        flightIataNumber = flightIataNumber,
        airlineName = airlineName,
        flightIcaoNo = flightIcaoNo,
        terminal = terminal,
        gate = gate,
        delay = delay,
        scheduled = scheduled,
        altitude = altitude,
        direction = direction,
        latitude = latitude,
        longitude = longitude,
        hSpeed = hSpeed,
        vSpeed = vSpeed,
        status = status,
        squawk = squawk,
        modelName = modelName,
        modelCode = modelCode,
        airCraftType = airCraftType,
        regNo = regNo,
        iataModel = iataModel,
        icaoHex = icaoHex,
        productionLine = productionLine,
        series = series,
        lineNumber = lineNumber,
        constructionNo = constructionNo,
        firstFlight = firstFlight,
        deliveryDate = deliveryDate,
        rolloutDate = rolloutDate,
        currentOwner = currentOwner,
        planeStatus = planeStatus,
        airLineIataCode = airLineIataCode,
        airLineICaoCode = airLineICaoCode,
        airPlaneIataCode = airPlaneIataCode,
        engineCount = engineCount,
        regDate = regDate,
        progress = progress,
        type = type
    )
}

fun FullDetailFlightData.toFavFlightData(): FavFlightData {
    return FavFlightData(
        id = id,
        flightNo = flightNo,
        depIataCode = depIataCode,
        arrIataCode = arrIataCode,
        arrAirportName = arrAirportName,
        depAirportName = depAirportName,
        arrCity = arrCity,
        depCity = depCity,
        nameAirport = nameAirport,
        callSign = callSign,
        scheduledArrTime = scheduledArrTime,
        scheduledDepTime = scheduledDepTime,
        actualDepTime = actualDepTime,
        estimatedArrTime = estimatedArrTime,
        flightIataNumber = flightIataNumber,
        airlineName = airlineName,
        flightIcaoNo = flightIcaoNo,
        terminal = terminal,
        gate = gate,
        delay = delay,
        scheduled = scheduled,
        altitude = altitude,
        direction = direction,
        latitude = latitude,
        longitude = longitude,
        hSpeed = hSpeed,
        vSpeed = vSpeed,
        status = status,
        squawk = squawk,
        modelName = modelName,
        modelCode = modelCode,
        airCraftType = airCraftType,
        regNo = regNo,
        iataModel = iataModel,
        icaoHex = icaoHex,
        productionLine = productionLine,
        series = series,
        lineNumber = lineNumber,
        constructionNo = constructionNo,
        firstFlight = firstFlight,
        deliveryDate = deliveryDate,
        rolloutDate = rolloutDate,
        currentOwner = currentOwner,
        planeStatus = planeStatus,
        airLineIataCode = airLineIataCode,
        airLineICaoCode = airLineICaoCode,
        airPlaneIataCode = airPlaneIataCode,
        engineCount = engineCount,
        regDate = regDate,
        progress = progress,
        type = type
    )
}


