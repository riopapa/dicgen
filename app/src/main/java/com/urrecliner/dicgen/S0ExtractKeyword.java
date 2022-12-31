package com.urrecliner.dicgen;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class S0ExtractKeyword {

    /*
    *   dic0_bible 로부터 [_사전_] 데이터를 추출해서 dic1_extracted.txt 로 추출
    *   작업된 text file로 부터 dic1_extracted.txt 로 복사되어야 함
     */

    int count;
    File keyFile;
    List<String> extracted = new ArrayList<>();

    public List<String> extract(Context context, File kf) {

        this.keyFile = kf;
//        46  14:23 그러므로 온 [_교회_]가 함께 모여 다 방언으로 말하면 알지 못하는 자들이나 믿지 아니하는 자들이 들어와서 너희를 미쳤다 하지 아니하겠느냐[_$44#2:13_]
        boolean none = keyFile.delete();


        BufferedReader bfRead = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.dic0_bible)));
        count = 0;
        String line = null;
        while (true){
            try {
                if ((line = bfRead.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            extract_Keyword(line);
        }
        Log.w("extract","completed extracted() = "+extracted.size());
        return extracted;
    }

    void extract_Keyword(String line) {
//        Log.w("src",line);
        int pos1 = -1, pos2;
        if (line.contains("bib"))
            Log.w("Extract", line);
        if (!line.contains("[_"))
            return;
        String keyword = "";
        String[] strs = line.substring(0, 16).split(" ");
        int bible = Integer.parseInt(strs[1]) + 100;
        String [] cv;
        if (!strs[2].equals(""))
            cv = strs[2].split(":");
        else
            cv = strs[3].split(":");
        int chap = Integer.parseInt(cv[0]) + 100;
        int ver = Integer.parseInt(cv[1]) + 100;
        String bibleStr = "," +bible+","+chap+","+ver;
        while (true) {
            pos1 = line.indexOf("[_");
            if (pos1 < 0)
                return;
            pos2 = line.indexOf("_]");
            if (pos2 < 0) {
                Log.w("Error", "_] not found " + line);
                return;
            }
            try {
                keyword = line.substring(pos1 + 2, pos2);
                if (!keyword.startsWith("$")) {
                    String keyResult = keyword + bibleStr;
//                Log.w("key", keyResult);
//                    MainActivity.append2File(keyFile, keyResult);
                    extracted.add(keyResult);
                    count++;
                }
            } catch (Exception e) {
                Log.w("exception "+pos1+" "+pos2, line);
            }
            line = line.substring(pos2 + 2);
        }
    }

}