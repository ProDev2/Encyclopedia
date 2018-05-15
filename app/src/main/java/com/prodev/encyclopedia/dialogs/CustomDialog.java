package com.prodev.encyclopedia.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.R;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public abstract class CustomDialog {
    public static final int LIGHT_THEME = R.style.Theme_AppCompat_Light_Dialog_Alert;
    public static final int DARK_THEME = R.style.Theme_AppCompat_Dialog_Alert;

    public static final String DEFAULT_POSITIVE_TEXT = "Ok";
    public static final String DEFAULT_NEGATIVE_TEXT = "Cancel";

    protected boolean cancelable = true;
    protected boolean showButtons = true;
    protected boolean positiveButton = true;
    protected boolean negativeButton = true;
    protected String positiveText = DEFAULT_POSITIVE_TEXT;
    protected String negativeText = DEFAULT_NEGATIVE_TEXT;
    private Context context;
    private View contentView;
    private int theme;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private String title;

    public CustomDialog(Context context, int layoutId) {
        this.context = context;

        setLayoutId(layoutId);
    }

    public CustomDialog(Context context, View contentView) {
        this.context = context;

        setContentView(contentView);
    }

    public CustomDialog(Context context, int layoutId, String title) {
        this.context = context;
        this.title = title;

        setLayoutId(layoutId);
    }

    public CustomDialog(Context context, View contentView, String title) {
        this.context = context;
        this.title = title;

        setContentView(contentView);
    }

    public void setLayoutId(int layoutId) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setContentView(layoutInflater.inflate(layoutId, null));
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    public View findViewById(int id) {
        if (contentView != null)
            return contentView.findViewById(id);
        else
            return null;
    }

    public void setTheme(int id) {
        this.theme = id;
    }

    public void setShowButtons(boolean showButtons) {
        this.showButtons = showButtons;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public void setTitle(String title) {
        this.title = title;

        if (dialog != null)
            dialog.setTitle(title);
    }

    public Context getContext() {
        return context;
    }

    public Window getWindow() {
        if (dialog != null)
            return dialog.getWindow();
        else
            return null;
    }

    public void build() {
        builder = new AlertDialog.Builder(context, theme);
        builder.setTitle(title);

        create(contentView);
        builder.setView(contentView);

        if (showButtons && positiveButton) {
            builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                    boolean close = onAccept();
                    if (!close) {
                        build();
                        show();
                    }
                }
            });
        }
        if (showButtons && negativeButton) {
            builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                    boolean close = onDeny();
                    if (!close) {
                        build();
                        show();
                    }
                }
            });
        }

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                hideKeyboard();
            }
        });

        dialog = builder.create();
        dialog.setCancelable(cancelable);

        if (title == null || (title != null && title.length() <= 0))
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        WindowManager.LayoutParams dialogParams = dialog.getWindow().getAttributes();
        dialogParams.dimAmount = 0.7f;
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    public void show() {
        if (dialog == null)
            build();

        dialog.show();
    }

    public void hide() {
        if (dialog != null)
            dialog.hide();

        hideKeyboard();
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    public abstract void create(View contentView);

    public abstract boolean onAccept();

    public abstract boolean onDeny();
}
