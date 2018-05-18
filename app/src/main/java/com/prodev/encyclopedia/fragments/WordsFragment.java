package com.prodev.encyclopedia.fragments;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.prodev.encyclopedia.MainActivity;
import com.prodev.encyclopedia.R;
import com.prodev.encyclopedia.adapter.WordSetAdapter;
import com.prodev.encyclopedia.container.Language;
import com.prodev.encyclopedia.container.Word;
import com.prodev.encyclopedia.container.WordSet;
import com.prodev.encyclopedia.dialogs.custom.WordAddDialog;
import com.prodev.encyclopedia.fetcher.EncyclopediaFetcher;
import com.prodev.encyclopedia.interfaces.Encyclopedia;
import com.simplelib.SimpleFragment;
import com.simplelib.container.SimpleFilter;

import java.util.ArrayList;

public class WordsFragment extends SimpleFragment {
    private SearchView searchView;

    private ViewGroup contentLayout;

    private String searchFor;
    private SimpleFilter<WordSet> filter;

    private RecyclerView list;
    private LinearLayoutManager manager;
    private WordSetAdapter adapter;

    private FloatingActionButton addButton;

    public WordsFragment() {
        super(R.layout.words_fragment);
    }

    @Override
    public void create(View view) {
        enableOptionsMenu(R.menu.words_menu);

        contentLayout = (ViewGroup) findViewById(R.id.words_fragment_content_layout);

        ArrayList<WordSet> sets = new ArrayList<>();
        try {
            Encyclopedia encyclopedia = EncyclopediaFetcher.getGlobalEncyclopedia();
            encyclopedia.getAllWords(sets);
        } catch (Exception e) {
        }

        createFilter();

        list = (RecyclerView) findViewById(R.id.words_fragment_list);

        manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(manager);

        list.addItemDecoration(new DividerItemDecoration(getActivity(), manager.getOrientation()));

        adapter = new WordSetAdapter(sets) {
            @Override
            public void onEdit(WordSet fromSet, WordSet toSet) {
                try {
                    Encyclopedia encyclopedia = EncyclopediaFetcher.getGlobalEncyclopedia();
                    encyclopedia.editWord(fromSet, toSet);
                } catch (Exception e) {
                }

                EncyclopediaFetcher.globalSave();
            }

            @Override
            public void onDelete(WordSet set) {
                openDeleteBar(set);
            }
        };
        adapter.setFilter(filter);
        list.setAdapter(adapter);

        addButton = (FloatingActionButton) findViewById(R.id.words_fragment_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();

                if (canAddWord()) openAddDialog();
            }
        });

        update();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.words_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String text) {
                searchFor = text;
                if (adapter != null) adapter.updateFilter();

                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String text) {
                searchFor = text;
                if (adapter != null) adapter.updateFilter();

                return true;
            }
        });
    }

    private void createFilter() {
        filter = new SimpleFilter<WordSet>() {
            @Override
            public boolean filter(WordSet set) {
                if (searchFor != null && searchFor.length() > 0) {
                    for (Word word : set)
                        if (word.getText().toLowerCase().contains(searchFor.toLowerCase()))
                            return true;
                    return false;
                } else
                    return true;
            }
        };
    }

    private boolean canAddWord() {
        try {
            Encyclopedia encyclopedia = EncyclopediaFetcher.getGlobalEncyclopedia();
            return encyclopedia.canAddWord();
        } catch (Exception e) {
        }
        return false;
    }

    private void update() {
        try {
            MenuItem addItem = getMenu().findItem(R.id.words_add);

            if (canAddWord()) {
                addItem.setVisible(true);
                addButton.setVisibility(View.VISIBLE);
            } else {
                addItem.setVisible(false);
                addButton.setVisibility(View.GONE);
            }
        } catch (Exception e) {
        }
    }

    private void openAddDialog() {
        if (Language.getLanguageCount() <= 0) {
            Toast.makeText(getActivity(), getString(R.string.add_language_first), Toast.LENGTH_SHORT).show();
            return;
        }

        WordAddDialog addDialog = new WordAddDialog(getActivity()) {
            @Override
            public void onAdd(WordSet set) {
                if (canAddWord() && !set.isEmpty()) {
                    try {
                        Encyclopedia encyclopedia = EncyclopediaFetcher.getGlobalEncyclopedia();

                        ArrayList<WordSet> allWords = new ArrayList<>();
                        encyclopedia.getAllWords(allWords);
                        if (!wordSetExists(allWords, set)) {
                            adapter.add(set);
                            encyclopedia.addWord(set);

                            if (adapter.getListSize() > 0)
                                adapter.smoothScrollToPosition(adapter.getListSize() - 1);
                        }
                    } catch (Exception e) {
                    }

                    EncyclopediaFetcher.globalSave();
                }
            }
        };
        addDialog.show();
    }

    private void openDeleteBar(final WordSet set) {
        Snackbar snackbar = Snackbar
                .make(contentLayout, getString(R.string.remove_word), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.remove), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adapter.remove(set);

                        try {
                            Encyclopedia encyclopedia = EncyclopediaFetcher.getGlobalEncyclopedia();
                            encyclopedia.removeWord(set);
                        } catch (Exception e) {
                        }

                        EncyclopediaFetcher.globalSave();
                    }
                });
        snackbar.show();
    }

    private boolean wordSetExists(ArrayList<WordSet> setList, WordSet set) {
        for (WordSet wordSet : setList) {
            if (wordSet.isEqualTo(set))
                return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            MainActivity.switchToMenuItem(R.id.nav_words, false);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.words_add:
                update();

                if (canAddWord()) openAddDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean back() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return false;
        }

        if (searchFor != null && searchFor.length() > 0) {
            searchFor = null;
            return false;
        }

        return super.back();
    }
}
