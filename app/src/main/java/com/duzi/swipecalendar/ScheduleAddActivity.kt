package com.duzi.swipecalendar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.duzi.swipecalendar.MainActivity.Companion.TIMESTAMP
import kotlinx.android.synthetic.main.activity_schedule_add.*
import java.util.*

class ScheduleAddActivity : AppCompatActivity() {

    var selectedDay = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_add)

        val bundle = intent.extras
        bundle?.let {
            val timestamp = bundle.getLong(TIMESTAMP)
            selectedDay.timeInMillis = timestamp
            println("selectedDay : ${selectedDay.timeInMillis} ${selectedDay.get(Calendar.YEAR)} ${selectedDay.get(Calendar.MONTH)} ${selectedDay.get(Calendar.DATE)}")
        }

        initLayout()
    }

    private fun initLayout() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.title = ""
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }

}
