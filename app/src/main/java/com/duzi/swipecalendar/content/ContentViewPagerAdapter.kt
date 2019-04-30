package com.duzi.swipecalendar.content

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.duzi.swipecalendar.MyEvent
import com.duzi.swipecalendar.swipecaldendarview.CalendarEvent

class ContentViewPagerAdapter(fm: FragmentManager,
                              private val list: ArrayList<MyEvent>): FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        val title = list[position].getTitle()
        val content = list[position].getContent()

        return ContentFragment.newInstance(title, content)
    }

    override fun getCount(): Int = list.size

    fun addAll(list: List<MyEvent>) {
        this.list.addAll(list)
    }

    fun findItem(timeInMillis: Long): Int {
        for(i in 0 until list.size) {
            if((list[i] as CalendarEvent).timeInMillis ==  timeInMillis) return i
        }

        return -1
    }

    fun findItem(event: MyEvent, timeInMillis: Long): Int {
        for(i in 0 until list.size) {
            if(list[i] == event) {
                println("${timeInMillis} ${(event as CalendarEvent).timeInMillis}")
                return i
            }
        }

        return -1
    }

}