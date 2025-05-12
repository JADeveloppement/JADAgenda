package fr.jadeveloppement.agenda.components;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.Calendar;

import fr.jadeveloppement.agenda.R;
import fr.jadeveloppement.agenda.functions.Functions;

public class CustomCalendarPicker extends LinearLayout {
    private final String TAG = "JADagenda";

    private final Context context;
    private final LinearLayout calendarLayout;
    private final LinearLayout.LayoutParams calendarLayoutParams;
    private final SaveBtn calendarPickerSaveBtn;

    private final CustomCalendar calendar;
    private final TextView calendarPickerDatePreview;
    private final TextView calendarPickerDateTitle;
    private final LinearLayout datePreviewContainer;

    private final LinearLayout clockPreviewContainer;
    private final TextView calendarPickerClockPreview;

    private TimePickerComponent timePickerComponent;
    private Runnable callback;

    private String selectedDate = Functions.getTodayDate();

    public CustomCalendarPicker(Context c){
        super(c);
        this.context = c;
        this.calendarLayout = new LinearLayout(context);
        this.calendarLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        this.datePreviewContainer = new LinearLayout(context);
        this.calendarPickerDatePreview = new TextView(context);
        this.calendarPickerSaveBtn = new SaveBtn(context);
        this.calendar = new CustomCalendar(context, this::dateChanged);
        this.calendarPickerDateTitle = new TextView(context);

        this.clockPreviewContainer = new LinearLayout(context);
        this.calendarPickerClockPreview = new TextView(context);
        this.timePickerComponent = new TimePickerComponent(context, this::timeChanged);

        initLayout();
    }

    public CustomCalendarPicker(Context c, Runnable call){
        super(c);
        this.context = c;
        this.calendarLayout = new LinearLayout(context);
        this.calendarLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        this.datePreviewContainer = new LinearLayout(context);
        this.calendarPickerDatePreview = new TextView(context);
        this.calendarPickerSaveBtn = new SaveBtn(context);
        this.calendar = new CustomCalendar(context, this::dateChanged);
        this.calendarPickerDateTitle = new TextView(context);

        this.clockPreviewContainer = new LinearLayout(context);
        this.calendarPickerClockPreview = new TextView(context);
        this.timePickerComponent = new TimePickerComponent(context, this::timeChanged);

        this.callback = call;

        initLayout();
    }

    private void initLayout(){
        calendar.setDaySelected(selectedDate);
        calendarLayout.removeAllViews();
        calendarLayout.setOrientation(LinearLayout.VERTICAL);
        calendarLayout.setLayoutParams(calendarLayoutParams);

        setTitle();
        setDatePreview();
        setClockPreview();

        calendarLayout.addView(calendarPickerDateTitle);

        calendarLayout.addView(calendar.getMonthLayout());
        calendarLayout.addView(calendar.getFirstLine());
        calendarLayout.addView(calendar.getDaysLayout());

        calendarLayout.addView(datePreviewContainer);
        calendarLayout.addView(clockPreviewContainer);

        calendarLayout.addView(calendarPickerSaveBtn.getBtnLayout());

        dateChanged();
    }

    private void setClockPreview(){
        clockPreviewContainer.setLayoutParams(calendarLayoutParams);
        clockPreviewContainer.setOrientation(LinearLayout.HORIZONTAL);
        clockPreviewContainer.setGravity(Gravity.CENTER_VERTICAL);
        clockPreviewContainer.setWeightSum(10f);

        LinearLayout.LayoutParams paramsIconContainer = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        );
        paramsIconContainer.setMarginEnd(Functions.getDpInPx(context, 8));

        LinearLayout layoutClockIcon = new LinearLayout(context);
        layoutClockIcon.setLayoutParams(paramsIconContainer);
        layoutClockIcon.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        layoutClockIcon.setClickable(false);

