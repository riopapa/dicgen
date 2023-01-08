package com.urrecliner.dicgen;

import android.app.Activity;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileRead {

    private static File packageFolder;

    FileRead(File packageFolder) {
        this.packageFolder = packageFolder;
    }

    public static String[] readBibleFile(String filename, boolean kr) {

        String file2read = packageFolder + "/" + filename;
        String[] lines;
        try {
            lines = readLines(file2read, kr);
            return lines;
        } catch(IOException e) {
//
//            String message = filename+" 이 없거나, 파일읽기가 거부되어 있습니다.";
//            new AlertDialog.Builder(mActivity)
//                    .setMessage(message)
//                    .setPositiveButton("OK", null)
//                    .create().show();
        }
        return null;
    }

    public static String[] readLines(String filename, boolean kr) throws IOException {
        final int BUFFER_SIZE = 81920;
        String code = (kr) ? "EUC-KR": "UTF-8";
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), code),BUFFER_SIZE);

        List<String> lines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();
        return lines.toArray(new String[0]);
    }


    ArrayList<String> readRawTextFile(Context ctx, int resId)
    {
        ArrayList<String> lines = new ArrayList<>();
        InputStream inputStream = ctx.getResources().openRawResource(resId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_16));

        String line = "";
        while (line != null) {
            try {
                line = reader.readLine();
                if (line != null)
                    lines.add(line);
                else
                    break;
            } catch (IOException e) {
                line = null;
            }
        }
        return lines;
    }

}