package fr.jadeveloppement.agenda.functions;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class TextWatcherFunction implements TextWatcher {
    private final EditText editText;

    public TextWatcherFunction(EditText eT) {
        this.editText = eT;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            char firstChar = s.charAt(0);
            if (Character.isLowerCase(firstChar)) {
                s.replace(0, 1, String.valueOf(Character.toUpperCase(firstChar)));
                editText.setSelection(editText.length()); // Move cursor to the end of the first character
            }
        }
    }
}