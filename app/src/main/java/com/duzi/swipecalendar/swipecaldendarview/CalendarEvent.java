package com.duzi.swipecalendar.swipecaldendarview;


public abstract class CalendarEvent {

    private int indicatorColor;
    private long startTimeInMillis;

    public CalendarEvent(long startTimeInMillis, int indicatorColor) {
        this.startTimeInMillis = startTimeInMillis;
        this.indicatorColor = indicatorColor;
    }

    public long getTimeInMillis() {
        return startTimeInMillis;
    }

    public int getColor() {
        return indicatorColor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof CalendarEvent)) {
            return false;
        }
        CalendarEvent eventObj = (CalendarEvent) obj;
        return eventObj.getTimeInMillis() == startTimeInMillis && eventObj.getColor() == indicatorColor;
    }

    @Override
    public int hashCode() {
        return 37 * indicatorColor + (int) (startTimeInMillis ^ (startTimeInMillis >>> 32));
    }
}