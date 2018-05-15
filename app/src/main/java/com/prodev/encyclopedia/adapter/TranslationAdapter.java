package com.prodev.encyclopedia.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prodev.encyclopedia.R;
import com.prodev.encyclopedia.container.Translation;
import com.prodev.encyclopedia.container.Word;
import com.simplelib.adapter.SimpleRecyclerAdapter;

import java.util.ArrayList;

public abstract class TranslationAdapter extends SimpleRecyclerAdapter<Translation> {
    public TranslationAdapter() {
    }

    public TranslationAdapter(ArrayList<Translation> list) {
        super(list);
    }

    @Override
    public View createHolder(ViewGroup viewGroup, int i) {
        return inflateLayout(viewGroup, R.layout.translation_item);
    }

    @Override
    public void bindHolder(ViewHolder viewHolder, final Translation translation, int i) {
        final Word word = translation.getFrom();

        TextView textView = (TextView) viewHolder.findViewById(R.id.translation_item_text);
        TextView moreView = (TextView) viewHolder.findViewById(R.id.translation_item_more);

        if (word.isEmpty()) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(word.getText());
            textView.setVisibility(View.VISIBLE);
        }

        int translationCount = translation.getTranslationCount();
        if (!word.isEmpty() && translationCount > 1) {
            String infoText = getContext().getString(R.string.translations_found_info);
            infoText = infoText.replace("@c", Integer.toString(translationCount));
            infoText = infoText.replace("@w", word.getText());

            moreView.setText(infoText);
            moreView.setVisibility(View.VISIBLE);
        } else {
            moreView.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTranslation(translation);
            }
        });
    }

    public abstract void onClickTranslation(Translation translation);
}
