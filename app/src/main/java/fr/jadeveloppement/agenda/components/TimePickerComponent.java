package fr.jadeveloppement.agenda.components;

import static java.util.Objects.isNull;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import fr.jadeveloppement.agenda.functions.Functions;

public class TimePickerComponent extends LinearLayout {

    private final Context context;
    private final LinearLayout timePickerLayout;
    private Runnable callback;
    private TimePicker timePicker;
    private SaveBtn btnSave;

    public TimePickerComponent(Context c){
        super(c);
        this.context = c;
        this.timePickerLayout = new LinearLayout(context);
        this.btnSave = new SaveBtn(context);

        initLayout();
    }

    public TimePickerComponent(Context c, Runnable call){
        super(c);
        this.context = c;
        this.timePickerLayout = new LinearLayout(context);
        this.btnSave = new SaveBtn(context);
        this.callback = call;
        timePickerLayout.removeAllViews();

        initLayout();
    }

    private void initLayout(){
        timePickerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        ));
        timePickerLayout.setOrientation(LinearLayout.VERTICAL);
        timePickerLayout.setGravity(Gravity.CENTER);
        timePickerLayout.setPadding(
                Functions.getDpInPx(context, 8),
                Functions.getDpInPx(context, 8),
                Functions.getDpInPx(context, 8),
                Functions.getDpInPx(context, 8)
        );

        timePicker = new TimePicker(context);
        timePicker.setLayoutParams(
                new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                )
        );
        timePicker.setIs24HourView(true);

        timePicker.setOnTimeChangedListener((TimePicker view, int hourOfDay, int minute) -> {
            if (!isNull(callback)) callback.run();
        });

        timePickerLayout.addView(timePicker);
        timePickerLayout.addView(btnSave.getBtnLayout());
    }

    public String getHour(){
        String hour = timePicker.getHour() < 10 ? "0" + timePicker.getHour() : String.valueOf(timePicker.getHour());
        String minute = timePicker.getMinute() < 10 ? "0" + timePicker.getMinute() : String.valueOf(timePicker.getMinute());
        return hour + ":" + minute;
    }

    public LinearLayout getTimePickerLayout(){
        return timePickerLayout;
    }

    public TextView getBtnSave() {
        return btnSave.getBtnLayout();
    }
}
