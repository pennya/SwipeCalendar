package com.duzi.swipecalendar.swipecaldendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.OverScroller;

import com.duzi.swipecalendar.R;
import com.duzi.swipecalendar.swipecaldendarview.util.CalendarUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.duzi.swipecalendar.swipecaldendarview.MonthIndex.FOCUSED_MONTH;
import static com.duzi.swipecalendar.swipecaldendarview.MonthIndex.NEXT_MONTH;
import static com.duzi.swipecalendar.swipecaldendarview.MonthIndex.PREVIOUS_MONTH;

public class SwipeCalendarView extends View {

    private static final int VELOCITY_THRESHOLD = 2000;

    private static final float RATIO_ROW_HEIGHT_WIDTH = 0.098f;
    private static final float RATIO_WIDTH_PADDING_X = 12.0f;
    private static final float RATIO_WIDTH_PADDING_Y = 15.0f;
    private static final float RATIO_WIDTH_TEXT_HEIGHT = 36.0f;
    private static final float RATIO_WIDTH_CIRCLE_RADIUS = 27.0f;
    private static final float RATIO_DURATION_DISTANCE = 0.75f;
    private static final int DEFAULT_DAYS_IN_WEEK = 7;
    private static final int RESIZE_ANIMATION_DURATION = 200;

    // 한 주의 첫번째 날 ( 일요일 )
    private int firstDayOfWeek;
    private String[] weekDayNames;
    private boolean isResize;

    private float paddingX;
    private float paddingY;
    private float betweenX;
    private float betweenY;
    private int viewHeight;
    private int offset;
    private int rowsCount;
    private Rect textRect = new Rect();

    private MonthPager monthPager;

    private Paint textInsideCirclePaint;
    private Paint textPaint;
    private float textHeight;
    private Paint selectedDayCirclePaint;
    private Paint currentDayCirclePaint;
    private float circleRadius;
    private Paint eventCirclePaint;
    private Paint backgroundPaint;
    private Paint weekDaysNamesTextPaint;
    private Paint sundayTextPaint;
    private float eventCircleRadius;
    private float placeForPointsWidth;

    private OnDateSelectedListener onDateSelectedListener;
    private OnMonthChangedListener onMonthChangedListener;

    private GestureDetectorCompat detector;
    private VelocityTracker velocityTracker;
    private OverScroller scroller;

    public SwipeCalendarView(Context context) {
        this(context, null);
    }

    public SwipeCalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

        drawMonth(canvas, FOCUSED_MONTH);

        if (offset > 0) {
            System.out.println("draw PREVIOUS_MONTH ");
            drawMonth(canvas, PREVIOUS_MONTH);
        }

