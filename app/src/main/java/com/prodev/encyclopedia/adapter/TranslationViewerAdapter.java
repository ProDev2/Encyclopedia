package com.prodev.encyclopedia.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prodev.encyclopedia.R;
import com.prodev.encyclopedia.container.Word;
import com.simplelib.adapter.SimpleRecyclerAdapter;

import java.util.ArrayList;

public class TranslationViewerAdapter extends SimpleRecyclerAdapter<Word> {
    public TranslationViewerAdapter() {
    }

    public TranslationViewerAdapter(ArrayList<Word> list) {
        super(list);
    }

    @Override
    public View createHolder(ViewGroup viewGroup, int i) {
        return inflateLayout(viewGroup, R.layout.translation_viewer_item);
    }

    @Override
    public void bindHolder(ViewHolder viewHolder, Word word, int i) {
        TextView textView = (TextView) viewHolder.findViewById(R.id.translation_viewer_item_text);

        if (!word.isEmpty()) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(word.getText());
        } else {
            textView.setVisibility(View.GONE);
        }
    }
}
