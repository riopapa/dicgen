package com.urrecliner.dicgen;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class MakeBible {

    void make (Context context, File bibFolder) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.dic0_bible);
        BufferedReader bibleFile = new BufferedReader(new InputStreamReader(inputStream));
        InputStream agpStream = context.getResources().openRawResource(R.raw.agp_161016);
        BufferedReader agpFile = new BufferedReader(new InputStreamReader(agpStream));
        InputStream cevStream = context.getResources().openRawResource(R.raw.cev_161016);
        BufferedReader cevFile = new BufferedReader(new InputStreamReader(cevStream));
        String bibLine = null, agpLine = null, cevLine = null, oLine = null;

        for (int b = 1; b < 67; b++) {
            try {
                Files.walk(new File(bibFolder,""+b).toPath())
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                e.printStackTrace();
            }

//            try {
//                FileUtils.deleteDirectory(dir);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

        for (int b = 1; b < 67; b++) {
            File file = new File(bibFolder,""+b);
            file.mkdir();
//            try {
//                FileUtils.deleteDirectory(dir);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

        try {
            bibLine = bibleFile.readLine();
            agpLine = agpFile.readLine();
            cevLine = cevFile.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }

        int bibNbr = 0, chNbr, vNbr, pos1, pos2;
        while (true) {
//  1  1:1 {?????? ??????}[_??????_]??? [_?????????_]??? [_??????_]??? [_??????_]????????????[_$43#1:3_][_$58#1:10_]
// 01 1:1 In the beginning God created the heavens and the earth.
// 01 1:1 ????????? ??????????????? ????????? ?????? ?????????????????????.
// {?????? ??????}[_??????_]??? [_?????????_]??? [_??????_]??? [_??????_]????????????[_$43#1:3_][_$58#1:10_]`a????????? ??????????????? ????????? ?????? ?????????????????????.`cIn the beginning God created the heavens and the earth.
            if (bibLine.startsWith("bib")) {
                Log.w("make", "make bible "+bibLine);
//            String str = bibLine.substring(4,7).trim();
                bibNbr = Integer.parseInt(bibLine.substring(4,7).trim());
            } else {
                bibLine = bibLine.trim();
                pos1 = agpLine.indexOf(":");
                String str = agpLine.substring(3,pos1).trim();
                chNbr = Integer.parseInt(agpLine.substring(3, pos1).trim());
                pos2 = agpLine.indexOf(" ", pos1);
                vNbr = Integer.parseInt(agpLine.substring(pos1 + 1, pos2));
                if (bibLine.length() < pos2+1) {
                    oLine = "??????" +
                            "`a" + agpLine.substring(pos2 + 1) +
                            "`c" + cevLine.substring(pos2 + 1);
                    Log.e("none "+bibNbr+" "+chNbr+" "+vNbr, oLine);
                } else {
                    oLine = bibLine.substring(pos2 + 1) +
                            "`a" + agpLine.substring(pos2 + 1) +
                            "`c" + cevLine.substring(pos2 + 1);
                }
//            Log.w("bib"+bibNbr+" "+chNbr+" "+vNbr, oLine);
                File file = new File(bibFolder, bibNbr+"/"+chNbr+".txt");
                MainActivity.append2File(file, oLine);
            }

            try {
                bibLine = bibleFile.readLine();
                agpLine = agpFile.readLine();
                cevLine = cevFile.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bibLine == null)
                break;
        }
        Log.w("makeBible", "make bible complete");
    }

}