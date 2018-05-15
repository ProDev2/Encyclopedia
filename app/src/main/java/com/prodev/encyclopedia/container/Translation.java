package com.prodev.encyclopedia.container;

import java.util.ArrayList;
import java.util.Iterator;

public class Translation implements Iterable<Word> {
    private Word from;
    private Language toLanguage;

    private ArrayList<Word> translations;

    public Translation(Translation src) {
        src.applyTo(this);
    }

    public Translation(Language fromLanguage, String from, Language toLanguage) {
        this(new Word(fromLanguage, from), toLanguage);
    }

    public Translation(Word from, Language toLanguage) {
        this.from = from;
        this.toLanguage = toLanguage;

        this.translations = new ArrayList<>();
    }

    public Word getFrom() {
        return from;
    }

    public void setFrom(Word from) {
        this.from = from;
    }

    public Language getFromLanguage() {
        return from.getLanguage();
    }

    public Language getToLanguage() {
        return toLanguage;
    }

    public void addTranslation(String translation) {
        addTranslation(new Word(toLanguage, translation));
    }

    public void addTranslation(Word word) {
        if (!hasTranslation(word))
            translations.add(word);
    }

    public boolean hasTranslation(Word translation) {
        update();

        for (Word word : translations) {
            if (word.isEqualTo(translation))
                return true;
        }
        return false;
    }

    public boolean isSameTranslationAs(Translation translation) {
        return from.isEqualTo(translation.from) && toLanguage.isEqualTo(translation.toLanguage);
    }

    public boolean isTranslationFor(Word word) {
        return from.isEqualTo(word);
    }

    public boolean isTranslationFor(Language language) {
        return toLanguage.isEqualTo(language);
    }

    public ArrayList<Word> getAllTranslations() {
        update();
        return translations;
    }

    public int getTranslationCount() {
        update();
        return translations.size();
    }

    public boolean isEmpty() {
        for (Word word : translations) {
            if (!word.isEmpty())
                return false;
        }
        return true;
    }

    @Override
    public Iterator<Word> iterator() {
        return translations.iterator();
    }

    public void applyTo(Translation translation) {
        update();

        translation.from = from.copy();
        translation.toLanguage = toLanguage.copy();

        if (translation.translations == null)
            translation.translations = new ArrayList<>();

        for (Word word : translations)
            translation.translations.add(word.copy());
    }

    public Translation copy() {
        return new Translation(this);
    }

    public void update() {
        ArrayList<Word> removeList = new ArrayList<>();
        for (Word translation : translations)
            if (translation.isEmpty())
                removeList.add(translation);

        translations.removeAll(removeList);
    }
}