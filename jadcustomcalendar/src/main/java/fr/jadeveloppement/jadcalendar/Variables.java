package fr.jadeveloppement.jadcalendar;

import android.graphics.Color;
import android.util.Log;

public class Variables {
    private static final String TAG = "JADCustomCalendar";

    public enum CalendarColor {
        ORANGE, RED, BLUE
    };

    public static int getColor(Enum color){
        if (color == CalendarColor.ORANGE)
            return Color.parseColor("#FFA500");
        else if (color == CalendarColor.RED)
            return Color.parseColor("#B22222");
        else if (color == CalendarColor.BLUE)
            return Color.parseColor("#4169E1");
        else return Color.parseColor("#000000");
    }

    public static final String[] monthesNamesLong = {
            "Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre",
            "Novembre", "Décembre"
    };

    public static final String[] daysShortNames = {
            "L", "M", "M", "J", "V", "S", "D"
    };
}
