package com.urrecliner.dicgen;

import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class S2MergeKeywords {

    /*
    *   make json file from keywords array
    */

    List<MainActivity.KeyRef> keyRefs;
    List<MainActivity.bcv> bcvs;

    void merge(List<MainActivity.Keyword> keywords, File jsonFile) {
        Log.w("merge ","merge startesd "+ keywords.size());
        String sKey = "x";
        keyRefs = new ArrayList<>();
        bcvs = new ArrayList<>();
        for (int i = 0; i < keywords.size(); i++) {
            String [] ss = keywords.get(i).str.split(",");
            String nowKey = ss[0].trim();
            int b = Integer.parseInt(ss[1])-100;
            int c = Integer.parseInt(ss[2])-100;
            int v = Integer.parseInt(ss[3])-100;
            if (sKey.equals(nowKey)) {
                bcvs.add(new MainActivity.bcv(b,c,v));
            } else {
                if (!sKey.equals("x")) {
                    keyRefs.add (new MainActivity.KeyRef(sKey, bcvs));
                }
                sKey = nowKey;
                bcvs = new ArrayList<>();
                bcvs.add(new MainActivity.bcv(b,c,v));
            }
        }

        keyRefs.add(new MainActivity.KeyRef(sKey, bcvs));
        Log.w("merge ","merge complete "+ keyRefs.size());
        boolean none = jsonFile.delete();
        Gson gson = new Gson();
        String json = gson.toJson(keyRefs);
        MainActivity.append2File(jsonFile, json);
        Log.w("merge"," merged JSON file complete");
    }

}