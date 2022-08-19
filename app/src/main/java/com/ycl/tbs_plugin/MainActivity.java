package com.ycl.tbs_plugin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ycl.tbs.FileDisplayActivity;
import com.ycl.tbs.utils.FileUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path;
                //FileUtil.copyFileFromAssets(getApplicationContext(), "HowToLoadX5Core.doc", path = FileUtil.getTBSFileDir(getApplicationContext()).getPath() + "/HowToLoadX5Core.doc");
                FileDisplayActivity.start(MainActivity.this, "https://download.ttxc.net/word.doc");
                //startActivity(new Intent(MainActivity.this, FileDisplayActivity.class));
            }
        });
    }
}