package com.urrecliner.dicgen;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class S2MergeKeywords {

    /*
    *   dic2_sorted 를 읽어 같은 단어 별로 bible, chapter, verse array file 을 만듬
    *   keyRef.json 에도 기록
     */
    ;
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
            if (sKey.equals(nowKey)) {
                bcvs.add(new MainActivity.bcv(Integer.parseInt(ss[1]),
                        Integer.parseInt(ss[2]), Integer.parseInt(ss[3])));
            } else {
                if (!sKey.equals("x")) {
                    keyRefs.add (new MainActivity.KeyRef(sKey, bcvs));
                }
                sKey = nowKey;
                bcvs = new ArrayList<>();
                bcvs.add(new MainActivity.bcv(Integer.parseInt(ss[1]),
                        Integer.parseInt(ss[2]), Integer.parseInt(ss[3])));
            }
        }

        keyRefs.add(new MainActivity.KeyRef(sKey, bcvs));
        Log.w("merge ","merge complete "+ keyRefs.size());

//        for (int j = 0; j < keyRefs.size(); j++) {
//            MainActivity.KeyRef mg = keyRefs.get(j);
//            String s = mg.key + ";";
//            for (int k = 0; k < mg.bcvs.size(); k++) {
//                MainActivity.bcv bv = mg.bcvs.get(k);
//                s += bv.b +","+bv.c +","+bv.v +";";
//            }
//            MainActivity.append2File(mergedFile, s);
//        }
//        Log.w("merge"," mergedFile complete");
        boolean none = jsonFile.delete();
        Gson gson = new Gson();
        String json = gson.toJson(keyRefs);
        MainActivity.append2File(jsonFile, json);
        Log.w("merge"," merged JSON file complete");
    }

}