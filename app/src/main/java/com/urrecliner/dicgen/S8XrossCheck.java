package com.urrecliner.dicgen;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class S8XrossCheck {

    /*
    *   keyRefs와 dict folder file 명을 비교하여 빠진 것에 대해 log 추출
     */
    List<MainActivity.KeyRef> keyRefs;
    List<String> xrossKey;
    List<Integer> xrossCnt;
    File[] dicList;
    String[] dictNames;

    void check(Context context, File dicFolder, File jsonFile) {

        Log.w("xross","Xross start");
        readKeyRef(jsonFile);
        xrossKey = new ArrayList<>();
        xrossCnt = new ArrayList<>();
        for (int i = 0; i < keyRefs.size(); i++) {
            xrossKey.add(keyRefs.get(i).key);
            xrossCnt.add(0);
        }

        dicList = dicFolder.listFiles((dir, name) ->
                (name.endsWith("txt")));

        Log.w("xross", "Dic files "+dicList.length);
        dictNames = new String[dicList.length];
        for (int i = 0; i < dicList.length; i++) {
            String dic = dicList[i].getName();
            dictNames[i] = dic.substring(0, dic.length()-4);
        }
        Arrays.sort(dictNames);

        for (int i = 0; i < dictNames.length; i++) {
            String s = dictNames[i];
            int idx = Collections.binarySearch(xrossKey, s);
            if (idx < 0) {
                Log.w("xross", "not reffered dic.txt " + s);
            }
            else
                xrossCnt.set(idx,xrossCnt.get(idx)+1);
        }
        Log.w("xross","");
        Log.w("xross"," Check merged");
        for (int i = 0; i < xrossKey.size(); i++) {
            if (xrossCnt.get(i) == 0) {
                Log.w("xross","error "+xrossKey.get(i));
            }
        }
    }

    private void readKeyRef(File jsonFile) {
        final String EUC_KR = "UTF-8";
        final int BUFFER_SIZE = 81920;

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile), EUC_KR),BUFFER_SIZE);
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        }

        String json = "";
        try {
            json = bufferedReader.readLine();
            bufferedReader.close();
        } catch (IOException e) {   // no json file
            e.printStackTrace();
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<MainActivity.KeyRef>>() {
        }.getType();
        keyRefs =  gson.fromJson(json, type);
        Log.w("json","Read "+keyRefs.size() );
    }

}
//        dicList = dicFolder.listFiles((dir, name) ->
//                (name.endsWith("txt")));