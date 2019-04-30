package com.duzi.swipecalendar

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initLayout()
    }

    @SuppressLint("SetTextI18n")
    fun initLayout() {

        calendarView.setOnDateSelectedListener{ dayCalendar, events ->
            println("dayCalendar : ${dayCalendar.timeInMillis} ${dayCalendar.get(Calendar.YEAR)} ${dayCalendar.get(Calendar.MONTH)} ${dayCalendar.get(Calendar.DATE)}")
        }

        calendarView.setOnMonthChangedListener { monthCalendar: Calendar? ->
            monthCalendar?.let {
                tv_main_date.text = "${monthCalendar.get(Calendar.YEAR)}.${monthCalendar.get(Calendar.MONTH) + 1}"
            }
        }

        btnAddSchedule.setOnClickListener {
            startActivity(Intent(this@MainActivity, ScheduleAddActivity::class.java))
        }
    }
}
