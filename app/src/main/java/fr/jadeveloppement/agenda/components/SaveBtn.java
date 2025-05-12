package fr.jadeveloppement.agenda.components;

import android.content.Context;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import fr.jadeveloppement.agenda.R;
import fr.jadeveloppement.agenda.functions.Functions;

public class SaveBtn extends TextView {

    private final Context context;
    private final TextView saveLayout;

    public SaveBtn(Context c){
        super(c);
        this.context = c;
        this.saveLayout = new TextView(context);
        saveLayout.setPadding(
                Functions.getDpInPx(context,8),
                Functions.getDpInPx(context,8),
                Functions.getDpInPx(context,8),
                Functions.getDpInPx(context,8));

        saveLayout.setLayoutParams(
                new ViewGroup.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        saveLayout.setText("Valider");
        saveLayout.setBackgroundResource(R.drawable.rounded_box_orange);
        saveLayout.setTextColor(context.getColor(R.color.white));
        saveLayout.setTypeface(Typeface.DEFAULT_BOLD);
        saveLayout.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
    }

    public TextView getBtnLayout(){
        return saveLayout;
    }
}
