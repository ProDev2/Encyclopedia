package com.prodev.encyclopedia.fetcher;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prodev.encyclopedia.Config;
import com.prodev.encyclopedia.container.Language;
import com.prodev.encyclopedia.helper.TranslationHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class LanguageFetcher {
    private static final String storageName = Config.STORAGE;
    private static final String storageItem = Config.STORAGE_LANGUAGES;

    private static final String storageItemHelper = Config.STORAGE_HELPER;

    public static LanguageFetcher fetcher;

    private Context context;
    private SharedPreferences storage;

    public LanguageFetcher(Context context) {
        this.context = context;

        this.storage = context.getSharedPreferences(storageName, 0);

        load();
    }

    public static void init(Context context) {
        if (fetcher == null)
            fetcher = new LanguageFetcher(context);

        TranslationHelper.init();
        globalLoad();
    }

    public static void globalLoad() {
        if (fetcher != null)
            fetcher.load();
    }

    public static void globalSave() {
        if (fetcher != null)
            fetcher.save();
    }

    public void load() {
        try {
            if (storage.contains(storageItem)) {
                String code = storage.getString(storageItem, "");

                Type type = new TypeToken<ArrayList<Language>>() {}.getType();
                ArrayList<Language> newList = new Gson().fromJson(code, type);

                if (newList.size() > 0) {
                    Language.getAllLanguages().clear();
                    for (Language language : newList)
                        Language.getAllLanguages().add(language);
                }
            }
        } catch (Exception e) {
        }

        try {
            if (TranslationHelper.helper != null && storage.contains(storageItemHelper)) {
                String code = storage.getString(storageItemHelper, "");

                Type type = new TypeToken<TranslationHelper>() {}.getType();
                TranslationHelper helper = new Gson().fromJson(code, type);

                helper.applyTo(TranslationHelper.helper);
            }
        } catch (Exception e) {
        }
    }

    public void save() {
        try {
            Type type = new TypeToken<ArrayList<Language>>() {}.getType();
            String code = new Gson().toJson(Language.getAllLanguages(), type);

            storage.edit().putString(storageItem, code).commit();
        } catch (Exception e) {
        }

        try {
            if (TranslationHelper.helper != null) {
                Type type = new TypeToken<TranslationHelper>() {}.getType();
                String code = new Gson().toJson(TranslationHelper.helper, type);

                storage.edit().putString(storageItemHelper, code).commit();
            }
        } catch (Exception e) {
        }
    }
}
