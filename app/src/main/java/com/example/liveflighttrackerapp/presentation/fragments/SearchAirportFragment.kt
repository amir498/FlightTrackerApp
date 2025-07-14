package com.example.liveflighttrackerapp.presentation.fragments

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import com.example.liveflighttrackerapp.data.model.airport.AirportsDataItems
import com.example.liveflighttrackerapp.databinding.FragmentSearchAirportsBinding
import com.example.liveflighttrackerapp.presentation.activities.MainActivity
import com.example.liveflighttrackerapp.presentation.adapter.SearchAirportAdapter

class SearchAirportFragment(context: Context, attr: AttributeSet) :
    MyFragment<FragmentSearchAirportsBinding>(context, attr) {
    var touchDownY = -1
    var ignoreTouches = false
    private var lastTouchCoords = Pair(0f, 0f)

    override fun setupFragment(activity: MainActivity) {
        this.activity = activity
        this.binding = FragmentSearchAirportsBinding.bind(this)
        binding.btnBack.setOnClickListener {
            activity.onBackPress()
        }

        binding.tvAirports.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {


            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                val adapter = binding.recyclerView.adapter as SearchAirportAdapter
                val filterList: List<AirportsDataItems> = activity.airportsDataList
                    .filter { it.nameAirport?.lowercase()?.contains(text.lowercase()) == true }

                adapter.setList(filterList)
            }
        })
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
                        distance > 0 && binding.recyclerView.computeVerticalScrollOffset() == 0
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
}