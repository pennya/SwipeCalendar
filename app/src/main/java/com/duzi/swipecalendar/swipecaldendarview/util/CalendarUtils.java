package com.duzi.swipecalendar.swipecaldendarview.util;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

public class CalendarUtils {

    public static Calendar getTodayCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar;
    }

    public static int getDayOfMonth(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getDayOfWeek(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static void setNextMonth(Calendar calendar) {
        calendar.add(Calendar.MONTH, 1);
    }

    public static void setPreviousMonth(Calendar calendar) {
        calendar.add(Calendar.MONTH, -1);
    }

    public static int getYear(Calendar calendar) {
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonth(Calendar calendar) {
        return calendar.get(Calendar.MONTH);
    }

    public static int getFirstWeekDayOfMonth(Calendar calendar) {
        Calendar utilCalendar = (Calendar) calendar.clone();
        utilCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int dayOfWeek = utilCalendar.get(Calendar.DAY_OF_WEEK) - utilCalendar.getFirstDayOfWeek();
        return dayOfWeek < 0 ? 7 + dayOfWeek : dayOfWeek;
    }

    public static int getNumberOfDaysInMonth(Calendar calendar) {
        // 해당 월 마지막 날짜
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static String[] getWeekDaysAbbreviation(int firstDayOfWeek) {
        if (firstDayOfWeek < 1 || firstDayOfWeek > 7) {
            throw new IllegalArgumentException("Day must be from Java Calendar class");
        }

        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(Locale.getDefault());
        String[] shortWeekdays = dateFormatSymbols.getShortWeekdays();

        String[] weekDaysFromSunday = new String[]{shortWeekdays[1], shortWeekdays[2],
                shortWeekdays[3], shortWeekdays[4], shortWeekdays[5], shortWeekdays[6],
                shortWeekdays[7]};

        String[] weekDaysNames = new String[7];

        for (int day = firstDayOfWeek - 1, i = 0; i < 7; i++, day++) {
            day = day >= 7 ? 0 : day;
            weekDaysNames[i] = weekDaysFromSunday[day].toUpperCase();
        }

        return weekDaysNames;
    }
}
