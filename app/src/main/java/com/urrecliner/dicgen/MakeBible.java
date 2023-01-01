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
    static String blank = "  "; //전각 스페이스
    static String [] fullBibleNames = {blank,"창세기","출애굽기","레위기","민수기","신명기","여호수아","사사기","룻기","사무엘상","사무엘하","열왕기상","열왕기하","역대상","역대하","에스라","느헤미야","에스더","욥기","시편","잠언","전도서","아가","이사야","예레미야","예레미야애가","에스겔","다니엘","호세아","요엘","아모스","오바댜","요나","미가","나훔","하박국","스바냐","학개","스가랴","말라기","마태복음","마가복음","누가복음","요한복음","사도행전","로마서","고린도전서","고린도후서","갈라디아서","에베소서","빌립보서","골로새서","데살로니가전서","데살로니가후서","디모데전서","디모데후서","디도서","빌레몬서","히브리서","야고보서","베드로전서","베드로후서","요한일서","요한이서","요한삼서","유다서","요한계시록",blank};


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
//  1  1:1 {천지 창조}[_태초_]에 [_하나님_]이 [_천지_]를 [_창조_]하시니라[_$43#1:3_][_$58#1:10_]
// 01 1:1 In the beginning God created the heavens and the earth.
// 01 1:1 태초에 하나님께서 하늘과 땅을 창조하셨습니다.
// {천지 창조}[_태초_]에 [_하나님_]이 [_천지_]를 [_창조_]하시니라[_$43#1:3_][_$58#1:10_]`a태초에 하나님께서 하늘과 땅을 창조하셨습니다.`cIn the beginning God created the heavens and the earth.
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
                    oLine = "없음" +
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