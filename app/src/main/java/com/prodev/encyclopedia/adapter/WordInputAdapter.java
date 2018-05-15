package com.prodev.encyclopedia.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.prodev.encyclopedia.R;
import com.prodev.encyclopedia.container.Word;
import com.prodev.encyclopedia.container.WordSet;
import com.simplelib.adapter.SimpleRecyclerAdapter;

import java.util.ArrayList;

public class WordInputAdapter extends SimpleRecyclerAdapter<Word> {
    private WordSet set;

    private Window window;

    public WordInputAdapter() {
    }

    public WordInputAdapter(ArrayList<Word> list) {
        super(list);
    }

    public WordInputAdapter(WordSet set) {
        super(set.getAllWords());

        this.set = set;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    @Override
    public View createHolder(ViewGroup viewGroup, int i) {
        return inflateLayout(viewGroup, R.layout.word_input_item);
    }

    @Override
    public void bindHolder(ViewHolder viewHolder, final Word word, int i) {
        TextView titleView = (TextView) viewHolder.findViewById(R.id.word_input_item_title);
        EditText editText = (EditText) viewHolder.findViewById(R.id.word_input_item_text);

        try {
            titleView.setText(word.getLanguage().getName());

            if (!word.isEmpty())
                editText.setText(word.getText());
            else
                editText.setText("");
        } catch (Exception e) {
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                word.setText(s.toString());

                if (set != null)
                    set.put(word);
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && window != null) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
    }
}
