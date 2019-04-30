package com.duzi.swipecalendar

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var selectedDay: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initLayout()
    }

    @SuppressLint("SetTextI18n")
    fun initLayout() {

        calendarView.setOnDateSelectedListener{ dayCalendar, events ->
            selectedDay = dayCalendar
            println("dayCalendar : ${dayCalendar.timeInMillis} ${dayCalendar.get(Calendar.YEAR)} ${dayCalendar.get(Calendar.MONTH)} ${dayCalendar.get(Calendar.DATE)}")
        }

        calendarView.setOnMonthChangedListener { monthCalendar: Calendar? ->
            monthCalendar?.let {
                tv_main_date.text = "${monthCalendar.get(Calendar.YEAR)}.${monthCalendar.get(Calendar.MONTH) + 1}"
            }
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
