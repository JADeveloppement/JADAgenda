package fr.jadeveloppement.agenda.components;

import android.app.TimePickerDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicReference;

import fr.jadeveloppement.agenda.R;
import fr.jadeveloppement.jadcustomcalendar.CustomCalendar;

public class PopupAddTaskReminder extends LinearLayout implements fr.jadeveloppement.jadcustomcalendar.CustomCalendar.DateChanged {
    private final String TAG = "JADagenda";

    private final Context context;

    private LinearLayout popupAddTaskReminderSetHour;
    private TextView popupAddTaskReminderHourSelected;
    private AtomicReference<String> timeSelected = new AtomicReference<>("");
    private AtomicReference<String> daySelected = new AtomicReference<>("");
    private CustomCalendar customCalendar;

    private Button popupAddTaskReminderSave;

    public PopupAddTaskReminder(Context c){
        super(c);
        this.context = c;

        LayoutInflater.from(context).inflate(R.layout.popup_task_add_reminder, this, true);

        initLayout();
        setPopupEvents();
    }

    private void setPopupEvents() {
        popupAddTaskReminderSetHour.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                    (view, selectedHour, selectedMinute) -> {
                        timeSelected.set(String.format("%02d:%02d", selectedHour, selectedMinute));
                    },
                    hour,
                    minute,
                    true);

            // Show the TimePickerDialog
            timePickerDialog.show();

            timePickerDialog.setOnDismissListener(v1 -> {
                if (!timeSelected.get().isBlank()) popupAddTaskReminderHourSelected.setText(timeSelected.get());
            });
        });
    }

    private void initLayout(){
        popupAddTaskReminderSetHour = findViewById(R.id.popupAddTaskReminderSetHour);
        popupAddTaskReminderHourSelected = findViewById(R.id.popupAddTaskReminderHourSelected);

        customCalendar = findViewById(R.id.popupAddTaskReminderCalendar);
        daySelected.set(customCalendar.getDaySelected());

        popupAddTaskReminderSave = findViewById(R.id.popupAddTaskReminderSave);
    }

    public String getDaySelected(){
        return daySelected.get();
    }

    public String getTimeSelected(){
        return timeSelected.get();
    }

    public Button getPopupAddTaskReminderSave(){
        return popupAddTaskReminderSave;
    }

    @Override
    public void selectedDayChanged() {
        daySelected.set(customCalendar.getDaySelected());
    }
}
