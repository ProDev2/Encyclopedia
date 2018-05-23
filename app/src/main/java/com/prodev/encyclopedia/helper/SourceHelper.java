package com.prodev.encyclopedia.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prodev.encyclopedia.container.Language;
import com.prodev.encyclopedia.container.Word;
import com.prodev.encyclopedia.container.WordSet;
import com.prodev.encyclopedia.fetcher.EncyclopediaFetcher;
import com.prodev.encyclopedia.fetcher.LanguageFetcher;
import com.prodev.encyclopedia.interfaces.Encyclopedia;
import com.prodev.encyclopedia.tools.Crypter;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SourceHelper {
    private ArrayList<WordSet> allWords;

    public SourceHelper() {}

    public String saveAllSource() {
        return saveAllSource(null);
    }

    public String saveAllSource(String key) {
        fetchAllWords();

        if (allWords != null)
            return saveSource(allWords, key);

        return null;
    }

    public String saveSource(ArrayList<WordSet> setsToSave) {
        return saveSource(setsToSave, null);
    }

    public String saveSource(ArrayList<WordSet> setsToSave, String key) {
        Type type = new TypeToken<ArrayList<WordSet>>() {}.getType();
        String code = new Gson().toJson(setsToSave, type);

        try {
            if (key != null && key.length() > 0)
                code = Crypter.encrypt(code, key);
        } catch (Exception e) {
        }

        return code;
    }

    public boolean loadSource(String code) {
        return loadSource(code, null);
    }

    public boolean loadSource(String code, String key) {
        try {
            if (key != null && key.length() > 0)
                code = Crypter.decrypt(code, key);
        } catch (Exception e) {
            return false;
        }

        try {
            if (code.length() > 0) {
                fetchAllWords();

                Type type = new TypeToken<ArrayList<WordSet>>() {}.getType();
                ArrayList<WordSet> setList = new Gson().fromJson(code, type);

                for (WordSet set : setList) {
                    addWordSet(set);
                }

                LanguageFetcher.globalSave();
                EncyclopediaFetcher.globalSave();

                return true;
            }
        } catch (Exception e) {
        }

        return false;
    }

    private void fetchAllWords() {
        try {
            Encyclopedia encyclopedia = EncyclopediaFetcher.getGlobalEncyclopedia();

            allWords = new ArrayList<>();
            encyclopedia.getAllWords(allWords);
        } catch (Exception e) {
        }
    }

    private void addWordSet(WordSet set) {
        if (allWords != null && canAddWord() && !set.isEmpty()) {
            for (Word word : set)
                word.setLanguage(Language.getByName(word.getLanguage().getName()));
            set.refreshFields();

            try {
                if (!wordSetExists(set)) {
                    Encyclopedia encyclopedia = EncyclopediaFetcher.getGlobalEncyclopedia();

                    WordSet matchableSet = getMatchableSet(set);
                    if (matchableSet != null && !matchableSet.isEmpty()) {
                        WordSet oldSet = matchableSet.copy();
                        matchableSet.add(set);

                        encyclopedia.editWord(oldSet, matchableSet);
                    } else {
                        encyclopedia.addWord(set);

                        allWords.add(set);
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    private boolean wordSetExists(WordSet set) {
        for (WordSet wordSet : allWords) {
            if (wordSet.isEqualTo(set))
                return true;
        }
        return false;
    }

    private WordSet getMatchableSet(WordSet set) {
        for (WordSet item : allWords) {
            if (item.matchesAtLeastOne(set))
                return item;
        }
        return null;
    }

    public boolean canAddWord() {
        try {
            Encyclopedia encyclopedia = EncyclopediaFetcher.getGlobalEncyclopedia();
            return encyclopedia.canAddWord();
        } catch (Exception e) {
        }
        return false;
    }
}
