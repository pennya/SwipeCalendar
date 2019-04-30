package com.duzi.swipecalendar.swipecaldendarview;

import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.List;

public interface OnDateSelectedListener {

    void onDateSelected(Calendar dayCalendar, @Nullable List<CalendarEvent> events);
}