package com.prodev.encyclopedia.container;

import java.util.ArrayList;
import java.util.Arrays;

public class Word {
    private Language language;
    private String text;

    public Word(Word src) {
        src.applyTo(this);
    }

    public Word(String languageName) {
        this(new Language(languageName));
    }

    public Word(String languageName, String text) {
        this(new Language(languageName), text);
    }

    public Word(Language language) {
        this(language, null);
    }

    public Word(Language language, String text) {
        this.language = language;
        this.text = text;
    }

    public void setLanguage(String languageName) {
        setLanguage(new Language(languageName));
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addWord(Word word) {
        addWord(word.getText());
    }

    public void addWord(String word) {
        if (word != null && word.length() > 0) {
            if (text == null || (text != null && text.length() <= 0))
                text = word;
            else if (!hasWord(word))
                text += "\n" + word;
        }
    }

    public boolean hasWord(Word word) {
        return hasWord(word.getText());
    }

    public boolean hasWord(String word) {
        for (String item : getWords()) {
            if (item.equals(word))
                return true;
        }
        return false;
    }

    public ArrayList<String> getWords() {
        ArrayList<String> words = new ArrayList<>();
        if (text != null && text.length() > 0) {
            if (text.contains("\n"))
                words.addAll(Arrays.asList(text.split("\n")));
            else
                words.add(text);
        }
        return words;
    }

    public boolean matchesAtLeastOne(Word word) {
        if (word != null && !word.isEmpty()) {
            for (String item : getWords()) {
                if (word.hasWord(item))
                    return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return text == null || (text != null && text.length() <= 0);
    }

    public boolean isEqualTo(Word word) {
        boolean equal = true;
        if (isEmpty() && !word.isEmpty()) equal = false;
        if (!isEmpty() && word.isEmpty()) equal = false;
        if (!isEmpty() && !word.isEmpty() && !getText().equals(word.getText())) equal = false;
        if (!getLanguage().isEqualTo(word.getLanguage())) equal = false;
        return equal;
    }

    public void applyTo(Word word) {
        word.language = language.copy();
        word.text = text;
    }

    public Word copy() {
        return new Word(this);
    }
}