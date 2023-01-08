package com.urrecliner.dicgen;

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
    *   verify dict folder against keyRefs json
     */
    List<MainActivity.KeyRef> keyRefs;
    List<String> refKey;
    List<Integer> refCnt;
    File[] dicList;
    String[] dictNames;

    void check(File dicFolder, File jsonFile) {

        FileRead fileRead = new FileRead(dicFolder);
        Log.w("xross","Xross start");
        readKeyRef(jsonFile);
        refKey = new ArrayList<>();
        refCnt = new ArrayList<>();
        for (int i = 0; i < keyRefs.size(); i++) {
            refKey.add(keyRefs.get(i).key);
            refCnt.add(0);
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
        for (String s : dictNames) {
            String [] lines = fileRead.readBibleFile(s+".txt", true);
            if (!lines[0].startsWith("~"))
                Log.w(s, lines[0]);
            if (lines.length < 3)
                Log.w(s+" short", lines[0]);
        }
        dictNames[6000] = "x";
        for (String s : dictNames) {
            int idx = Collections.binarySearch(refKey, s);
            if (idx < 0) {
                Log.w("xross", "not reffered dic.txt " + s);
            } else
                refCnt.set(idx, refCnt.get(idx) + 1);
        }
        Log.w("check","");
        Log.w("check"," Check merged");
        for (int i = 0; i < refKey.size(); i++) {
            if (refCnt.get(i) == 0) {
                Log.w("check","error "+ refKey.get(i));
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