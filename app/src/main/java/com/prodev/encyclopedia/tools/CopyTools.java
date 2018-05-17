package com.prodev.encyclopedia.tools;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class CopyTools {
    public static void setClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", text);
        clipboard.setPrimaryClip(clip);
    }
}
