package com.example.liveflighttrackerapp.presentation.fragments

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import com.example.liveflighttrackerapp.data.model.airLine.StaticAirLineItems
import com.example.liveflighttrackerapp.databinding.FragmentSearchAirLinesBinding
import com.example.liveflighttrackerapp.presentation.activities.MainActivity
import com.example.liveflighttrackerapp.presentation.adapter.SearchAirLinesAdapter

class SearchAirLinesFragment(context: Context, attr: AttributeSet) :
    MyFragment<FragmentSearchAirLinesBinding>(context, attr) {

    var touchDownY = -1
    var ignoreTouches = false
    private var lastTouchCoords = Pair(0f, 0f)

    override fun setupFragment(activity: MainActivity) {
        this.activity = activity
        this.binding = FragmentSearchAirLinesBinding.bind(this)

        binding.btnBack.setOnClickListener {
            activity.onBackPress()
        }

        binding.tvAirLines.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                val adapter = binding.recyclerView.adapter as SearchAirLinesAdapter
                val filterList: List<StaticAirLineItems> = activity.airLines.filter {
                        it.nameAirline?.lowercase()?.startsWith(text.lowercase()) == true
                    }

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