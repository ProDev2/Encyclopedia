package com.prodev.encyclopedia.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class TranslationSet extends Word implements Iterable<Translation> {
    private Language toLanguage;
    private ArrayList<Translation> translations;

    public TranslationSet(Word word, Language toLanguage) {
        super(word);

        this.toLanguage = toLanguage;

        init();
    }

    public TranslationSet(Language fromLanguage, String word, Language toLanguage) {
        super(fromLanguage, word);

        this.toLanguage = toLanguage;

        init();
    }

    private void init() {
        translations = new ArrayList<>();
    }

    public Language getFromLanguage() {
        return getLanguage();
    }

    public Language getToLanguage() {
        return toLanguage;
    }

    public String getFromText() {
        return getText();
    }

    public void add(String word, String... translations) {
        add(word, toLanguage, translations);
    }

    public void add(String word, Language language, String... translations) {
        add(word, language, new ArrayList<>(Arrays.asList(translations)));
    }

    public void add(String word, Language language, ArrayList<String> translations) {
        Translation translationItem = new Translation(new Word(getLanguage(), word), language);
        for (String translation : translations) {
            translationItem.addTranslation(new Word(language, translation));
        }
        add(translationItem);
    }

    public void add(Word word, Word... translations) {
        add(word, toLanguage, new ArrayList<Word>(Arrays.asList(translations)));
    }

    public void add(Word word, Language language, ArrayList<Word> translations) {
        Translation translationItem = new Translation(word, language);
        for (Word item : translations) {
            translationItem.addTranslation(item);
        }
        add(translationItem);
    }

    public void add(Translation translation) {
        if (translation != null && !translation.isEmpty()) {
            Translation foundTranslation = getTranslationBy(translation.getFrom());
            if (foundTranslation != null && foundTranslation.isSameTranslationAs(translation)) {
                for (Word word : translation) {
                    foundTranslation.addTranslation(word);
                }
            } else translations.add(translation);
        }
    }

    public Translation getTranslationBy(Word fromWord) {
        for (Translation translation : translations) {
            if (translation.isTranslationFor(fromWord))
                return translation;
        }
        return null;
    }

    public boolean hasTranslationFor(Word word) {
        return getTranslationBy(word) != null;
    }

    public ArrayList<Translation> getAll() {
        return translations;
    }

    @Override
    public Iterator<Translation> iterator() {
        return translations.iterator();
    }
}