package com.duzi.swipecalendar.swipecaldendarview;


import com.duzi.swipecalendar.swipecaldendarview.util.CalendarUtils;
import java.util.Calendar;

class MonthPager {

    private int firstDayOfWeek;
    private int selectedDay;
    private Calendar currentMonthCalendar;

    private CalendarMonth previousMonth;
    private CalendarMonth focusedMonth;
    private CalendarMonth nextMonth;

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

        return month;
    }

    void goForward() {
        // Select first day of month after change of month
        selectDay(1);

        previousMonth = focusedMonth;
        focusedMonth = nextMonth;

        // Building next month from focused month calendar, after adding a month to clone
        Calendar calendar = (Calendar) focusedMonth.getCalendar().clone();
        CalendarUtils.setNextMonth(calendar);
        nextMonth = buildCalendarMonth(calendar);
    }

    void goBack() {
        // Select first day of month after change of month
        selectDay(1);

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
