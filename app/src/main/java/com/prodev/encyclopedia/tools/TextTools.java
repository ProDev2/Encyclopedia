package com.prodev.encyclopedia.tools;

public class TextTools {
    public static String parseText(String text, String index, String replaceIndex) {
        while (text.contains(index + " "))
            text = text.replace(index + " ", index);
        while (text.contains(" " + index))
            text = text.replace(" " + index, index);
        while (text.contains(index + index))
            text = text.replace(index + index, index);
        if (text.startsWith(index))
            text = text.substring(index.length());
        if (text.endsWith(index))
            text = text.substring(0, text.length() - index.length());
        text = text.replace(index, replaceIndex);
        return text;
    }
}
