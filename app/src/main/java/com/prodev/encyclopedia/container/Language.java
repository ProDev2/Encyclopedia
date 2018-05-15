package com.prodev.encyclopedia.container;

import java.util.ArrayList;

public class Language {
    private static ArrayList<Language> languages;

    private int id;
    private String name;

    public Language(Language src) {
        src.applyTo(this);
    }

    public Language(String name) {
        createByName(name);
    }

    public static void initList() {
        if (languages == null)
            languages = new ArrayList<>();
    }

    public static int createId() {
        initList();

        ArrayList<Integer> ids = getAllIds();

        int id = 0;
        while (ids.contains(id)) id++;
        return id;
    }

    public static Language getById(int id) {
        initList();

        for (Language language : languages) {
            if (language.getId() == id)
                return language;
        }
        return null;
    }

    public static Language getByName(String name) {
        initList();

        for (Language language : languages) {
            if (language.getName().equals(name))
                return language;
        }
        return new Language(name);
    }

    public static void remove(Language language) {
        removeById(language.getId());
    }

    public static void removeByName(String name) {
        remove(getByName(name));
    }

    public static void removeById(int id) {
        initList();

        ArrayList<Language> foundItems = new ArrayList<>();
        for (Language item : languages) {
            if (item.getId() == id)
                foundItems.add(item);
        }
        languages.removeAll(foundItems);
    }

    public static boolean languageExists(Language language) {
        return idExists(language.getId());
    }

    public static boolean idExists(int id) {
        initList();

        for (Language language : languages) {
            if (language.getId() == id)
                return true;
        }
        return false;
    }

    public static boolean nameExists(String name) {
        initList();

        for (Language language : languages) {
            if (language.getName().equals(name))
                return true;
        }
        return false;
    }

    public static ArrayList<Integer> getAllIds() {
        initList();

        ArrayList<Integer> ids = new ArrayList<>();
        for (Language language : languages) {
            if (!ids.contains(language.getId()))
                ids.add(language.getId());
        }
        return ids;
    }

    public static ArrayList<Language> getAllLanguages() {
        initList();
        return languages;
    }

    public static int getLanguageCount() {
        initList();
        return languages.size();
    }

    public void createByName(String name) {
        this.name = name;

        initList();

        if (nameExists(name)) {
            this.id = getByName(name).getId();
        } else {
            this.id = createId();

            if (!idExists(id)) languages.add(this);
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isEqualTo(Language language) {
        return id == language.getId();
    }

    public void remove() {
        remove(this);
    }

    public void applyTo(Language language) {
        language.id = id;
        language.name = name;
    }

    public Language copy() {
        return new Language(this);
    }
}