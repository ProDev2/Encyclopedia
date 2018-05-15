package com.prodev.encyclopedia.adapter;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.prodev.encyclopedia.R;
import com.prodev.encyclopedia.container.Word;
import com.prodev.encyclopedia.container.WordSet;
import com.prodev.encyclopedia.dialogs.custom.EditDialog;
import com.simplelib.adapter.SimpleRecyclerAdapter;

import java.util.ArrayList;

public abstract class WordSetAdapter extends SimpleRecyclerAdapter<WordSet> {
    public WordSetAdapter() {
    }

    public WordSetAdapter(ArrayList<WordSet> list) {
        super(list);
    }

    @Override
    public View createHolder(ViewGroup viewGroup, int i) {
        return inflateLayout(viewGroup, R.layout.wordset_item);
    }

    @Override
    public void bindHolder(ViewHolder viewHolder, final WordSet set, int i) {
        RecyclerView wordList = (RecyclerView) viewHolder.findViewById(R.id.wordset_item_list);

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        wordList.setLayoutManager(manager);

        wordList.addItemDecoration(new DividerItemDecoration(getContext(), manager.getOrientation()));

        WordAdapter adapter = new WordAdapter(set.getAllWords()) {
            @Override
            public void onSelect(Word word) {
                openEditDialog(set, word, this);
            }
        };
        wordList.setAdapter(adapter);

        ImageButton deleteButton = (ImageButton) viewHolder.findViewById(R.id.wordset_item_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDelete(set);
            }
        });
    }

    private void openEditDialog(final WordSet set, final Word editWord, final WordAdapter adapter) {
        EditDialog dialog = new EditDialog(getContext()) {
            @Override
            public void onResult(String text) {
                WordSet fromSet = set.copy();

                editWord.setText(text);
                set.put(editWord);

                try {
                    int pos = adapter.getList().indexOf(editWord);
                    if (pos >= 0)
                        adapter.notifyItemChanged(pos);
                    else
                        adapter.notifyDataSetChanged();
                } catch (Exception e) {
                }

                onEdit(fromSet, set);
            }
        };
        dialog.setHint(getContext().getString(R.string.enter_word_text));
        if (!editWord.isEmpty())
            dialog.setText(editWord.getText());
        dialog.show();
    }

    public abstract void onEdit(WordSet fromSet, WordSet toSet);

    public abstract void onDelete(WordSet set);
}
