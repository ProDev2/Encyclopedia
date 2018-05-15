package com.prodev.encyclopedia.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prodev.encyclopedia.R;
import com.prodev.encyclopedia.container.Language;
import com.simplelib.adapter.SimpleRecyclerAdapter;

public abstract class LanguageSelectorAdapter extends SimpleRecyclerAdapter<Language> {
    private Language selected;

    public LanguageSelectorAdapter() {
        this(null);
    }

    public LanguageSelectorAdapter(Language selected) {
        super(Language.getAllLanguages());

        this.selected = selected;
    }

    public void setSelected(Language selected) {
        this.selected = selected;

        try {
            if (getRecyclerView() != null)
                notifyDataSetChanged();
        } catch (Exception e) {
        }
    }

    @Override
    public View createHolder(ViewGroup viewGroup, int i) {
        return inflateLayout(viewGroup, R.layout.language_item);
    }

    @Override
    public void bindHolder(ViewHolder viewHolder, final Language language, int i) {
        ImageView checkView = (ImageView) viewHolder.findViewById(R.id.language_item_check);
        TextView textView = (TextView) viewHolder.findViewById(R.id.language_item_text);

        if (selected != null && selected.isEqualTo(language))
            checkView.setVisibility(View.VISIBLE);
        else
            checkView.setVisibility(View.INVISIBLE);

        textView.setText(language.getName());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = language;
                try {
                    if (getRecyclerView() != null)
                        notifyDataSetChanged();
                } catch (Exception e) {
                }

                onSelect(language);
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onLongSelect(language);
                return true;
            }
        });
    }

    public abstract void onSelect(Language language);

    public abstract void onLongSelect(Language language);
}
