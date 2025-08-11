package com.example.liveflighttrackerapp.presentation.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.example.liveflighttrackerapp.databinding.FragmentFlightsDetailsBinding
import com.example.liveflighttrackerapp.presentation.activities.MainActivity
import com.example.liveflighttrackerapp.presentation.utils.FullDetailsFlightData
import com.example.liveflighttrackerapp.presentation.utils.formatTo12HourTime

class FlightsDetailsFragment(context: Context, attr: AttributeSet) :
    MyFragment<FragmentFlightsDetailsBinding>(context, attr) {

    var touchDownY = -1
    var ignoreTouches = false
    private var lastTouchCoords = Pair(0f, 0f)

    @SuppressLint("ClickableViewAccessibility")
    override fun setupFragment(activity: MainActivity) {
        this.activity = activity
        this.binding = FragmentFlightsDetailsBinding.bind(this)

        binding.discreteSeekBar.setOnTouchListener { _, _ -> true }
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return true
        }

        var shouldParentIntercept = false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchDownY = event.y.toInt()
            }

            MotionEvent.ACTION_MOVE -> {
                if (ignoreTouches) {
                    if (lastTouchCoords.first != event.x || lastTouchCoords.second != event.y) {
                        touchDownY = -1
                        return true
                    }
                }

                if (touchDownY != -1) {
                    val distance = event.y.toInt() - touchDownY
                    shouldParentIntercept =
                        distance > 0 && binding.scrollView.scrollY == 0
                    if (shouldParentIntercept) {
                        activity?.startHandlingTouches(touchDownY)
                        touchDownY = -1
                    }
                }
            }
        }
        lastTouchCoords = Pair(event.x, event.y)
        return shouldParentIntercept
    }

    fun setData() {
        binding.apply {
            flightNum.text = FullDetailsFlightData?.flightNo
            callSign.text = FullDetailsFlightData?.callSign
            depIataCode.text = FullDetailsFlightData?.depIataCode
            arrivalIataCode.text = FullDetailsFlightData?.arrIataCode
            depCityName.text = FullDetailsFlightData?.depCity
            arrCityName.text = FullDetailsFlightData?.arrCity
            airlineName.text = FullDetailsFlightData?.nameAirport
            tvAmericanAirlines.text = FullDetailsFlightData?.airlineName
            depTime.text =
                formatTo12HourTime(FullDetailsFlightData?.scheduledDepTime.toString())
            arriTime.text =
                formatTo12HourTime(FullDetailsFlightData?.scheduledArrTime.toString())
            terminalValue.text = FullDetailsFlightData?.terminal
            GateNo.text = FullDetailsFlightData?.gate
            delayValue.text = formatTo12HourTime(FullDetailsFlightData?.delay.toString())
            Scheduled.text = formatTo12HourTime(FullDetailsFlightData?.scheduled.toString())
            altitudeValue.text = FullDetailsFlightData?.altitude.toString()
            Direction.text = FullDetailsFlightData?.direction.toString()
            latitudeValue.text = FullDetailsFlightData?.latitude.toString()
            Longitude.text = FullDetailsFlightData?.longitude.toString()
            HSpeed.text = FullDetailsFlightData?.hSpeed.toString()
            Speed.text = FullDetailsFlightData?.vSpeed.toString()
            enRoute.text = FullDetailsFlightData?.status.toString()
            SquawkValue.text = FullDetailsFlightData?.squawk.toString()
            modelName.text = FullDetailsFlightData?.modelName.toString()
            modelCode.text = FullDetailsFlightData?.modelCode.toString()
            aircraftType.text = FullDetailsFlightData?.airCraftType.toString()
            regNo.text = FullDetailsFlightData?.regNo.toString()
            iataModel.text = FullDetailsFlightData?.iataModel.toString()
            ICAOHex.text = FullDetailsFlightData?.icaoHex.toString()
            FirstFlightDate.text = FullDetailsFlightData?.firstFlight.toString()
            DeliveryDate.text = FullDetailsFlightData?.deliveryDate.toString()
            RegisterationDate.text = FullDetailsFlightData?.regNo.toString()
            rolloutDate.text = FullDetailsFlightData?.rolloutDate.toString()
            EngineType.text = FullDetailsFlightData?.squawk.toString()
            RegisterationDates.text = FullDetailsFlightData?.regNo.toString()
            rolloutDates.text = FullDetailsFlightData?.rolloutDate.toString()
        }
    }
}