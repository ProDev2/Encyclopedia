package com.prodev.encyclopedia.fragments;

import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.prodev.encyclopedia.R;
import com.prodev.encyclopedia.adapter.LanguageSelectorAdapter;
import com.prodev.encyclopedia.container.Language;
import com.prodev.encyclopedia.dialogs.custom.EditDialog;
import com.prodev.encyclopedia.fetcher.EncyclopediaFetcher;
import com.prodev.encyclopedia.fetcher.LanguageFetcher;
import com.prodev.encyclopedia.helper.TranslationHelper;
import com.prodev.encyclopedia.interfaces.Encyclopedia;
import com.simplelib.SimpleFragment;

public class LanguageSelectorFragment extends SimpleFragment {
    private OnSelectLanguageListener listener;

    private Language selected;

    private ViewGroup contentView;

    private RecyclerView list;
    private RecyclerView.LayoutManager manager;
    private LanguageSelectorAdapter adapter;

    public LanguageSelectorFragment() {
        super(R.layout.language_selector_fragment);
    }

    @Override
    public void create(View view) {
        enableOptionsMenu(R.menu.language_selector_menu);

        contentView = (ViewGroup) findViewById(R.id.language_selector_fragment_content_view);

        list = (RecyclerView) findViewById(R.id.language_selector_fragment_list);

        manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(manager);

        adapter = new LanguageSelectorAdapter(selected) {
            @Override
            public void onSelect(Language language) {
                if (listener != null)
                    listener.onSelect(language);

                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }

            @Override
            public void onLongSelect(Language language) {
                onRemove(language);
            }
        };
        list.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        updateMenu();
    }

    private void updateMenu() {
        try {
            MenuItem addItem = getMenu().findItem(R.id.language_selector_add);

            if (EncyclopediaFetcher.fetcher != null && EncyclopediaFetcher.getGlobalEncyclopedia().canAddLanguage())
                addItem.setVisible(true);
            else
                addItem.setVisible(false);
        } catch (Exception e) {
        }
    }

    private void onRemove(final Language language) {
        Snackbar snackbar = Snackbar
                .make(contentView, getString(R.string.remove_language), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.remove), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        language.remove();

                        try {
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                        }

                        if (TranslationHelper.helper != null)
                            TranslationHelper.helper.update();

                        LanguageFetcher.globalSave();

                        EncyclopediaFetcher.fetcher.removeLanguage(language);

                        try {
                            Encyclopedia encyclopedia = EncyclopediaFetcher.getGlobalEncyclopedia();
                            encyclopedia.removeLanguage(language);
                        } catch (Exception e) {
                        }

                        EncyclopediaFetcher.globalSave();
                    }
                });

        snackbar.show();
    }

    public LanguageSelectorFragment setSelected(Language selected) {
        this.selected = selected;
        if (adapter != null)
            adapter.setSelected(selected);

        return this;
    }

    public LanguageSelectorFragment setListener(OnSelectLanguageListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.language_selector_add:
                callAdd();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void callAdd() {
        updateMenu();

        if (EncyclopediaFetcher.fetcher != null) {
            final Encyclopedia encyclopedia = EncyclopediaFetcher.getGlobalEncyclopedia();
            if (encyclopedia != null && encyclopedia.canAddLanguage()) {
                EditDialog dialog = new EditDialog(getActivity()) {
                    @Override
                    public void onResult(String text) {
                        if (encyclopedia.canAddLanguage()) {
                            Language language = new Language(text);

                            setSelected(language);
                            LanguageFetcher.globalSave();

                            if (adapter != null)
                                adapter.onSelect(language);

                            EncyclopediaFetcher.getGlobalEncyclopedia().addLanguage(language);
                        }
                    }
                };
                dialog.setTitle(getString(R.string.add_language));
                dialog.setHint(getString(R.string.enter_language_name));
                dialog.show();
            }
        }
    }

    public interface OnSelectLanguageListener {
        void onSelect(Language language);
    }
}
