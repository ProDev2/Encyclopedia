package com.prodev.encyclopedia.container;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;

public class WordSet implements Iterable<Word> {
    private ArrayList<Word> list;

    public WordSet(WordSet src) {
        src.applyTo(this);
    }

    public WordSet() {
        this(true);
    }

    public WordSet(boolean createLanguages) {
        this.list = new ArrayList<>();

        if (createLanguages) refreshFields();
    }

    public void refreshFields() {
        try {
            for (Language language : Language.getAllLanguages())
                if (!contains(language))
                    put(new Word(language));

            ArrayList<Language> languages = getAllLanguages();
            languages.removeAll(Language.getAllLanguages());

            for (Language language : languages)
                remove(language);
        } catch (Exception e) {
        }
    }

    public void put(int languageId, String word) {
        put(Language.getById(languageId), word);
    }

    public void put(Language language, String word) {
        put(new Word(language, word));
    }

    public void put(Word word) {
        if (!Language.languageExists(word.getLanguage())) return;

        if (!contains(word))
            list.add(word);
        else {
            Word oldWord = get(word.getLanguage());
            if (oldWord != null) {
                int index = list.indexOf(oldWord);
                if (index >= 0)
                    list.set(index, word);
            }
        }
    }

    public Word get(int languageId) {
        return get(Language.getById(languageId));
    }

    public Word get(Language language) {
        if (!Language.languageExists(language)) return null;

        for (Word item : list) {
            if (item.getLanguage().isEqualTo(language))
                return item;
        }
        Word word = new Word(language);
        put(word);
        return word;
    }

    public void remove(Language language) {
        Word word = null;
        for (Word item : list) {
            if (item.getLanguage().isEqualTo(language))
                word = item;
        }

        if (word != null)
            list.remove(word);
    }

    public void clear() {
        list.clear();
    }

    public boolean contains(Word word) {
        return contains(word.getLanguage());
    }

    public boolean contains(Language language) {
        for (Word item : list) {
            if (item.getLanguage().isEqualTo(language))
                return true;
        }
        return false;
    }

    public boolean isEmpty() {
        boolean empty = true;
        for (Word word : list) {
            if (!word.isEmpty())
                empty = false;
        }
        return empty;
    }

    public boolean isEqualTo(WordSet set) {
        if (list.size() != set.list.size()) return false;
        for (int count = 0; count < list.size(); count++) {
            try {
                if (!list.get(count).isEqualTo(set.get(count))) return false;
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<Word> getAllWords() {
        return list;
    }

    public ArrayList<Language> getAllLanguages() {
        ArrayList<Language> languages = new ArrayList<>();
        for (Word word : list) {
            boolean exists = false;
            try {
                for (Language language : languages)
                    if (word.getLanguage().isEqualTo(language))
                        exists = true;
            } catch (Exception e) {
            }

            if (!exists) languages.add(word.getLanguage());
        }
        return languages;
    }

    @NonNull
    @Override
    public Iterator<Word> iterator() {
        return list.iterator();
    }

    public void applyTo(WordSet set) {
        if (set.list == null)
            set.list = new ArrayList<>();

        for (Word word : list)
            set.list.add(word.copy());
    }

    public WordSet copy() {
        return new WordSet(this);
    }
}
