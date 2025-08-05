package fr.jadeveloppement.agenda.components;

import static java.lang.Integer.parseInt;
import static java.util.Objects.isNull;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import fr.jadeveloppement.agenda.R;

public class CustomCalendar extends LinearLayout {
    private int CALENDAR_ACTIVE_COLOR = Color.parseColor("#FFA500");

    public void setSelectionDayColor(int color){
        this.CALENDAR_ACTIVE_COLOR = color;
    }

    public int getSelectionDayColor(){
        return this.CALENDAR_ACTIVE_COLOR;
    }

    private final String TAG = "Agenda";

    private final String[] monthesLong = {
            "",
            "Janvier",
            "Février",
            "Mars",
            "Avril",
            "Mai",
            "Juin",
            "Juillet",
            "Aout",
            "Septembre",
            "Octobre",
            "Novembre",
            "Décembre"
    };
    private final String[] daysLong = {
            "Lundi",
            "Mardi",
            "Mercredi",
            "Jeudi",
            "Vendredi",
            "Samedi",
            "Dimanche"
    };

    public static String[] daysShort = {
            "L", "M", "M", "J", "V", "S", "D"
    };

    private final Context context;
    private final LinearLayout monthLayout, calendarFirstLine, calendarDaysLayout;


    private String selectedDay;
    private String calendarMonth, calendarDay, calendarYear;
    private int calendarWeekNumber;

    private Runnable dayTvClicked;


    public CustomCalendar(Context c){
        super(c);
        this.context = c;
        this.calendarFirstLine = new LinearLayout(context);
        this.calendarDaysLayout = new LinearLayout(context);
        this.monthLayout = new LinearLayout(context);

        initVariables();
    }

    public CustomCalendar(Context c, Runnable r){
        super(c);
        this.context = c;
        this.calendarFirstLine = new LinearLayout(context);
        this.calendarDaysLayout = new LinearLayout(context);
        this.monthLayout = new LinearLayout(context);
        this.dayTvClicked = r;

        initVariables();
    }

    private void initVariables(){
        this.selectedDay = getTodayDate();
        this.calendarDay = selectedDay.split("-")[2];
        this.calendarMonth = selectedDay.split("-")[1];
        this.calendarYear = selectedDay.split("-")[0];
        this.calendarWeekNumber = getWeekNumberFromDate(selectedDay);
    }

    public int getWeekNumber(){
        return calendarWeekNumber;
    }

    public int getDayOfWeekIndex(){
        return getDayOfWeekIndex(selectedDay);
    }

    public String getDay(){
        return calendarDay;
    }

    public String getMonth(){
        return calendarMonth;
    }

    public String getYear(){
        return calendarYear;
    }

