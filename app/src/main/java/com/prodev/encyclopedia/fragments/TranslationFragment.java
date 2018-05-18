package com.prodev.encyclopedia.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.prodev.encyclopedia.Config;
import com.prodev.encyclopedia.MainActivity;
import com.prodev.encyclopedia.R;
import com.prodev.encyclopedia.adapter.TranslationAdapter;
import com.prodev.encyclopedia.adapter.TranslationViewerAdapter;
import com.prodev.encyclopedia.container.Language;
import com.prodev.encyclopedia.container.Translation;
import com.prodev.encyclopedia.container.Word;
import com.prodev.encyclopedia.helper.SearchHelper;
import com.prodev.encyclopedia.helper.TranslationHelper;
import com.prodev.encyclopedia.tools.CopyTools;
import com.simplelib.SimpleFragment;
import com.simplelib.math.Vector2;

public class TranslationFragment extends SimpleFragment {
    private TranslationHelper translationHelper;

    private Translation lastTranslation;

    private ConstraintLayout switchLayout;

    private ImageButton switchButton;

    private Button fromButton;
    private Button toButton;

    private ImageButton closeButton;
    private Button inputHeaderButton;
    private EditText inputView;

    private SearchHelper searchHelper;

    private CardView translationLayout;

    private Button translationHeaderButton;
    private ImageButton translationCopyButton;

    private RecyclerView translationView;
    private LinearLayoutManager translationManager;
    private TranslationViewerAdapter translationAdapter;

    private RecyclerView resultView;
    private LinearLayoutManager resultManager;
    private TranslationAdapter resultAdapter;

    public TranslationFragment() {
        super(R.layout.translation_fragment);

        if (TranslationHelper.helper != null)
            this.translationHelper = TranslationHelper.helper;
        else
            this.translationHelper = new TranslationHelper();
    }

