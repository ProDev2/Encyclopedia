package com.prodev.encyclopedia.helper;

import com.prodev.encyclopedia.container.Language;
import com.prodev.encyclopedia.fetcher.LanguageFetcher;

public class TranslationHelper {
    public static TranslationHelper helper;

    private Language from, to;

    public TranslationHelper() {
    }

    public static void init() {
        if (helper == null)
            helper = new TranslationHelper();
    }

    public Language getFrom() {
        return from;
    }

    public void setFrom(Language from) {
        this.from = from;
        LanguageFetcher.globalSave();
    }

    public Language getTo() {
        return to;
    }

    public void setTo(Language to) {
        this.to = to;
        LanguageFetcher.globalSave();
    }

    public void switchLanguages() {
        if (from == null || to == null) return;

        Language fromN = new Language(to.getName());
        Language toN = new Language(from.getName());

        this.from = fromN;
        this.to = toN;

        LanguageFetcher.globalSave();
    }

    public void update() {
        if (from != null && !Language.idExists(from.getId()))
            from = null;

        if (to != null && !Language.idExists(to.getId()))
            to = null;

        LanguageFetcher.globalSave();
    }

    public void applyTo(TranslationHelper helper) {
        helper.from = from;
        helper.to = to;
    }
}
