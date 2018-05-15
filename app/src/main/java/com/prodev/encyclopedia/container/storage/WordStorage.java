package com.prodev.encyclopedia.container.storage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prodev.encyclopedia.container.Language;
import com.prodev.encyclopedia.container.TranslationSet;
import com.prodev.encyclopedia.container.Word;
import com.prodev.encyclopedia.container.WordSet;
import com.prodev.encyclopedia.interfaces.Encyclopedia;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class WordStorage implements Encyclopedia {
    private ArrayList<WordSet> sets;

    public WordStorage() {
        this.sets = new ArrayList<>();
    }

    @Override
    public void translate(TranslationSet translationSet) {
        for (WordSet wordSet : sets) {
            if (wordSet.contains(translationSet.getFromLanguage()) && wordSet.contains(translationSet.getToLanguage())) {
                Word foundWord = wordSet.get(translationSet.getFromLanguage());

                if (addFoundWord(translationSet, foundWord))
                    translationSet.add(foundWord, wordSet.get(translationSet.getToLanguage()));
            }
        }
    }

    private boolean addFoundWord(Word word, Word foundWord) {
        String wordText = word.getText().toLowerCase();
        String foundWordText = foundWord.getText().toLowerCase();

        return foundWordText.contains(wordText) || wordText.contains(foundWordText);
    }

    @Override
    public void addWord(WordSet set) {
        sets.add(set);
    }

    @Override
    public void editWord(WordSet fromSet, WordSet toSet) {
        //Nothing to do here
    }

    @Override
    public void removeWord(WordSet set) {
        sets.remove(set);
    }

    @Override
    public void getAllWords(ArrayList<WordSet> sets) {
        sets.addAll(this.sets);
    }

    @Override
    public boolean canAddWord() {
        return true;
    }

    @Override
    public void addLanguage(Language language) {
        for (WordSet set : sets)
            set.refreshFields();
    }

    @Override
    public void removeLanguage(Language language) {
        ArrayList<WordSet> removeList = new ArrayList<>();
        for (WordSet set : sets) {
            set.refreshFields();

            if (set.isEmpty())
                removeList.add(set);
        }
        sets.removeAll(removeList);
    }

    @Override
    public boolean canAddLanguage() {
        return true;
    }

    @Override
    public void loadLanguage(Language language, String code) {
        if (code != null && code.length() > 0) {
            try {
                Type type = new TypeToken<ArrayList<String>>() {}.getType();
                ArrayList<String> itemList = new Gson().fromJson(code, type);

                for (int count = 0; count < itemList.size(); count++) {
                    if (count < sets.size()) {
                        WordSet set = sets.get(count);
                        if (set != null)
                            set.put(language, itemList.get(count));
                        else {
                            set = new WordSet();
                            set.put(language, itemList.get(count));
                            sets.set(count, set);
                        }
                    } else if (count == sets.size()) {
                        WordSet set = new WordSet();
                        set.put(language, itemList.get(count));
                        sets.add(set);
                    } else {
                        WordSet set = new WordSet();
                        set.put(language, itemList.get(count));
                        sets.add(count, set);
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    public String saveLanguage(Language language) {
        ArrayList<String> itemList = new ArrayList<>();
        for (WordSet set : sets) {
            Word word = set.get(language);
            if (word != null && !word.isEmpty())
                itemList.add(word.getText());
            else
                itemList.add("");
        }

        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        String code = new Gson().toJson(itemList, type);
        return code;
    }
}
