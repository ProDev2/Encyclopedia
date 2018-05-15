package com.prodev.encyclopedia.helper;

import android.support.v4.app.FragmentActivity;

import com.prodev.encyclopedia.container.Translation;
import com.prodev.encyclopedia.container.TranslationSet;
import com.prodev.encyclopedia.container.Word;
import com.prodev.encyclopedia.fetcher.EncyclopediaFetcher;
import com.prodev.encyclopedia.interfaces.Encyclopedia;
import com.simplelib.adapter.SimpleRecyclerAdapter;

public class SearchHelper implements Runnable {
    private FragmentActivity activity;
    private TranslationHelper translationHelper;
    private SimpleRecyclerAdapter<Translation> adapter;

    private String searchFor;

    private Thread thread;

    public SearchHelper(FragmentActivity activity, TranslationHelper translationHelper, SimpleRecyclerAdapter<Translation> adapter) {
        this.activity = activity;
        this.translationHelper = translationHelper;
        this.adapter = adapter;
    }

    public void searchFor(String text) {
        this.searchFor = text;

        cancel();

        thread = new Thread(this);
        thread.start();
    }

    public void cancel() {
        try {
            if (thread != null)
                thread.interrupt();
        } catch (Exception e) {
        }
    }

    @Override
    public void run() {
        if (searchFor != null && searchFor.length() > 0 && translationHelper.getFrom() != null && translationHelper.getTo() != null) {
            Encyclopedia encyclopedia = EncyclopediaFetcher.getGlobalEncyclopedia();

            Word searchWord = new Word(translationHelper.getFrom(), searchFor);
            TranslationSet translationSet = new TranslationSet(searchWord, translationHelper.getTo());
            encyclopedia.translate(translationSet);

            adapter.getList().clear();
            adapter.getList().addAll(translationSet.getAll());

            postUpdate();
        } else {
            postClear();
        }
    }

    private void postUpdate() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void postClear() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.getList().clear();
                adapter.notifyDataSetChanged();
            }
        });
    }
}
