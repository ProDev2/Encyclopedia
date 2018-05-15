package com.prodev.encyclopedia.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prodev.encyclopedia.R;
import com.prodev.encyclopedia.container.Word;
import com.simplelib.adapter.SimpleRecyclerAdapter;

import java.util.ArrayList;

public abstract class WordAdapter extends SimpleRecyclerAdapter<Word> {
    public WordAdapter() {
    }

    public WordAdapter(ArrayList<Word> list) {
        super(list);
    }

    @Override
    public View createHolder(ViewGroup viewGroup, int i) {
        return inflateLayout(viewGroup, R.layout.word_item);
    }

    @Override
    public void bindHolder(ViewHolder viewHolder, final Word word, int i) {
        TextView titleView = (TextView) viewHolder.findViewById(R.id.word_item_title);
        TextView textView = (TextView) viewHolder.findViewById(R.id.word_item_text);

        try {
            titleView.setText(word.getLanguage().getName());

            if (!word.isEmpty()) {
                textView.setText(word.getText());
                textView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelect(word);
            }
        });
    }

    public abstract void onSelect(Word word);
}
