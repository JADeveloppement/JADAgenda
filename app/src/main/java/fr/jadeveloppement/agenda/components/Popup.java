package fr.jadeveloppement.agenda.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import fr.jadeveloppement.agenda.MainActivity;
import fr.jadeveloppement.agenda.R;

public class Popup {

    private final Context context;
    private View popupView;
    private TaskItemAdapter adapter;
    private View container;

    public Popup(Context c){
        this.context = c;
        this.popupView = LayoutInflater.from(context).inflate(R.layout.popup_layout, null);

        initPopup();
    }

    public Popup(Context c, View cont, TaskItemAdapter a){
        this.context = c;
        this.popupView = LayoutInflater.from(context).inflate(R.layout.popup_layout, null);
        this.container = cont;
        this.adapter = a;
        initPopup();
    }

    private PopupWindow popupWindow;

    private void initPopup(){
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.setInputMethodMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.showAsDropDown(MainActivity.getMainView(), 0, 0);
    }

    private View content;

    public void addContent(View v){
        content = v;
        LinearLayout popupContentContainer = popupView.findViewById(R.id.popupLayoutContentContainer);
        popupContentContainer.removeAllViews();
        popupContentContainer.addView(v);
    }

    public View getContentView(){
        return content;
    }

    public void closePopup(){
        popupWindow.dismiss();
    }
}
