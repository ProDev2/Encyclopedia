package com.prodev.encyclopedia.interfaces;

import com.prodev.encyclopedia.container.Language;
import com.prodev.encyclopedia.container.TranslationSet;
import com.prodev.encyclopedia.container.WordSet;

import java.util.ArrayList;

public interface Encyclopedia {
    void translate(TranslationSet translationSet);

    void addWord(WordSet set);

    void editWord(WordSet fromSet, WordSet toSet);

    void removeWord(WordSet set);

    void getAllWords(ArrayList<WordSet> sets);

    boolean canAddWord();

    void addLanguage(Language language);

    void removeLanguage(Language language);

    boolean canAddLanguage();

    void loadLanguage(Language language, String code);

    String saveLanguage(Language language);
}
