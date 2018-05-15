package com.prodev.encyclopedia.dialogs.custom;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.prodev.encyclopedia.R;
import com.prodev.encyclopedia.adapter.WordInputAdapter;
import com.prodev.encyclopedia.container.WordSet;
import com.prodev.encyclopedia.dialogs.CustomDialog;

public abstract class WordAddDialog extends CustomDialog {
    private WordSet set;

    private RecyclerView list;
    private LinearLayoutManager manager;
    private WordInputAdapter adapter;

    public WordAddDialog(Context context) {
        super(context, R.layout.word_add_dialog);

        setCancelable(true);
        setTheme(R.style.DialogStyle);
    }

    @Override
    public void create(View contentView) {
        set = new WordSet();

        list = (RecyclerView) findViewById(R.id.word_add_list);

        manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(manager);

        list.addItemDecoration(new DividerItemDecoration(getContext(), manager.getOrientation()));

        adapter = new WordInputAdapter(set);
        list.setAdapter(adapter);
    }

    @Override
    public void build() {
        super.build();

        adapter.setWindow(getWindow());
    }

    @Override
    public boolean onAccept() {
        if (!set.isEmpty())
            onAdd(set);
        return true;
    }

    @Override
    public boolean onDeny() {
        return true;
    }

    public abstract void onAdd(WordSet set);
}
