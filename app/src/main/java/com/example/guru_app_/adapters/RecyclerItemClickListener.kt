package com.example.guru_app_.adapters

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerItemClickListener(//클릭 이벤트 처리
    context: Context,
    recyclerView: RecyclerView,
    private val listener: OnItemClickListener
) : RecyclerView.OnItemTouchListener {

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
        fun onLongItemClick(view: View, position: Int)
    }

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            val child = recyclerView.findChildViewUnder(e.x, e.y)
            if (child != null) {
                listener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child))
            }
        }
    })
    //터치 이벤트, RecyclerView의 아이템 상호작용 여부 결정
    override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
        val child = view.findChildViewUnder(e.x, e.y)
        if (child != null && gestureDetector.onTouchEvent(e)) {
            listener.onItemClick(child, view.getChildAdapterPosition(child))
        }
        return false
    }

    override fun onTouchEvent(view: RecyclerView, motionEvent: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
}
