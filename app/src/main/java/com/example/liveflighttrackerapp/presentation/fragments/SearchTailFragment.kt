package com.example.liveflighttrackerapp.presentation.fragments

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.example.liveflighttrackerapp.databinding.FragmentSearchTailBinding
import com.example.liveflighttrackerapp.presentation.activities.MainActivity

class SearchTailFragment(context: Context, attr: AttributeSet) :
    MyFragment<FragmentSearchTailBinding>(context, attr) {
    var touchDownY = -1
    var ignoreTouches = false
    private var lastTouchCoords = Pair(0f, 0f)

    override fun setupFragment(activity: MainActivity) {
        this.activity = activity
        this.binding = FragmentSearchTailBinding.bind(this)

        binding.btnBack.setOnClickListener {
            activity.onBackPress()
        }
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