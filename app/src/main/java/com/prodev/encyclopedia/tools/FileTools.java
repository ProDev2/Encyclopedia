package com.prodev.encyclopedia.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;

public class FileTools {
    public static String readFile(File file) {
        StringBuilder textBuilder = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String line = "";
            while((line = bufferedReader.readLine()) != null) {
                if (textBuilder.toString().length() > 0)
                    textBuilder.append("\n");
                textBuilder.append(line);

            }

            bufferedReader.close();
        } catch (Exception e) {
        }

        return textBuilder.toString();
    }

    public static void saveFile(File file, String text) {
        try {
            OutputStream outputStream = new FileOutputStream(file);
            outputStream.write(text.getBytes());
            outputStream.close();
        } catch (Exception e) {
        }
    }
}