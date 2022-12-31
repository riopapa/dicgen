package com.urrecliner.dicgen;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
//import org.apache.commons.io.FileUtils;

public class MainActivity extends AppCompatActivity {

    File outFolder, dicFolder, extractFile, sortedFile, mergedFile, jsonFile, bibFolder;
    int count;
    Timer timer;
    TimerTask timerTask;
    TextView tvGo, tvNext, tvPrev;
    static EditText edDicKey;
    Activity activity;

    File[] dicList;
    String[] dictNames;

    static class Keyword {
        String str;
        public Keyword(String str) {
            this.str = str;
        }

    }

    static class KeyRef {
        List<bcv> bcvs;
        String key;
        public KeyRef(String key, List<bcv> bcv) {
            this.key = key;
            this.bcvs = bcv;
        }
    }
    static class bcv {
        int b, c, v;
        public bcv(int b, int c, int v) {
            this.b = b;
            this.c = c;
            this.v = v;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        askPermission();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
        setContentView(R.layout.activity_main);
        outFolder = new File(Environment.getExternalStorageDirectory(), "download/dicKey");
        if (!outFolder.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                outFolder.mkdirs();
            } catch (Exception e) {
                Log.e("Package Folder", outFolder.toString() + "_" + e);
            }
        }
        dicFolder = new File(Environment.getExternalStorageDirectory(), "download/dict");
        bibFolder = new File(Environment.getExternalStorageDirectory(), "download/bible");
        extractFile = new File(outFolder,"dic1_extracted.txt");
        sortedFile = new File(outFolder, "dic2_sorted.txt");
        mergedFile = new File(outFolder, "dic3_merged.txt");
        jsonFile = new File(outFolder, "keyRef.json");

        edDicKey = findViewById(R.id.dictKey);
        tvGo = findViewById(R.id.gogo);
        tvNext = findViewById(R.id.gonext);
        tvPrev = findViewById(R.id.goprev);
        tvGo.setOnClickListener(v -> {
            showDictContents();
        });
        tvNext.setOnClickListener(v -> {
            showDictNext(1);
        });
        tvPrev.setOnClickListener(v -> {
            showDictNext(-1);
        });

//        dicList = dicFolder.listFiles((dir, name) ->
//                (name.endsWith("txt")));
//
//        Log.w("xross", "Dic files "+dicList.length);
//        dictNames = new String[dicList.length];
//        for (int i = 0; i < dicList.length; i++) {
//            String dic = dicList[i].getName();
//            dictNames[i] = dic.substring(0, dic.length()-4);
//        }
//        Arrays.sort(dictNames);


//        new S0ExtractKeyword().extract(getApplicationContext(), extractFile);
//        new S1SortKeywords().sort(getApplicationContext(), sortedFile);
//        new S2MergeKeywords().merge(getApplicationContext(), mergedFile, jsonFile);
        new S8XrossCheck().check(getApplicationContext(), dicFolder, jsonFile);
//        new MakeBible().make(getApplicationContext(), bibFolder);
    }

    private void showDictContents() {
        String inpKey = edDicKey.getText().toString();
        if (inpKey.length() > 0) {
            String [] dicTexts = readBibleFile(inpKey, true);
            String result = TextUtils.join("\n", dicTexts);
            TextView tv = findViewById(R.id.text);
            tv.setText(result);
        }
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run () {
                if (edDicKey.getText().toString().length() == 0) {
                    timer.cancel();
                    Toast.makeText(getApplicationContext(), "timer canceled",
                            Toast.LENGTH_LONG).show();
                }
                else
                    showDictNext(1);
            }
        };
        timer.schedule(timerTask, 5000, 400);
    }

    private void showDictNext(int plusMinus) {
        String inpKey = edDicKey.getText().toString();
        int idx  = Arrays.binarySearch(dictNames, inpKey+".txt");
        if (idx >= 0) {
            inpKey = dictNames[idx+plusMinus];
            final String inpKeyF = inpKey.substring(0, inpKey.length()-4);
            String [] dicTexts = readBibleFile(inpKeyF, true);
            String result = TextUtils.join("\n", dicTexts);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    edDicKey.setText(inpKeyF);
//                    TextView tv = findViewById(R.id.text);
//                    tv.setText((result.length()> 40)? result.substring(0,39): result);
                    Log.w("next ", inpKeyF + "> " + dicTexts[1]);
                    if (dicTexts.length> 2)
                        Log.w("next ", inpKeyF + "> " + dicTexts[2]);
                    else
                        Log.w("next ", inpKeyF + "> only 1 line");
                    if (dicTexts.length> 3)
                        Log.w("next ", inpKeyF + "> " + dicTexts[3]);
                    Log.w("next ", "");

                }
            });
        }
    }

    String[] readBibleFile(String filename, boolean kr) {

        String file2read =  dicFolder+ "/" + filename+".txt";
        String[] lines;
        try {
            lines = readLines(file2read, kr);
            return lines;
        } catch(IOException e) {
//
//            String message = filename+" 이 없거나, 파일읽기가 거부되어 있습니다.";
//            new AlertDialog.Builder(mActivity)
//                    .setMessage(message)
//                    .setPositiveButton("OK", null)
//                    .create().show();
        }
        return null;
    }

    private static String[] readLines(String filename, boolean kr) throws IOException {
        final int BUFFER_SIZE = 81920;
        String code = (kr) ? "EUC-KR": "UTF-8";
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), code),BUFFER_SIZE);

        List<String> lines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();
        return lines.toArray(new String[0]);
    }


    static void append2File(File file, String textLine) {
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    Log.e("file "+file,"File Error");
                }
            }
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(textLine+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) bw.close();
                if (fw != null) fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    // ↓ ↓ ↓ P E R M I S S I O N    RELATED /////// ↓ ↓ ↓ ↓
    ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();

    private void askPermission() {
//        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);
        if (permissionsToRequest.size() != 0) {
            requestPermissions(permissionsToRequest.toArray(new String[0]),
//            requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                    ALL_PERMISSIONS_RESULT);
        }
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList <String> result = new ArrayList<>();
        for (String perm : wanted) if (hasPermission(perm)) result.add(perm);
        return result;
    }
    private boolean hasPermission(String permission) {
        return (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ALL_PERMISSIONS_RESULT) {
            for (String perms : permissionsToRequest) {
                if (hasPermission(perms)) {
                    permissionsRejected.add(perms);
                }
            }
            if (permissionsRejected.size() > 0) {
                if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                    String msg = "These permissions are mandatory for the application. Please allow access.";
                    showDialog(msg);
                }
            } else
                Toast.makeText(getApplicationContext(), "Permissions not granted.", Toast.LENGTH_LONG).show();
        }
    }
    private void showDialog(String msg) {
        showMessageOKCancel(msg,
                (dialog, which) -> requestPermissions(permissionsRejected.toArray(
                        new String[0]), ALL_PERMISSIONS_RESULT));
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

// ↑ ↑ ↑ ↑ P E R M I S S I O N    RELATED /////// ↑ ↑ ↑

}