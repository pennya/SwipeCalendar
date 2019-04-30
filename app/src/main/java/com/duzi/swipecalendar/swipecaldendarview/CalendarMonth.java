package com.duzi.swipecalendar.swipecaldendarview;

import android.util.SparseArray;

import java.util.Calendar;
import java.util.List;

public class CalendarMonth {
    private int year;
    private int month;
    private int amountOfDays;
    private int firstWeekDay;

    private static final int DEFAULT_DAYS_IN_WEEK = 7;

    private SparseArray<List<CalendarEvent>> events;

    private Calendar calendar;

    private CalendarMonth(Builder builder) {
        year = builder.year;
        month = builder.month;
        amountOfDays = builder.amountOfDays;
        firstWeekDay = builder.firstWeekDay;
        calendar = builder.calendar;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getAmountOfDays() {
        return amountOfDays;
    }

    public int getFirstWeekDay() {
        return firstWeekDay;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public List<CalendarEvent> getEventOfDay(int dayOfMonth) {
        if (events == null) {
            return null;
        }
        return events.get(dayOfMonth);
    }

    public void setEvents(SparseArray<List<CalendarEvent>> events) {
        this.events = events;
    }

    public int getDayIndex(int dayOfMonth) {
        return DEFAULT_DAYS_IN_WEEK + firstWeekDay + dayOfMonth;
    }

    boolean compareByDate(CalendarMonth calendarMonth) {
        return year == calendarMonth.getYear() && month == calendarMonth.getMonth();
    }

    static class Builder {
        private int year;
        private int month;
        private int amountOfDays;
        private int firstWeekDay;
        private Calendar calendar;

        public Builder setYear(int year) {
            this.year = year;
            return this;
        }

        public Builder setMonth(int month) {
            this.month = month;
            return this;
        }

        public Builder setAmountOfDays(int amountOfDays) {
            this.amountOfDays = amountOfDays;
            return this;
        }

        public Builder setFirstWeekDay(int firstWeekDay) {
            this.firstWeekDay = firstWeekDay;
            return this;
        }

        public Builder setCalendar(Calendar calendar) {
            this.calendar = calendar;
            return this;
        }

        public CalendarMonth build() {
            return new CalendarMonth(this);
        }
    }
}