    @Override
    public void create(View view) {
        enableOptionsMenu(R.menu.translation_menu);

        switchLayout = (ConstraintLayout) findViewById(R.id.translation_fragment_switch_layout);

        switchButton = (ImageButton) findViewById(R.id.translation_fragment_switch);

        fromButton = (Button) findViewById(R.id.translation_fragment_from);
        toButton = (Button) findViewById(R.id.translation_fragment_to);

        closeButton = (ImageButton) findViewById(R.id.translation_fragment_close);
        inputHeaderButton = (Button) findViewById(R.id.translation_fragment_input_header);
        inputView = (EditText) findViewById(R.id.translation_fragment_input);

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchLanguages();
                hideTranslation();
            }
        });

        fromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchLanguage(translationHelper.getFrom(), new LanguageSelectorFragment.OnSelectLanguageListener() {
                    @Override
                    public void onSelect(Language language) {
                        translationHelper.setFrom(language);
                        update();

                        hideTranslation();
                    }
                });
            }
        });

        toButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchLanguage(translationHelper.getTo(), new LanguageSelectorFragment.OnSelectLanguageListener() {
                    @Override
                    public void onSelect(Language language) {
                        translationHelper.setTo(language);
                        update();

                        hideTranslation();
                    }
                });
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputView.setText("");

                searchFor(null);
                hideTranslation();
            }
        });

        inputHeaderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        inputView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hideTranslation();
                searchFor(s.toString());
            }
        });

        //Translation views
        translationLayout = (CardView) findViewById(R.id.translation_fragment_translation_layout);

        translationHeaderButton = (Button) findViewById(R.id.translation_fragment_translation_header);
        translationCopyButton = (ImageButton) findViewById(R.id.source_item_import);

        translationCopyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastTranslation != null) {
                    StringBuilder builder = new StringBuilder();
                    for (Word word : lastTranslation) {
                        if (builder.toString().length() > 0)
                            builder.append(",\n");
                        builder.append(word.getText());
                    }

                    CopyTools.setClipboard(getActivity(), builder.toString());

                    Toast.makeText(getActivity(), getString(R.string.copied), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Translation recycler view
        translationView = (RecyclerView) findViewById(R.id.translation_fragment_translation_list);

        translationManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        translationView.setLayoutManager(translationManager);

        translationAdapter = new TranslationViewerAdapter();
        translationView.setAdapter(translationAdapter);

        //Result recycler view
        resultView = (RecyclerView) findViewById(R.id.translation_fragment_list);

        resultManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        resultView.setLayoutManager(resultManager);

        resultView.addItemDecoration(new DividerItemDecoration(getActivity(), resultManager.getOrientation()));

        resultAdapter = new TranslationAdapter() {
            @Override
            public void onClickTranslation(Translation translation) {
                hideKeyboard();
                selectTranslation(translation);
            }
        };
        resultView.setAdapter(resultAdapter);

        hideTranslation();
        update();
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            MainActivity.switchToMenuItem(R.id.nav_translate, false);
        } catch (Exception e) {
        }

        try {
            if (translationHelper != null)
                translationHelper.update();

            update();
        } catch (Exception e) {
        }

        try {
            if (searchHelper != null) searchHelper.cancel();
            searchHelper = null;
        } catch (Exception e) {
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        try {
            hideTranslation();
            closeButton.performClick();

            if (searchHelper != null) searchHelper.cancel();
            searchHelper = null;
        } catch (Exception e) {
        }
    }

    private void update() {
        if (translationHelper.getFrom() != null)
            fromButton.setText(translationHelper.getFrom().getName());

        if (translationHelper.getTo() != null)
            toButton.setText(translationHelper.getTo().getName());

        if (translationHelper.getFrom() != null)
            inputHeaderButton.setText("▶ " + translationHelper.getFrom().getName());

        if (translationHelper.getTo() != null) {
            translationHeaderButton.setText("▶ " + translationHelper.getTo().getName());
            translationHeaderButton.setVisibility(View.VISIBLE);
        } else {
            translationHeaderButton.setVisibility(View.GONE);
        }

        searchFor(inputView.getText().toString());
    }

    private void switchLanguages() {
        switchButton.setEnabled(false);

        fromButton.setEnabled(false);
        toButton.setEnabled(false);

        final Vector2 fromPos = new Vector2(fromButton.getX(), fromButton.getY());
        final Vector2 toPos = new Vector2(toButton.getX(), toButton.getY());

        final float movement = switchLayout.getWidth() * Config.SWITCH_MOVEMENT;

        ValueAnimator vaMove = ValueAnimator.ofFloat(0f, 1f);
        vaMove.setDuration(200);
        vaMove.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = animation.getAnimatedFraction();

                fromButton.setAlpha(1 - value);
                toButton.setAlpha(1 - value);

                fromButton.setX(fromPos.getX() + (movement * value));
                toButton.setX(toPos.getX() - (movement * value));

                switchButton.setRotation(90 * value);
            }
        });
        vaMove.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                translationHelper.switchLanguages();
                update();
            }
        });

        ValueAnimator vaBack = ValueAnimator.ofFloat(0f, 1f);
        vaBack.setDuration(200);
        vaBack.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = animation.getAnimatedFraction();

                fromButton.setAlpha(value);
                toButton.setAlpha(value);

                fromButton.setX(fromPos.getX() + (movement * (1 - value)));
                toButton.setX(toPos.getX() - (movement * (1 - value)));

                switchButton.setRotation(90 + (90 * value));
            }
        });
        vaBack.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                switchButton.setEnabled(true);

                fromButton.setEnabled(true);
                toButton.setEnabled(true);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(vaMove, vaBack);
        animatorSet.start();
    }

    private void switchLanguage(Language selected, LanguageSelectorFragment.OnSelectLanguageListener languageListener) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        ft.replace(R.id.main_frame_layout, new LanguageSelectorFragment().setSelected(selected).setListener(languageListener), "lang_selector");
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.translation_words:
                startWords();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startWords() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        ft.replace(R.id.main_frame_layout, new WordsFragment(), "words");
        ft.addToBackStack(null);
        ft.commit();
    }

    private void searchFor(String text) {
        if (searchHelper == null)
            searchHelper = new SearchHelper(getActivity(), translationHelper, resultAdapter);

        searchHelper.searchFor(text);
    }

    private void hideTranslation() {
        lastTranslation = null;

        translationLayout.setVisibility(View.GONE);
    }

    private void selectTranslation(Translation translation) {
        lastTranslation = translation;

        translationLayout.setVisibility(View.VISIBLE);

        translationAdapter.getList().clear();
        translationAdapter.getList().addAll(translation.getAllTranslations());

        translationAdapter.notifyDataSetChanged();
    }
}
