package com.duzi.swipecalendar.swipecaldendarview;

public enum MonthIndex {
    PREVIOUS_MONTH(-1), FOCUSED_MONTH(0), NEXT_MONTH(1);

    private int value;
    MonthIndex(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
