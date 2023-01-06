package com.urrecliner.dicgen;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MakeRef {
    static String blank = "  "; //전각 스페이스


    void make (Context context, File refFolder, String[] sName) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.ref_bible);
        BufferedReader refFile = new BufferedReader(new InputStreamReader(inputStream));

        String refLine = null;

        try {
            refLine = refFile.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            String [] w = refLine.split(",");
            String s = "";
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < w.length; i++) {
                String ref = w[i];
                int pos = ref.indexOf(" ");
                if (pos < 0) {
                    Log.w("x",refLine);
                    break;
                }
                String sn = ref.substring(0, pos);
                int bib = 0, chap, ver;
                for (int j = 1; j < 67; j++) {
                    if (sn.equals(sName[j])) {
                        bib = j;
                        break;
                    }
                }
                if (bib == 0) {
                    Log.e("bib Err "+ref, refLine);
                    break;
                }
                String chapVer = ref.substring(pos+1);  // ver ~ ver
                String chapVer0;    // unique
                pos = chapVer.indexOf("-");
                if (pos < 0)
                    chapVer0 = chapVer;
                else
                    chapVer0 = chapVer.substring(0,pos);
                pos = chapVer0.indexOf(":");
                if (pos < 0) {
                    Log.e(ref+" chapVer0 Err "+w[i], refLine);
                    chap = Integer.parseInt(chapVer0);
                    ver = 1;
                } else {
                    chap = Integer.parseInt(chapVer0.substring(0, pos));
                    ver = Integer.parseInt(chapVer0.substring(pos + 1));
                }
                sb.append(ref).append(",")
                        .append(bib).append(",").append(chap).append(",").append(ver)
                        .append(";");
            }

            String fName = "_인용성구_" + w[0].trim();

            File file = new File(refFolder, fName+".txt");
            file.delete();
//  창 2:18-24,1,2,18;창 24:48-60,1,24,48;민 6:24-26,4,6,24;
            MainActivity.append2File(file, sb.toString());

            try {
                refLine = refFile.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (refLine == null)
                break;
        }
        Log.w("makeRef", "make 인용구 complete");
    }

}