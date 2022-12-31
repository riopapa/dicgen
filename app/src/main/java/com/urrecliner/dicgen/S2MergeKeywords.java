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

    List<MainActivity.Keyword> keywords;
    List<MainActivity.KeyRef> keyRefs;
    List<MainActivity.bcv> bcvs;

    // dic2_sorted 에서 같은 키는 장절을 합해서 하나로 합쳐 dic3_merged.txt 로
    // 또 keyRef.json 으로 write
    void merge(Context context, File mergedFile, File jsonFile) {
        boolean none = mergedFile.delete();
        keywords = new ArrayList<>();
        Log.w("merge","reading start");
        InputStream inputStream = context.getResources().openRawResource(R.raw.dic2_sorted);        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String [] ss = line.split(",");
        String sv = ss[0].trim();
        String svb = ss[1], svc = ss[2], svv = ss[3];
        keyRefs = new ArrayList<>();
        bcvs = new ArrayList<>();
        bcvs.add(new MainActivity.bcv(Integer.parseInt(ss[1]),
                Integer.parseInt(ss[2]), Integer.parseInt(ss[3])));
        while (line != null && line.length() > 5) {
            ss = line.split(",");
            ss[0] = ss[0].trim();   // remove trailing blank
            if (sv.equals(ss[0])) {
                if (!(svb.equals(ss[1]) && svc.equals(ss[2]) && svv.equals(ss[3]))) {
                    bcvs.add(new MainActivity.bcv(Integer.parseInt(ss[1])-100,
                            Integer.parseInt(ss[2])-100, Integer.parseInt(ss[3])-100));
                    svb = ss[1]; svc = ss[2]; svv = ss[3];
                }
            } else {
                keyRefs.add(new MainActivity.KeyRef(sv, bcvs));
//                Log.w("m", sv+" size="+bibleVers.size());
                sv = ss[0];
                svb = ss[1]; svc = ss[2]; svv = ss[3];
                bcvs = new ArrayList<>();
                bcvs.add(new MainActivity.bcv(Integer.parseInt(ss[1])-100,
                        Integer.parseInt(ss[2])-100, Integer.parseInt(ss[3])-100));
            }
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        keyRefs.add(new MainActivity.KeyRef(sv, bcvs));
        Log.w("merge ","merge complete "+ keyRefs.size());

        for (int j = 0; j < keyRefs.size(); j++) {
            MainActivity.KeyRef mg = keyRefs.get(j);
            String s = mg.key + ";";
            for (int k = 0; k < mg.bcvs.size(); k++) {
                MainActivity.bcv bv = mg.bcvs.get(k);
                s += bv.b +","+bv.c +","+bv.v +";";
            }
            MainActivity.append2File(mergedFile, s);
        }
        Log.w("merge"," mergedFile complete");
        none = jsonFile.delete();
        Gson gson = new Gson();
        String json = gson.toJson(keyRefs);
        MainActivity.append2File(jsonFile, json);
        Log.w("merge"," merged JSON file complete");
    }

}