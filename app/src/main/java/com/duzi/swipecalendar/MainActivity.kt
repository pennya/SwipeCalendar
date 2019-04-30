package com.duzi.swipecalendar

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.duzi.swipecalendar.content.ContentViewPagerAdapter
import com.duzi.swipecalendar.swipecaldendarview.CalendarEvent
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var selectedDay: Calendar
    private lateinit var adapter: ContentViewPagerAdapter
    private val cachedEvents = arrayListOf<MyEvent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initLayout()
    }

    @SuppressLint("SetTextI18n")
    fun initLayout() {

        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
                // nothing
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                // nothing
            }

            override fun onPageSelected(position: Int) {
                // move calendar
                val temp = Calendar.getInstance()
                temp.timeInMillis = (cachedEvents[position] as CalendarEvent).timeInMillis
                calendarView.moveSelectedDay(temp)
            }

        })

        adapter = ContentViewPagerAdapter(this, supportFragmentManager, arrayListOf())
        viewPager.adapter = adapter


        calendarView.setOnDateSelectedListener{ dayCalendar, events ->
            selectedDay = dayCalendar
            println("dayCalendar : ${dayCalendar.timeInMillis} ${dayCalendar.get(Calendar.YEAR)} ${dayCalendar.get(Calendar.MONTH)} ${dayCalendar.get(Calendar.DATE)}")

            //val index = adapter.findItem(dayCalendar.timeInMillis)
            //viewPager.currentItem = index
            events?.run {
                for(event in events.iterator()) {
                    val myEvent = event as MyEvent
                    println("events : ${myEvent.getTitle()} ${myEvent.getContent()}")

                    val index = adapter.findItem(myEvent)
                    viewPager.currentItem = index
                }
            }
        }

        calendarView.setOnMonthChangedListener { monthCalendar: Calendar? ->
            monthCalendar?.let {
                tv_main_date.text = "${monthCalendar.get(Calendar.YEAR)}.${monthCalendar.get(Calendar.MONTH) + 1}"
            }
        }

        calendarView.setOnLoadEventsListener { year, month ->
            val events = arrayListOf<MyEvent>()

            // get data from database

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)

            // test sample
            for(i in 1..30) {
                if(i % 2 == 0) continue
                calendar.set(Calendar.DAY_OF_MONTH, i)
                events.add(MyEvent(calendar.timeInMillis, R.color.red, "title $month $i", "content $month $i"))
                //events.add(MyEvent(calendar.timeInMillis, R.color.red, "title$i$i", "content$i$i"))
            }

            cachedEvents.addAll(events)
            adapter.addAll(events)
            adapter.notifyDataSetChanged()

            events
        }

        btnAddSchedule.setOnClickListener {
            if(::selectedDay.isInitialized) {
                val intent = Intent(this@MainActivity, ScheduleAddActivity::class.java)
                val bundle = Bundle()
                bundle.putLong(TIMESTAMP, selectedDay.timeInMillis)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }

    companion object {
        const val TIMESTAMP = "TIMESTAMP"
    }
}
