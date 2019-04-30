package com.duzi.swipecalendar

import com.duzi.swipecalendar.swipecaldendarview.CalendarEvent

class MyEvent(startTimeInMillis: Long, indicatorColor: Int, title: String?, content: String?)
    : CalendarEvent(startTimeInMillis, indicatorColor) {

    private var title: String? = null
    private var content: String? = null

    init {
        title?.let { this.title = it }
        content?.let { this.content = it }
    }

    fun getTitle(): String? = title
    fun getContent(): String? = content
}