        ImageView clockPreviewIcon = new ImageView(context);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                Functions.getDpInPx(context, 36),
                Functions.getDpInPx(context,36)
        );
        iconParams.setMarginEnd(Functions.getDpInPx(context, 8));
        clockPreviewIcon.setLayoutParams(iconParams);
        clockPreviewIcon.setBackgroundResource(R.drawable.clock);
        clockPreviewIcon.setClickable(false);
        layoutClockIcon.addView(clockPreviewIcon);


        LinearLayout layoutClockNowIcon = new LinearLayout(context);
        layoutClockNowIcon.setLayoutParams(paramsIconContainer);
        layoutClockNowIcon.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);

        ImageView clockNowIcon = new ImageView(context);
        iconParams.setMarginEnd(Functions.getDpInPx(context, 8));
        iconParams.setMarginEnd(0);
        clockNowIcon.setLayoutParams(iconParams);
        clockNowIcon.setBackgroundResource(R.drawable.time);
        layoutClockNowIcon.addView(clockNowIcon);

        calendarPickerClockPreview.setPadding(
                Functions.getDpInPx(context,8),
                Functions.getDpInPx(context,16),
                Functions.getDpInPx(context,8),
                Functions.getDpInPx(context,24));
        calendarPickerClockPreview.setLayoutParams(
                new LinearLayout.LayoutParams(
                        0,
                        LayoutParams.WRAP_CONTENT,
                        8f
                )
        );
        calendarPickerClockPreview.setTextAppearance(android.R.style.TextAppearance_Medium);
        calendarPickerClockPreview.setHint("Choisissez un horaire de rappel");
        calendarPickerClockPreview.setText(getTime());

        layoutClockNowIcon.setOnClickListener(v -> {
            calendarPickerClockPreview.setText(getTime());
        });

        clockPreviewContainer.setOnClickListener(v -> {
            Popup popupClock = new Popup(context, this, null);
            timePickerComponent = new TimePickerComponent(context, this::timeChanged);
            timePickerComponent.getBtnSave().setOnClickListener(v1 -> {
                popupClock.closePopup();
            });
            popupClock.addContent(timePickerComponent.getTimePickerLayout());
        });

        clockPreviewContainer.addView(layoutClockIcon);
        clockPreviewContainer.addView(calendarPickerClockPreview);
        clockPreviewContainer.addView(layoutClockNowIcon);
    }

    private String getTime(){
        Calendar cal = Calendar.getInstance();
        String hour = cal.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + cal.get(Calendar.HOUR_OF_DAY) : String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
        String minute = cal.get(Calendar.MINUTE) < 10 ? "0" + cal.get(Calendar.MINUTE) : String.valueOf(cal.get(Calendar.MINUTE));
        return hour + ":" + minute;
    }

    private void timeChanged(){
        calendarPickerClockPreview.setText(timePickerComponent.getHour());
    }

    private void setDatePreview(){
        datePreviewContainer.setLayoutParams(calendarLayoutParams);
        datePreviewContainer.setOrientation(LinearLayout.HORIZONTAL);
        datePreviewContainer.setGravity(Gravity.CENTER_VERTICAL);

        ImageView datePreviewIcon = new ImageView(context);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                Functions.getDpInPx(context, 36),
                Functions.getDpInPx(context,36)
        );
        iconParams.setMarginEnd(Functions.getDpInPx(context, 8));
        datePreviewIcon.setLayoutParams(iconParams);
        datePreviewIcon.setBackgroundResource(R.drawable.schedule);

        calendarPickerDatePreview.setPadding(
                Functions.getDpInPx(context,8),
                Functions.getDpInPx(context,16),
                Functions.getDpInPx(context,8),
                Functions.getDpInPx(context,24));
        calendarPickerDatePreview.setTextAppearance(android.R.style.TextAppearance_Medium);

        datePreviewContainer.addView(datePreviewIcon);
        datePreviewContainer.addView(calendarPickerDatePreview);
    }

    private void setTitle(){
        calendarPickerDateTitle.setPadding(
                Functions.getDpInPx(context,8),
                Functions.getDpInPx(context,16),
                Functions.getDpInPx(context,8),
                Functions.getDpInPx(context,24));

        calendarPickerDateTitle.setTextAppearance(android.R.style.TextAppearance_Large);
        calendarPickerDateTitle.setTypeface(Typeface.DEFAULT_BOLD);
        calendarPickerDateTitle.setTextColor(context.getColor(R.color.orange1));
        calendarPickerDateTitle.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        calendarPickerDateTitle.setVisibility(View.GONE);
    }

    private void dateChanged(){
        selectedDate = calendar.getDaySelected();
        calendarPickerDatePreview.setText(Functions.convertStdDateToLocale(selectedDate));
    }

    public void setPickerTitle(String title){
        calendarPickerDateTitle.setVisibility(View.VISIBLE);
        calendarPickerDateTitle.setText(title);
    }

    public LinearLayout getCalendarLayout(){
        return calendarLayout;
    }

    public TextView getBtnSave(){
        return calendarPickerSaveBtn.getBtnLayout();
    }

    public String getDateTime(){
        return calendarPickerDatePreview.getText().toString() + " " + calendarPickerClockPreview.getText().toString();
    }
}
