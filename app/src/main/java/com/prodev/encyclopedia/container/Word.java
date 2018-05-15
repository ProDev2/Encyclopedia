package com.prodev.encyclopedia.container;

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