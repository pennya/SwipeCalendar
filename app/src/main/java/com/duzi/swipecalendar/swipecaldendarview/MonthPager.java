package com.duzi.swipecalendar.swipecaldendarview;


import android.util.SparseArray;

import com.duzi.swipecalendar.swipecaldendarview.util.CalendarUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.duzi.swipecalendar.swipecaldendarview.MonthIndex.FOCUSED_MONTH;
import static com.duzi.swipecalendar.swipecaldendarview.MonthIndex.NEXT_MONTH;
import static com.duzi.swipecalendar.swipecaldendarview.MonthIndex.PREVIOUS_MONTH;

class MonthPager {

    private int firstDayOfWeek;
    private int selectedDay;
    private Calendar currentMonthCalendar;

    private CalendarMonth previousMonth;
    private CalendarMonth focusedMonth;
    private CalendarMonth nextMonth;

    private OnLoadEventsListener onLoadEventsListener;

    MonthPager(int firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;

        currentMonthCalendar = CalendarUtils.getTodayCalendar();
        currentMonthCalendar.setFirstDayOfWeek(firstDayOfWeek);

        Calendar focusedMonthCalendar = (Calendar) currentMonthCalendar.clone();
        selectedDay = CalendarUtils.getDayOfMonth(focusedMonthCalendar);

        setCalendarMonths(focusedMonthCalendar);
    }

    private void setCalendarMonths(Calendar focusedMonthCalendar) {
        Calendar nextMonthCalendar = (Calendar) focusedMonthCalendar.clone();
        CalendarUtils.setNextMonth(nextMonthCalendar);

        Calendar previousMonthCalendar = (Calendar) focusedMonthCalendar.clone();
        CalendarUtils.setPreviousMonth(previousMonthCalendar);

        previousMonth = buildCalendarMonth(previousMonthCalendar);
        focusedMonth = buildCalendarMonth(focusedMonthCalendar);
        nextMonth = buildCalendarMonth(nextMonthCalendar);
    }

    private CalendarMonth buildCalendarMonth(Calendar calendar) {
        CalendarMonth month = new CalendarMonth.Builder()
                .setYear(CalendarUtils.getYear(calendar))
                .setMonth(CalendarUtils.getMonth(calendar))
                .setFirstWeekDay(CalendarUtils.getFirstWeekDayOfMonth(calendar))
                .setAmountOfDays(CalendarUtils.getNumberOfDaysInMonth(calendar))
                .setCalendar(calendar)
                .build();

        loadEventsForMonth(month);
        return month;
    }

    private void loadEventsForMonth(CalendarMonth calendarMonth) {
        if (onLoadEventsListener == null) {
            return;
        }

        List<? extends CalendarEvent> monthEvents = onLoadEventsListener
                .onLoadEvents(calendarMonth.getYear(), calendarMonth.getMonth());

        if (monthEvents == null) {
            return;
        }

        // Events sorted by : (key) day of month - (value) events of day
        SparseArray<List<CalendarEvent>> eventsByDay = new SparseArray<>();

        Calendar eventCalendar = Calendar.getInstance();
        for (CalendarEvent calendarEvent : monthEvents) {
            eventCalendar.setTimeInMillis(calendarEvent.getTimeInMillis());

            if (CalendarUtils.isSameMonth(eventCalendar, calendarMonth.getCalendar())) {
                // Key
                int dayOfMonth = CalendarUtils.getDayOfMonth(eventCalendar);

                // Value
                List<CalendarEvent> eventsOfDay = eventsByDay.get(dayOfMonth);

                if (eventsOfDay == null) {
                    eventsOfDay = new ArrayList<>();
                    eventsOfDay.add(calendarEvent);
                    eventsByDay.put(dayOfMonth, eventsOfDay);
                } else {
                    eventsOfDay.add(calendarEvent);
                }
            }
        }

        calendarMonth.setEvents(eventsByDay);
    }


    void goForward(int day) {
        // Select first day of month after change of month
        selectDay(day);

        previousMonth = focusedMonth;
        focusedMonth = nextMonth;

        // Building next month from focused month calendar, after adding a month to clone
        Calendar calendar = (Calendar) focusedMonth.getCalendar().clone();
        CalendarUtils.setNextMonth(calendar);
        nextMonth = buildCalendarMonth(calendar);
    }

    void goBack(int day) {
        // Select first day of month after change of month
        selectDay(day);

        nextMonth = focusedMonth;
        focusedMonth = previousMonth;

        // Building next month from focused month calendar, after subtracting a month from clone
        Calendar calendar = (Calendar) focusedMonth.getCalendar().clone();
        CalendarUtils.setPreviousMonth(calendar);
        previousMonth = buildCalendarMonth(calendar);
    }

    void selectDay(int day) {
        selectedDay = day;
    }

    CalendarMonth getCalendarMonth(MonthIndex monthIndex) {
        switch (monthIndex) {
            case PREVIOUS_MONTH:
                return previousMonth;
            case FOCUSED_MONTH:
                return focusedMonth;
            case NEXT_MONTH:
                return nextMonth;
        }

        return null;
    }

    void setOnLoadEventsListener(OnLoadEventsListener listener) {
        onLoadEventsListener = listener;

        // Invalidate
        loadEventsForMonth(getCalendarMonth(PREVIOUS_MONTH));
        loadEventsForMonth(getCalendarMonth(FOCUSED_MONTH));
        loadEventsForMonth(getCalendarMonth(NEXT_MONTH));
    }

    boolean isOnCurrentMonth(MonthIndex monthIndex) {
        return getCalendarMonth(monthIndex).getYear() == CalendarUtils.getYear(currentMonthCalendar) &&
                getCalendarMonth(monthIndex).getMonth() == CalendarUtils.getMonth(currentMonthCalendar);
    }

    int getCurrentDay() {
        return CalendarUtils.getDayOfMonth(currentMonthCalendar);
    }

    int getDayOfWeek(int day) {
        Calendar temp = (Calendar) focusedMonth.getCalendar().clone();
        temp.set(Calendar.DATE, day);
        return CalendarUtils.getDayOfWeek(temp);
    }

    int getSelectedDay() {
        return selectedDay;
    }
}
