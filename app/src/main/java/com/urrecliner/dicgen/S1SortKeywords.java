package com.urrecliner.dicgen;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class S1SortKeywords {

    List<MainActivity.Keyword> keywords;

    // dic_index.txt file을 dic1_index.txt 로 copy 후 실행
    // dic1_index를 sort 해서 dic2_sorted 로
    public void sort(Context context, File sortedFile) {
        boolean none = sortedFile.delete();
        keywords = new ArrayList<>();
        int i = 0;
        Log.w("sort","sort start");
        InputStream inputStream = context.getResources().openRawResource(R.raw.dic1_extracted);        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (line != null && line.length() > 5) {
            i++;
            String [] s = line.split(",");  // add blank after keyword for sort properly
            keywords.add(new MainActivity.Keyword(s[0]+" ,"+s[1]+","+s[2]+","+s[3]));
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.w("sort","read complete");
        keywords.sort(Comparator.comparing(obj -> (obj.str)));
        Log.w("sort","keywords sorted");
        for (int j = 0; j < keywords.size(); j++) {
            MainActivity.append2File(sortedFile, keywords.get(j).str);
        }
        keywords.clear();
        Log.w("all ","sorted write complete "+keywords.size());
    }

}