        if (offset < 0) {
            System.out.println("draw NEXT_MONTH ");
            drawMonth(canvas, NEXT_MONTH);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minWidth = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int width = resolveSizeAndState(minWidth, widthMeasureSpec, 1);
        int height;

        if(isResize) {
            height = viewHeight;
        } else {
            height = (int) (width * RATIO_ROW_HEIGHT_WIDTH *
                    getMonthRowsCount(monthPager.getCalendarMonth(FOCUSED_MONTH)));

            paddingX = width / RATIO_WIDTH_PADDING_X;
            paddingY = width / RATIO_WIDTH_PADDING_Y;
            betweenX = (width - paddingX * 2) / (DEFAULT_DAYS_IN_WEEK - 1);
            betweenY = (height / rowsCount * 6 - paddingY * 2) / 5;

            textHeight = width / RATIO_WIDTH_TEXT_HEIGHT;
            circleRadius = width / RATIO_WIDTH_CIRCLE_RADIUS;
            eventCircleRadius = circleRadius / 7;
            placeForPointsWidth = betweenX / 2;

            textPaint.setTextSize(textHeight);
            textInsideCirclePaint.setTextSize(textHeight);
            weekDaysNamesTextPaint.setTextSize(textHeight);
            sundayTextPaint.setTextSize(textHeight);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(!scroller.isFinished()) {
                    scroller.abortAnimation();
                }

                if(velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                } else {
                    velocityTracker.clear();
                }
                velocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                velocityTracker.addMovement(event);
                velocityTracker.computeCurrentVelocity(1000);
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);

                velocityTracker.computeCurrentVelocity(1000);
                handleGesture(velocityTracker.getXVelocity());
                velocityTracker.recycle();
                velocityTracker.clear();
                velocityTracker = null;
                break;
        }
        return this.detector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            offset = scroller.getCurrX();
            invalidate();
            if (offset == scroller.getFinalX()) {
                scroller.forceFinished(true);
            }
        }
    }

    public void moveToday() {
        //TODO 오늘 날짜로 이동
    }

    private int currentMonth = -1;

    public void moveSelectedDay(Calendar calendar) {

        int day = calendar.get(Calendar.DATE);
        int tempMonth = calendar.get(Calendar.MONTH);

        System.out.println(String.format("month %d  day %d   current month %d", tempMonth, day, currentMonth));
        if(currentMonth == -1) {
            currentMonth = tempMonth;
        }

        if(tempMonth > currentMonth) {
            currentMonth= tempMonth;
            monthPager.goForward(day);
            dispatchOnMonthChanged(monthPager.getCalendarMonth(FOCUSED_MONTH).getCalendar());
            resizeView(getMonthRowsCount(monthPager.getCalendarMonth(FOCUSED_MONTH)));
            ViewCompat.postInvalidateOnAnimation(this);
        } else if(tempMonth < currentMonth) {
            currentMonth= tempMonth;
            monthPager.goBack(day);
            dispatchOnMonthChanged(monthPager.getCalendarMonth(FOCUSED_MONTH).getCalendar());
            resizeView(getMonthRowsCount(monthPager.getCalendarMonth(FOCUSED_MONTH)));
            ViewCompat.postInvalidateOnAnimation(this);
        }


        monthPager.selectDay(day);
        invalidate();
    }

    private void init() {
        firstDayOfWeek = 1;

        monthPager = new MonthPager(firstDayOfWeek);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.black));

        textInsideCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textInsideCirclePaint.setColor(ContextCompat.getColor(getContext(), R.color.white));

        selectedDayCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectedDayCirclePaint.setStyle(Paint.Style.FILL);
        selectedDayCirclePaint.setColor(ContextCompat.getColor(getContext(), R.color.grey_500));

        currentDayCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        currentDayCirclePaint.setStyle(Paint.Style.FILL);
        currentDayCirclePaint.setColor(ContextCompat.getColor(getContext(), R.color.green_300));

        weekDaysNamesTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        weekDaysNamesTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.black));
        weekDaysNamesTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

        sundayTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sundayTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.red));
        sundayTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

        eventCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        eventCirclePaint.setStyle(Paint.Style.FILL);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.white_ae));

        rowsCount = getMonthRowsCount(monthPager.getCalendarMonth(FOCUSED_MONTH));
        weekDayNames = CalendarUtils.getWeekDaysAbbreviation(firstDayOfWeek);

        scroller = new OverScroller(getContext());
        detector = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                CalendarMonth calendarMonth = monthPager.getCalendarMonth(FOCUSED_MONTH);

                float x = motionEvent.getX(), y = motionEvent.getY();
                int day = getDayNumberOfCrd(x, y, calendarMonth.getFirstWeekDay());

                if (day < 1 || day > calendarMonth.getAmountOfDays()) {
                    return true;
                }

                monthPager.selectDay(day);
                invalidate();

                Calendar calendar = calendarMonth.getCalendar();
                calendar.set(Calendar.DATE, day);
                dispatchOnDateSelected(calendar, calendarMonth.getEventOfDay(day));
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float dx, float dy) {
                getParent().requestDisallowInterceptTouchEvent(true);

                int width = getWidth();
                offset -= dx;

                if (offset > width) {
                    offset = width;
                } else if (offset < -width) {
                    offset = -width;
                }

                System.out.println("offset " + offset);

                invalidate();
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
    }

    private void drawMonth(Canvas canvas, MonthIndex monthIndex) {
        CalendarMonth calendarMonth = monthPager.getCalendarMonth(monthIndex);

        // 선택 날짜 원 그리기
        if (monthIndex == FOCUSED_MONTH) {
            float[] crdCircle = calculateCrdForIndex(calendarMonth.getDayIndex(monthPager.getSelectedDay()),
                    null, monthIndex);
            canvas.drawCircle(crdCircle[0], crdCircle[1], circleRadius, selectedDayCirclePaint);
        }

        // 오늘 날짜 원 그리기
        if (monthPager.isOnCurrentMonth(monthIndex)) {
            float[] crdCircle = calculateCrdForIndex(calendarMonth.getDayIndex(monthPager.getCurrentDay()),
                    null, monthIndex);
            canvas.drawCircle(crdCircle[0], crdCircle[1], circleRadius, currentDayCirclePaint);
        }

        // 일~월 글자 표시
        for (int i = 1; i <= DEFAULT_DAYS_IN_WEEK; i++) {
            float[] crd = calculateCrdForIndex(i, weekDayNames[i - 1], monthIndex);

            boolean isSunday = weekDayNames[i - 1].equals("일");
            canvas.drawText(weekDayNames[i - 1], crd[0], crd[1],
                    isSunday ? sundayTextPaint : weekDaysNamesTextPaint);

        }

        // 1~31 날짜 표시
        for (int day = 1; day <= calendarMonth.getAmountOfDays(); day++) {
            int index = calendarMonth.getDayIndex(day);
            float[] crd = calculateCrdForIndex(index, Integer.toString(day), monthIndex);

            boolean isCurrentDay = monthPager.isOnCurrentMonth(monthIndex) && day == monthPager.getCurrentDay();
            boolean isSelectedDay = monthIndex == FOCUSED_MONTH && day == monthPager.getSelectedDay();
            boolean isSunday = monthPager.getDayOfWeek(day) == Calendar.SUNDAY;

            canvas.drawText(Integer.toString(day), crd[0], crd[1],
                    isCurrentDay || isSelectedDay ? textInsideCirclePaint : isSunday ? sundayTextPaint : textPaint);

            List<CalendarEvent> events = calendarMonth.getEventOfDay(day);
            if (events != null && !isCurrentDay && !isSelectedDay) {
                drawEventsOfDay(canvas, events, crd, day);
            }
        }
    }

    private void drawEventsOfDay(Canvas canvas, List<CalendarEvent> events, float[] crd, int day) {
        // Measure text of events day number
        String dayText = Integer.toString(day);
        textPaint.getTextBounds(dayText, 0, dayText.length(), textRect);

        float offsetForCenter = placeForPointsWidth / 2 - textRect.centerX();

        // Space between events points for X axis
        float betweenPoints = placeForPointsWidth / (events.size() + 1);

        for (int i = 0; i < events.size(); i++) {
            eventCirclePaint.setColor(events.get(i).getColor());
            canvas.drawCircle(crd[0] - offsetForCenter + betweenPoints * (i + 1),
                    crd[1] + circleRadius / 2, eventCircleRadius, eventCirclePaint);
        }
    }

    private int getDayNumberOfCrd(float x, float y, int firstDayOfWeek) {
        weekDaysNamesTextPaint.getTextBounds(weekDayNames[0], 0, weekDayNames[0].length(), textRect);
        float weekDaysNamesHeight = textRect.top + betweenY;

        float widthPerDay = (getWidth() - paddingX * 2 + betweenX) / DEFAULT_DAYS_IN_WEEK;
        float heightPerDay = (getHeight() - paddingY * 2 - weekDaysNamesHeight + betweenY) / (rowsCount - 1);

        x = x - paddingX + betweenX;
        y = y - paddingY + betweenY - weekDaysNamesHeight;

        int row = Math.round(x / widthPerDay);
        int column = Math.round(y / heightPerDay);

        return (column - 1) * DEFAULT_DAYS_IN_WEEK + row - firstDayOfWeek;
    }

    private float[] calculateCrdForIndex(int index, @Nullable String text, MonthIndex monthIndex) {
        int rowIndex = (index - 1) % DEFAULT_DAYS_IN_WEEK;
        int column = (index - 1) / DEFAULT_DAYS_IN_WEEK;

        float x = paddingX + (betweenX * rowIndex) + (getWidth() * monthIndex.getValue());
        float y = paddingY + (betweenY * column);

        x += offset;

        // Calculation of the text center
        if (text != null) {
            // Measure text size
            textPaint.getTextBounds(text, 0, text.length(), textRect);

            x -= textRect.centerX();
            y -= textRect.centerY();
        }

        return new float[]{x, y};
    }

    private int getMonthRowsCount(CalendarMonth calendarMonth) {
        float rowsCount = (float) (calendarMonth.getAmountOfDays() + calendarMonth.getFirstWeekDay()) / DEFAULT_DAYS_IN_WEEK;
        return (int) Math.ceil(rowsCount) + 1;
    }

    private void resizeView(int targetRowsCount) {
        // If current rows count are equals to target rows count resize not required
        if (rowsCount == targetRowsCount) {
            return;
        }

        class ResizeAnimation extends Animation {
            private int targetHeight;
            private View view;
            private int startHeight;

            private ResizeAnimation(View view, int targetHeight, int startHeight) {
                this.view = view;
                this.targetHeight = targetHeight;
                this.startHeight = startHeight;
            }

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                int newHeight = (int) (startHeight + (targetHeight - startHeight) * interpolatedTime);
                viewHeight = newHeight;
                view.getLayoutParams().height = newHeight;
                view.requestLayout();

                if (interpolatedTime == 1.0f) {
                    // Animation is over
                    isResize = false;
                }
            }

            @Override
            public void initialize(int width, int height, int parentWidth, int parentHeight) {
                super.initialize(width, height, parentWidth, parentHeight);
            }
        }
        ResizeAnimation resizeAnimation = new ResizeAnimation(this,
                getHeight() * targetRowsCount / rowsCount, getHeight());
        resizeAnimation.setDuration(RESIZE_ANIMATION_DURATION);
        startAnimation(resizeAnimation);

        isResize = true;
        rowsCount = targetRowsCount;
    }

    private void handleGesture(float velocity) {
        if (velocity == 0 && offset == 0) {
            return;
        }

        if ((velocity > VELOCITY_THRESHOLD || offset > getWidth() / 2)) {
            if (!canGoBack()) {
                handleGesture(0);
                return;
            }

            monthPager.goBack(1);
            currentMonth -= 1;

            int distance = getWidth() - offset;
            offset = offset - getWidth();

            scroller.startScroll(offset, 0, distance, 0,
                    (int) (Math.abs(distance) * RATIO_DURATION_DISTANCE));

            dispatchOnMonthChanged(monthPager.getCalendarMonth(FOCUSED_MONTH).getCalendar());
            resizeView(getMonthRowsCount(monthPager.getCalendarMonth(FOCUSED_MONTH)));

            ViewCompat.postInvalidateOnAnimation(this);

        } else if ((velocity < -VELOCITY_THRESHOLD || offset < -getWidth() / 2)) {
            if (!canGoForward()) {
                handleGesture(0);
                return;
            }

            monthPager.goForward(1);
            currentMonth += 1;

            int distance = -getWidth() - offset;
            offset = offset + getWidth();

            scroller.startScroll(offset, 0, distance, 0,
                    (int) (Math.abs(distance) * RATIO_DURATION_DISTANCE));
            dispatchOnMonthChanged(monthPager.getCalendarMonth(FOCUSED_MONTH).getCalendar());
            resizeView(getMonthRowsCount(monthPager.getCalendarMonth(FOCUSED_MONTH)));

            ViewCompat.postInvalidateOnAnimation(this);

        } else {
            int distance = -offset;
            scroller.startScroll(offset, 0, distance, 0,
                    (int) (Math.abs(distance) * RATIO_DURATION_DISTANCE * 2));
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        this.onDateSelectedListener = onDateSelectedListener;
    }

    public void setOnMonthChangedListener(OnMonthChangedListener onMonthChangedListener) {
        this.onMonthChangedListener = onMonthChangedListener;
        dispatchOnMonthChanged(monthPager.getCalendarMonth(FOCUSED_MONTH).getCalendar());
    }

    public void setOnLoadEventsListener(OnLoadEventsListener onLoadEventsListener) {
        monthPager.setOnLoadEventsListener(onLoadEventsListener);
    }

    private void dispatchOnDateSelected(Calendar calendar, List<CalendarEvent> eventsOfDay) {
        if (onDateSelectedListener != null) {
            onDateSelectedListener.onDateSelected(calendar, eventsOfDay);
        }
    }

    private void dispatchOnMonthChanged(Calendar calendar) {
        if (onMonthChangedListener != null) {
            onMonthChangedListener.onMonthChanged(calendar);
        }
    }

    private boolean canGoForward() {
        return offset <= 0;
    }

    private boolean canGoBack() {
        return offset >= 0;
    }
}
