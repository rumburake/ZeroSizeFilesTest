package com.threecats.zerosizefilestest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.text);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Testing storage...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                testStorages();
            }
        });
    }

    public void testStorages() {
        File[] dirs = getExternalCacheDirs();
        textView.setText("Storages found: " + dirs.length);

        String[] diags = new String[dirs.length];
        for (int i = 0; i < dirs.length; ++i) {
            if (dirs[i] != null) {
                diags[i] = dirs[i].getAbsolutePath();
            } else {
                diags[i] = "vacant";
            }
        }
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, diags);
        ListView l = (ListView)findViewById(R.id.list);
        l.setAdapter(itemsAdapter);

        boolean allpass = true;

        for (int i = 0; i < dirs.length; ++i) {
            if (dirs[i] != null) {
                StringBuilder diag = new StringBuilder();
                boolean pass = testStorage(dirs[i].getAbsolutePath(), diag);
                allpass = allpass && pass;
                diags[i] = diag.toString();
                final ListView fl = l;
                final int fi = i;
                final boolean fp = pass;
                l.post(new Runnable() {
                    @Override
                    public void run() {
                        fl.getChildAt(fi).setBackgroundColor(fp ? 0xFF00FF00 : 0xFFFF0000);
                    }
                });
            }
        }
        textView.append(" - TEST: " + (allpass ? " PASS " : " FAIL "));
        textView.setTextColor(allpass ? 0xFF00FF00 : 0xFFFF0000);
    }

    boolean testWrite(String filePath, StringBuilder diagText, boolean append) {
        FileOutputStream fos;
        File r;
        // some buffer to write
        int buflen = 100;
        byte[] bytes = new byte[buflen];

        try {
            fos = new FileOutputStream(filePath, true);
        } catch (FileNotFoundException e) {
            diagText.append("\n Error creating FileOutputStream: " + e.getMessage());
            return false;
        }

        try {
            fos.write(bytes);
        } catch (IOException e) {
            diagText.append("\n Error writing: " + e.getMessage());
            return false;
        }

        try {
            fos.close();
        } catch (IOException e) {
            diagText.append("\n Error closing : " + e.getMessage());
            return false;
        }

        r = new File(filePath);

        boolean exists = r.exists();
        diagText.append("\n Exists: " + exists + (exists ? " (PASS) " : " (FAIL) "));
        boolean isFile = r.isFile();
        diagText.append("\n Is File: " + isFile + (isFile ? " (PASS) " : " (FAIL) "));
        boolean lengthOK = r.length() == buflen * (append ? 2 : 1);
        diagText.append("\n Size: " + r.length() + (lengthOK ? " (PASS) " : " (FAIL) "));

        return isFile && exists && lengthOK;
    }

    boolean testStorage(String dirPath, StringBuilder diagText) {
        diagText.append("Dir Path: " + dirPath);

        File dir = new File(dirPath);

        // clear all files first
        diagText.append("\nCleaning Dir...");
        for(File oldFile : dir.listFiles()) {
            oldFile.delete();
        }

        String filePath = dirPath + "/" + "testfile";

        boolean pass = true;

        // new file
        diagText.append("\nInitial Write Test...");
        pass = testWrite(filePath, diagText, false) && pass;

        // append file
        diagText.append("\nAppend Write Test...");
        pass = testWrite(filePath, diagText, true) && pass;

        return pass;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
