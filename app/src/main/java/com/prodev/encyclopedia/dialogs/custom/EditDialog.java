package com.prodev.encyclopedia.dialogs.custom;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.prodev.encyclopedia.R;
import com.prodev.encyclopedia.dialogs.CustomDialog;

public abstract class EditDialog extends CustomDialog {
    private EditText inputView;

    private String text;
    private String hint;

    private boolean singleLine;

    public EditDialog(Context context) {
        super(context, R.layout.edit_dialog);

        setSingleLine(true);

        setCancelable(true);
        setTheme(R.style.DialogStyle);
    }

    public void setText(String text) {
        this.text = text;

        if (inputView != null && text != null)
            inputView.setText(text);
    }

    public void setHint(String hint) {
        this.hint = hint;

        if (inputView != null && hint != null)
            inputView.setHint(hint);
    }

    public void setSingleLine(boolean singleLine) {
        this.singleLine = singleLine;
    }

    @Override
    public void create(View contentView) {
        inputView = (EditText) findViewById(R.id.edit_dialog_input);

        if (text != null) inputView.setText(text);
        if (hint != null) inputView.setHint(hint);

        inputView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hide();
                    onAccept();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onAccept() {
        String result = inputView.getText().toString();
        if (singleLine) result = result.replace("\n", " ");
        onResult(result);
        return true;
    }

    @Override
    public boolean onDeny() {
        return true;
    }

    public abstract void onResult(String text);
}
