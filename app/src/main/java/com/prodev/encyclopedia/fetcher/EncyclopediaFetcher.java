package com.prodev.encyclopedia.fetcher;

import android.content.Context;
import android.content.SharedPreferences;

import com.prodev.encyclopedia.Config;
import com.prodev.encyclopedia.container.Language;
import com.prodev.encyclopedia.interfaces.Encyclopedia;

public class EncyclopediaFetcher {
    private static final String storageName = Config.STORAGE_WORDS;

    public static EncyclopediaFetcher fetcher;

    private Context context;
    private SharedPreferences storage;

    private Encyclopedia encyclopedia;

    public EncyclopediaFetcher(Context context, Encyclopedia encyclopedia) {
        this.context = context;
        this.encyclopedia = encyclopedia;

        this.storage = context.getSharedPreferences(storageName, 0);

        load();
    }

    public static void init(Context context, Encyclopedia encyclopedia) {
        if (fetcher == null)
            fetcher = new EncyclopediaFetcher(context, encyclopedia);
    }

    public static void globalLoad() {
        if (fetcher != null)
            fetcher.load();
    }

    public static void globalSave() {
        if (fetcher != null)
            fetcher.save();
    }

    public static Encyclopedia getGlobalEncyclopedia() {
        if (fetcher != null)
            return fetcher.encyclopedia;
        return null;
    }

    public Encyclopedia getEncyclopedia() {
        return encyclopedia;
    }

    public void load() {
        try {
            for (Language language : Language.getAllLanguages()) {
                String id = Integer.toString(language.getId());

                if (storage.contains(id)) {
                    String code = storage.getString(id, "");

                    if (encyclopedia != null)
                        encyclopedia.loadLanguage(language, code);
                }
            }
        } catch (Exception e) {
        }
    }

    public void save() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                postSave();
            }
        }).start();
    }

    private void postSave() {
        try {
            for (Language language : Language.getAllLanguages()) {
                String id = Integer.toString(language.getId());

                if (encyclopedia != null) {
                    String code = encyclopedia.saveLanguage(language);
                    storage.edit().putString(id, code).commit();
                }
            }
        } catch (Exception e) {
        }
    }

    public void removeLanguage(Language language) {
        try {
            String id = Integer.toString(language.getId());

            if (storage.contains(id))
                storage.edit().remove(id).commit();
        } catch (Exception e) {
        }
    }
}