    public LinearLayout getMonthLayout(){
        monthLayout.removeAllViews();
        ViewGroup.LayoutParams monthLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        monthLayout.setLayoutParams(monthLayoutParams);
        monthLayout.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources().getDisplayMetrics()));
        monthLayout.setOrientation(LinearLayout.HORIZONTAL);
        monthLayout.setGravity(Gravity.CENTER);
        monthLayout.setWeightSum(10f);

        TextView prevMonth = new TextView(context);
        TextView tvMonth = new TextView(context);
        TextView nextMonth = new TextView(context);

        LinearLayout.LayoutParams tvParams1 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        );

        LinearLayout.LayoutParams tvParams8 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                8f
        );

        prevMonth.setText("<");
        tvMonth.setText(String.valueOf(monthesLong[parseInt(getMonth())] + " " + getYear()));
        nextMonth.setText(">");

        prevMonth.setLayoutParams(tvParams1);
        tvMonth.setLayoutParams(tvParams8);
        nextMonth.setLayoutParams(tvParams1);

        prevMonth.setTextAppearance(androidx.appcompat.R.style.TextAppearance_AppCompat_Medium);
        tvMonth.setTextAppearance(androidx.appcompat.R.style.TextAppearance_AppCompat_Small);
        nextMonth.setTextAppearance(androidx.appcompat.R.style.TextAppearance_AppCompat_Medium);

        prevMonth.setTypeface(Typeface.DEFAULT_BOLD);
        tvMonth.setTypeface(Typeface.DEFAULT_BOLD);
        nextMonth.setTypeface(Typeface.DEFAULT_BOLD);
        tvMonth.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);

        prevMonth.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics()));
        tvMonth.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics()));
        nextMonth.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics()));

        monthLayout.addView(prevMonth);
        monthLayout.addView(tvMonth);
        monthLayout.addView(nextMonth);

        prevMonth.setOnClickListener(v -> {
            addInterval(-1, "month");
            if (!isNull(dayTvClicked)) dayTvClicked.run();
        });

        nextMonth.setOnClickListener(v -> {
            addInterval(1, "month");
            if (!isNull(dayTvClicked)) dayTvClicked.run();
        });

        return monthLayout;
    }

    public LinearLayout getFirstLine(){
        LinearLayout.LayoutParams calendarFirstLineParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        calendarFirstLine.setLayoutParams(calendarFirstLineParams);
        calendarFirstLine.setOrientation(LinearLayout.HORIZONTAL);
        calendarFirstLine.setWeightSum(7);
        calendarFirstLine.setPadding(12, 12, 12, 12);

        for (String d : daysShort){
            TextView tvDay = new TextView(getContext());
            LinearLayout.LayoutParams tvDayParams = new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
            );
            tvDay.setLayoutParams(tvDayParams);
            tvDay.setText(d);
            tvDay.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
            tvDay.setTypeface(Typeface.DEFAULT_BOLD);
            calendarFirstLine.addView(tvDay);
        }

        return calendarFirstLine;
    }

    public LinearLayout getDaysLayout(){
        int daysOfMonth = getDaysInMonth(calendarYear + "-" + calendarMonth + "-01");
        int firstDayOfWeek = getDayOfWeekIndex(calendarYear + "-" + calendarMonth + "-01");

        calendarDaysLayout.setOrientation(LinearLayout.VERTICAL);
        calendarDaysLayout.removeAllViews();

        int dayOfMonth = 1;
        LinearLayout currentWeekLayout;

        for (int week = 0; week < (int) Math.ceil((firstDayOfWeek + daysOfMonth) / 7.0); week++) {
            currentWeekLayout = new LinearLayout(getContext());
            LinearLayout.LayoutParams calendarWeekParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            currentWeekLayout.setLayoutParams(calendarWeekParams);
            currentWeekLayout.setOrientation(LinearLayout.HORIZONTAL);
            currentWeekLayout.setWeightSum(7);

            for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {
                LinearLayout dayOfWeekLayout = new LinearLayout(getContext());
                LinearLayout.LayoutParams dayOfWeekLayoutParams = new LinearLayout.LayoutParams(
                        0,
                        150,
                        1f
                );
                dayOfWeekLayout.setLayoutParams(dayOfWeekLayoutParams);
                dayOfWeekLayout.setOrientation(LinearLayout.VERTICAL);
                dayOfWeekLayout.setGravity(Gravity.CENTER_HORIZONTAL);

                TextView dayOfWeekTv = new TextView(getContext());
                LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                );
                dayOfWeekTv.setLayoutParams(tvParams);
                dayOfWeekTv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                dayOfWeekTv.setPadding(4, 4, 4, 4);

                ImageView dayOfWeekIcon = new ImageView(getContext());
                LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                        getDpInPx(getContext(), 16),
                        getDpInPx(getContext(), 16)
                );

                dayOfWeekIcon.setLayoutParams(iconParams);
//                dayOfWeekIcon.setBackgroundResource(R.drawable.alarm_bell_greyscale);

                int finalDayOfMonth = dayOfMonth;

                if ((week == 0 && dayOfWeek >= firstDayOfWeek) || (week > 0 && dayOfMonth <= daysOfMonth)){
                    dayOfWeekTv.setText(String.valueOf(dayOfMonth));
                    if (dayOfMonth == parseInt(calendarDay)){
                        dayOfWeekLayout.setBackgroundColor(CALENDAR_ACTIVE_COLOR);
                        dayOfWeekTv.setTypeface(Typeface.DEFAULT_BOLD);
                        dayOfWeekTv.setTextColor(context.getColor(R.color.white));
                    }
                    dayOfWeekLayout.setOnClickListener(v -> {
                        dayClicked(finalDayOfMonth);
                        if (!isNull(dayTvClicked)){
                            dayTvClicked.run();
                        }
                    });
                    dayOfMonth++;
                }
                dayOfWeekLayout.addView(dayOfWeekTv);
                currentWeekLayout.addView(dayOfWeekLayout);
            }
            calendarDaysLayout.addView(currentWeekLayout);
        }

        return calendarDaysLayout;
    }

    private int getDpInPx(Context c, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, c.getResources().getDisplayMetrics());
    }

    public TextView getButtonTodayLayout(){
        TextView btnSetToday = new TextView(context);
        btnSetToday.setPadding(
                getDpInPx(context, 8),
                getDpInPx(context, 4),
                getDpInPx(context, 8),
                getDpInPx(context, 4)
        );
        LinearLayout.LayoutParams btnSetTodayParams = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        btnSetToday.setLayoutParams(btnSetTodayParams);
//        btnSetToday.setBackgroundResource(R.drawable.rounded_box_orange);
        btnSetToday.setBackgroundColor(CALENDAR_ACTIVE_COLOR);
        btnSetToday.setTextColor(context.getColor(R.color.white));
        btnSetToday.setTypeface(Typeface.DEFAULT_BOLD);
        btnSetTodayParams.setMargins(
                getDpInPx(context, 4),
                getDpInPx(context, 8),
                getDpInPx(context, 4),
                getDpInPx(context, 8)
        );
        btnSetToday.setText("Aujourd'hui");
        btnSetToday.setOnClickListener(v -> {
            setDaySelected(getTodayDate());
            if (!isNull(dayTvClicked)) dayTvClicked.run();
        });
        return btnSetToday;
    }

    private String getTodayDate(){
        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int monthOfYear = cal.get(Calendar.MONTH)+1;
        int year = cal.get(Calendar.YEAR);

        String day = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
        String month = monthOfYear < 10 ? "0" + monthOfYear : String.valueOf(monthOfYear);

        return year + "-" + month + "-" + day;
    }

    public String[] getWeekRange(){
        WeekFields weekFields = WeekFields.of(Locale.FRANCE);
        LocalDate startDate = LocalDate.of(parseInt(calendarYear), 1, 1)
                .with(weekFields.weekOfYear(), calendarWeekNumber)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)); // Or SUNDAY for US

        LocalDate endDate = startDate.plusDays(6); // Assuming 7 days in a week

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return new String[]{startDate.format(formatter), endDate.format(formatter)};
    }

    public List<String> getListOfDatesOfWeek() {
        List<String> weekDates = new ArrayList<>();
        WeekFields weekFields = WeekFields.of(Locale.FRANCE);

        LocalDate startDate = LocalDate.of(parseInt(calendarYear), 1, 1)
                .with(weekFields.weekOfYear(), calendarWeekNumber)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)); // Or SUNDAY for US

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 0; i < 7; i++) {
            weekDates.add(startDate.plusDays(i).format(formatter));
        }

        return weekDates;
    }

    private int getWeekNumberFromDate(String dateString) {
        try {
            Locale locale = Locale.FRANCE;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(dateString, formatter);

            WeekFields weekFields = WeekFields.of(locale);
            return date.get(weekFields.weekOfYear());

        } catch (Exception e) {
            // Handle parsing errors or other exceptions
            return -1; // Or throw an exception
        }
    }

    private int getDaysInMonth(String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate locale = LocalDate.parse(dateString, formatter);

        return locale.lengthOfMonth();
    }

    private int getDayOfWeekIndex(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateString, formatter);

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.getValue() - 1; // Adjust to 0-based index
    }

    private void dayClicked(int dayClicked){
        String day = dayClicked < 10 ? "0" + dayClicked : String.valueOf(dayClicked);
        setDaySelected(calendarYear + "-" + calendarMonth + "-" + day);
    }

    public void setDaySelected(String dayS){
        selectedDay = dayS;
        calendarDay = dayS.split("-")[2];
        calendarMonth = dayS.split("-")[1];
        calendarYear = dayS.split("-")[0];
        calendarWeekNumber = getWeekNumberFromDate(selectedDay);

        if (calendarDaysLayout != null){
            getDaysLayout();
            getMonthLayout();
        }
    }

    public String getDaySelected(){
        return selectedDay;
    }

    private void addXDay(int x){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(selectedDay, formatter);
        LocalDate newDate = date.plusDays(x);
        setDaySelected(newDate.format(formatter));
    }

    private void addXWeek(int x){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(selectedDay, formatter);
        LocalDate newDate = date.plusDays((long) 7*x);
        setDaySelected(newDate.format(formatter));
    }

    private void addXMonth(int x){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(selectedDay, formatter);
        LocalDate newDate = date.plusMonths(x);
        setDaySelected(newDate.format(formatter));
    }

    private void addXYear(int x){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(selectedDay, formatter);
        LocalDate newDate = date.plusYears(x);
        setDaySelected(newDate.format(formatter));
    }

    public void addInterval(int incr, String i){
        String interval = i.toLowerCase();
        switch(interval){
            case "day":
            case "days":
                addXDay(incr);
                break;
            case "week":
            case "weeks":
                addXWeek(incr);
                break;
            case "month":
            case "monthes":
                addXMonth(incr);
                break;
            case "year":
            case "years":
                addXYear(incr);
                break;
            default:
                // empty default
                break;
        }

    }
}
