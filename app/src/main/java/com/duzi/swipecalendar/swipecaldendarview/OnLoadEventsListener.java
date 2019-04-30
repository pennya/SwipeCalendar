package com.duzi.swipecalendar.swipecaldendarview;

import java.util.List;

public interface OnLoadEventsListener {

    List<? extends CalendarEvent> onLoadEvents(int year, int month);
}
