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

    /*
    * sort extracted array into keywords
     */
    List<MainActivity.Keyword> keywords;

    List<MainActivity.Keyword>  sort(List<String> extracted) {
        Log.w("sort ","sort started "+ extracted.size());
        keywords = new ArrayList<>();
        for (int i = 0; i < extracted.size(); i++) {
            String [] s = extracted.get(i).split(",");
            keywords.add(new MainActivity.Keyword(s[0]+" ,"+s[1]+","+s[2]+","+s[3]));
        }
        keywords.sort(Comparator.comparing(obj -> (obj.str)));
//        Log.w("sort","keywords sorted "+keywords.size());
//        for (int j = 0; j < keywords.size(); j++) {
//            MainActivity.append2File(sortedFile, keywords.get(j).str);
//        }
        Log.w("sort ","sorted write complete "+keywords.size());
        return keywords;
    }

}